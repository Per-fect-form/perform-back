package com.example.perform_back.service;

import com.example.perform_back.dto.CommentDto;
import com.example.perform_back.entity.Comment;
import com.example.perform_back.entity.Post;
import com.example.perform_back.entity.User;
import com.example.perform_back.repository.CommentRepository;

import com.example.perform_back.repository.LikesRepository;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;
    private final UserService userService;
    private final PostService postService;

    @Autowired
    public CommentService(CommentRepository commentRepository, LikesRepository likesRepository,UserService userService, PostService postService) {
        this.commentRepository = commentRepository;
        this.likesRepository = likesRepository;
        this.userService = userService;
        this.postService = postService;
    }


    public CommentDto createComment(Long postId, CommentDto commentDto, String accessToken) throws JsonProcessingException {
        if (commentDto.getContent() == null || commentDto.getContent().isEmpty())
            throw new RuntimeException("댓글을 작성해주세요"); //댓글 내용이 빈 상태로 댓글 생성

        User user = userService.findByAccessToken(accessToken);
        Post post = postService.findById(postId);

        Comment newComment = new Comment();
        newComment.setContent(commentDto.getContent());
        newComment.setUser(user);
        newComment.setPost(post);
        newComment.setCreatedDate(new Date());
        newComment = commentRepository.save(newComment);

        return converToCommentDto(newComment);
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    public List<Comment> getAllComments() {
        return this.commentRepository.findAll();
    }

    public void deleteById(Long id) {
        commentRepository.deleteById(id);
        likesRepository.deleteByCommentId(id);
    }

    public CommentDto converToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .date(new Date())
                .userId(comment.getUser().getId())
                .postId(comment.getPost().getId())
                .build();
    }

}
