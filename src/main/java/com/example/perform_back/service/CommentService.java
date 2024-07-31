package com.example.perform_back.service;

import com.example.perform_back.dto.CommentDto;
import com.example.perform_back.entity.Comment;
import com.example.perform_back.entity.Post;
import com.example.perform_back.repository.CommentRepository;
import com.example.perform_back.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    CommentDto commentDto = new CommentDto();

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment createComment(CommentDto commentdto) {
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        Comment newComment = new Comment();
        newComment.setContent(commentdto.getContent());
        newComment.setPost(post);

        return commentRepository.save(newComment);
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    public List<Comment> getAllComments() {
        return this.commentRepository.findAll();
    }
}
