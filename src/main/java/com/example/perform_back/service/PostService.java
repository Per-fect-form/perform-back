package com.example.perform_back.service;

import com.example.perform_back.dto.AttachmentDto;
import com.example.perform_back.dto.AttachmentsDto;
import com.example.perform_back.dto.CommentDto;
import com.example.perform_back.dto.PostDto;
import com.example.perform_back.entity.Attachment;
import com.example.perform_back.entity.Comment;
import com.example.perform_back.entity.Post;
import com.example.perform_back.entity.User;
import com.example.perform_back.repository.CommentRepository;
import com.example.perform_back.repository.LikesRepository;
import com.example.perform_back.repository.PostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final AttachmentService attachmentService;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;
    private final UserService userService;

    public List<PostDto> findAll() {
        return convertToPostDtoList(postRepository.findAll());
    }

    public PostDto save(PostDto postDto, MultipartFile[] files, String accessToken) throws JsonProcessingException {
        Post postToSave = convertToPost(postDto, new Post());

        validate(postToSave.getTitle(), postToSave.getContent(), postToSave.getCategory()); //제목이나 내용이나 카테고리가 빈 상태에서 생성 시도
        User user = userService.findByAccessToken(accessToken); // 게시글 저장하기 전에 accessToken 먼저 검사

        postToSave = postRepository.save(postToSave);
        if (files != null && files.length > 0) {
            saveMultipartFiles(files, postToSave);
        }

        postToSave.setUser(user);
        postToSave = postRepository.save(postToSave);

        return converToPostDto(postToSave);
    }

    public Post findById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent())
            return post.get();
        else
            throw new RuntimeException("존재하지 않는 게시글 ID 입니다.");
    }

    @Transactional
    public void deleteById(Long id) {
         Post post = findById(id);

         attachmentService.deleteAllByPost(post);
         commentRepository.deleteAllByPostId(id);
         likesRepository.deleteAllByPostId(id);
         postRepository.deleteById(id);
    }

    public Post updateById(Long id, PostDto postDto, AttachmentsDto attachmentsDto, MultipartFile[] files) {
        Post post = findById(id);
        Post postToUpdate = convertToPost(postDto, post);

        if (postToUpdate.getComments().size() != 0) throw new RuntimeException("댓글이 달린 게시물은 수정할 수 없습니다."); //댓글이 달린 후 수정 시도
        validate(postToUpdate.getTitle(), postToUpdate.getContent(), postToUpdate.getCategory()); //제목이나 내용이나 카테고리가 빈 상태에서 수정 시도

        if(attachmentsDto != null) {
            for(AttachmentDto attachment : attachmentsDto.getAttachments()) {
                Attachment foundAttachment = attachmentService.findById(attachment.getId());
                attachmentService.deleteById(foundAttachment);
            }
        }
        if (files != null && files.length > 0) {
            saveMultipartFiles(files, postToUpdate);
        }
        return postRepository.save(postToUpdate);
    }

    private void saveMultipartFiles(MultipartFile[] files, Post post) {
        for (MultipartFile file : files) {
            attachmentService.savePostWithAttachment(post, file);
        }
        post.setAttachments(attachmentService.findByPost(post));
    }

    private static Post convertToPost(PostDto postDto, Post post) {
        post.setTitle(postDto.getTitle());
        post.setCategory(postDto.getCategory());
        post.setContent(postDto.getContent());
        post.setCreatedDate(new Date());
        return post;
    }

    public List<PostDto> convertToPostDtoList(List<Post> posts) {
        List<PostDto> postDtoList = new ArrayList<>();
        for (Post post : posts){
            postDtoList.add(converToPostDto(post));
        }
        return postDtoList;
    }

    private void validate(String title, String content, String category) {
        if(title == null || content == null || category == null ||
            title.isEmpty() || content.isEmpty() || category.isEmpty()) {
            throw new RuntimeException("제목, 내용, 카테고리를 모두 설정해주세요.");
        }
    }

    public List<PostDto> findByTitle(String title) {
        return convertToPostDtoList(postRepository.findByTitleContaining(title));
    }

    public PostDto converToPostDto(Post postToSave) {
        return PostDto.builder()
                .id(postToSave.getId())
                .title(postToSave.getTitle())
                .content(postToSave.getContent())
                .category(postToSave.getCategory())
                .userId(postToSave.getUser().getId())
                .createdDate(postToSave.getCreatedDate())
                .attachments(convertToAttchmentDtoList(postToSave.getAttachments()))
                .likesNum(likesRepository.findByPost(postToSave).size())
                .build();
    }

    private List<AttachmentDto> convertToAttchmentDtoList(List<Attachment> attachments) {
        if(attachments == null)
            return null;

        else return attachments.stream()
                .map(attachment -> {
                    AttachmentDto dto = new AttachmentDto();
                    dto.setId(attachment.getId());
                    dto.setFilePath(attachment.getPath());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
