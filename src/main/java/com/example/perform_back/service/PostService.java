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
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final AttachmentService attachmentService;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;
    private final KakaoService kakaoService;
    private final UserService userService;

    @Autowired
    public PostService(PostRepository postRepository, AttachmentService attachmentService,
                       CommentRepository commentRepository, LikesRepository likesRepository, KakaoService kakaoService, UserService userService) {
        this.postRepository = postRepository;
        this.attachmentService = attachmentService;
        this.commentRepository = commentRepository;
        this.likesRepository = likesRepository;
        this.kakaoService = kakaoService;
        this.userService = userService;
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public PostDto save(PostDto postDto, MultipartFile[] files, String accessToken) throws JsonProcessingException {
        Post postToSave = convertToPost(postDto, new Post());

        validate(postToSave.getTitle(), postToSave.getContent(), postToSave.getCategory()); //제목이나 내용이나 카테고리가 빈 상태에서 생성 시도

        postToSave = postRepository.save(postToSave);
        if (files != null && files.length > 0) {
            saveMultipartFiles(files, postToSave);
        }

        Long userId = kakaoService.getUserInfo(accessToken).getId();
        User user = userService.findById(userId);

        postToSave.setUser(user);
        postToSave = postRepository.save(postToSave);

        return converToPostDto(postToSave);
    }

    public Post findById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()){
            System.out.println("Post 반환");
            return post.get();
        }
        else{
            System.out.println("Post null");
            throw new RuntimeException("Post not found");
        }
    }

    @Transactional
    public void deleteById(Long id) {
         Optional<Post> post = postRepository.findById(id);
         if(post.isEmpty())
             throw new RuntimeException("Post not found");

         attachmentService.deleteAllByPost(post.get());
         commentRepository.deleteAllByPostId(id);
         likesRepository.deleteAllByPostId(id);
         postRepository.deleteById(id);
    }

    public Post updateById(Long id, PostDto postDto, AttachmentsDto attachmentsDto, MultipartFile[] files) {
        Optional<Post> post = postRepository.findById(id);
        if(post.isEmpty())
            throw new RuntimeException("Post not found");
        Post postToUpdate = convertToPost(postDto, post.get());



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

    public PostDto converToPostDto(Post postToSave) {
        return PostDto.builder()
                .id(postToSave.getId())
                .title(postToSave.getTitle())
                .content(postToSave.getContent())
                .category(postToSave.getCategory())
                .userId(postToSave.getUser().getId())
                .createdDate(postToSave.getCreatedDate())
                .attachments(attachmentService.convertToDto(postToSave.getAttachments()))
                .build();
    }

    private void validate(String title, String content, String category) {
        if(title == null || content == null || category == null ||
            title.isEmpty() || content.isEmpty() || category.isEmpty()) {
            throw new RuntimeException("제목, 내용, 카테고리를 모두 설정해주세요.");
        }
    }

    public List<Post> findByTitle(String title) {
        return postRepository.findByTitleContaining(title);
    }
}
