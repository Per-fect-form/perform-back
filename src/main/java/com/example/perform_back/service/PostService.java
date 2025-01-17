package com.example.perform_back.service;

import com.example.perform_back.dto.AttachmentDto;
import com.example.perform_back.dto.AttachmentsDto;
import com.example.perform_back.dto.PostDto;
import com.example.perform_back.entity.Attachment;
import com.example.perform_back.entity.Post;
import com.example.perform_back.entity.User;
import com.example.perform_back.repository.CommentRepository;
import com.example.perform_back.repository.LikesRepository;
import com.example.perform_back.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final AttachmentService attachmentService;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;
    private final UserService userService;
    private final String[] categories = {"back", "chest", "shoulder", "arm", "abs", "lower", "routine", "nutrition"};

    public List<PostDto> findAll() {
        return convertToPostDtoList(postRepository.findAll());
    }

    public PostDto save(PostDto postDto, MultipartFile[] files, String accessToken) {
        Post postToSave = convertToPost(postDto, new Post());

        validate(postToSave.getTitle(), postToSave.getContent(), postToSave.getCategory()); //제목이나 내용이나 카테고리가 빈 상태에서 생성 시도
        User user = userService.findByAccessToken(accessToken); // 게시글 저장하기 전에 accessToken 먼저 검사

        postToSave = postRepository.save(postToSave);
        if (files != null && files.length > 0) {
            saveMultipartFiles(files, postToSave);
        }

        postToSave.setUser(user);
        postToSave = postRepository.save(postToSave);

        return converToPostDto(postToSave, user);
    }

    public Post findById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent())
            return post.get();
        else
            throw new RuntimeException("존재하지 않는 게시글 ID 입니다.");
    }

    public PostDto getPost(Long id, String accessToken) {
        Post post = findById(id);
        if(accessToken != null){
            User user = userService.findByAccessToken(accessToken);
            return converToPostDto(post,user);
        } else
            return converToPostDto(post, null);
    }

    @Transactional
    public void deleteById(Long id, String accessToken) {
        Post post = findById(id);
        User user = userService.findByAccessToken(accessToken);

        if(!post.getUser().getId().equals(user.getId()))
            throw new RuntimeException("삭제 권한이 없습니다.");

        attachmentService.deleteAllByPost(post);
        commentRepository.deleteAllByPostId(id);
        likesRepository.deleteAllByPostId(id);
        postRepository.deleteById(id);
    }

    public PostDto updateById(Long id, PostDto postDto, AttachmentsDto attachmentsDto, MultipartFile[] files, String accessToken) {
        Post post = findById(id);
        User user = userService.findByAccessToken(accessToken);

        if(!post.getUser().getId().equals(user.getId()))
            throw new RuntimeException("수정 권한이 없습니다.");

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

        postToUpdate = postRepository.save(postToUpdate);
        return converToPostDto(postToUpdate, user);
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
            postDtoList.add(converToPostDto(post, null));
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

    public List<PostDto> findMyPosts(String accessToken) {
        User user = userService.findByAccessToken(accessToken);
        return convertToPostDtoList(postRepository.findByUser(user));
    }

    public List<PostDto> findByCategory(String category) {
        if(isValidCategory(category))
            return convertToPostDtoList(postRepository.findByCategory(category));
        else
            throw new RuntimeException("올바르지 않은 카테고리입니다.");
    }

    private boolean isValidCategory(String category) {
        return Arrays.asList(categories).contains(category);
    }

    public PostDto converToPostDto(Post post, User user) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .userId(post.getUser().getId())
                .username(post.getUser().getUsername())
                .createdDate(post.getCreatedDate())
                .attachments(convertToAttchmentDtoList(post.getAttachments()))
                .likesNum(likesRepository.findByPost(post).size())
                .liked(user != null && likesRepository.existsByPostAndUser(post, user))
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
