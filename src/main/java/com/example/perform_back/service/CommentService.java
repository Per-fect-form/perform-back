package com.example.perform_back.service;

import com.example.perform_back.dto.CommentDto;
import com.example.perform_back.entity.Comment;
import com.example.perform_back.entity.Post;
import com.example.perform_back.repository.CommentRepository;

import com.example.perform_back.repository.LikesRepository;
import com.example.perform_back.repository.PostRepository;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final LikesRepository likesRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, PostRepository postRepository,
        LikesRepository likesRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.likesRepository = likesRepository;
    }


    public Comment createComment(Long postId, CommentDto commentDto) {
        Comment newComment = new Comment();
        if (commentDto.getContent() == null || commentDto.getContent().isEmpty()) throw new RuntimeException("댓글을 작성해주세요"); //댓글 내용이 빈 상태로 댓글 생성
        newComment.setContent(commentDto.getContent());
        Post post = postRepository.findById(postId).get();
        newComment.setPost(post);
        newComment.setCreatedDate(new Date());

        return commentRepository.save(newComment);
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
}
