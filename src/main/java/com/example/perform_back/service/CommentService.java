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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;
    private final UserService userService;
    private final PostService postService;

    public CommentDto createComment(Long postId, CommentDto commentDto, String accessToken) {
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

        return converToCommentDto(newComment, user);
    }

    public Comment findById(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if(comment.isPresent())
            return comment.get();
        else
            throw new RuntimeException("해당 댓글이 존재하지 않습니다.");
    }

    public List<Comment> getAllComments() {
        return this.commentRepository.findAll();
    }

    public void deleteById(Long id, String accessToken) {
        Comment comment = findById(id);
        User user = userService.findByAccessToken(accessToken);

        if(!comment.getUser().getId().equals(user.getId()))
            throw new RuntimeException("삭제 권한이 없습니다.");

        likesRepository.deleteByCommentId(id);
        commentRepository.deleteById(id);
    }

    public List<CommentDto> findByPostAndUser(Long postId, String accessToken) {
        User user = userService.findByAccessToken(accessToken);
        List<Comment> comments = commentRepository.findByPostId(postId);
        return convertToCommentDtoList(comments,user);
    }

    public CommentDto converToCommentDto(Comment comment, User user) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .date(new Date())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .postId(comment.getPost().getId())
                .likesNum(likesRepository.findByComment(comment).size())
                .liked(likesRepository.existsByCommentAndUser(comment, user))
                .isExpert(user.isExpert())
                .build();
    }

    private List<CommentDto> convertToCommentDtoList(List<Comment> comments, User user) {
        return comments.stream()
            .map(comment -> converToCommentDto(comment, user))
            .collect(Collectors.toList());
    }

}
