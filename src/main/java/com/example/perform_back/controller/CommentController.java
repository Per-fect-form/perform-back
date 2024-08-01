package com.example.perform_back.controller;

import com.example.perform_back.dto.CommentDto;
import com.example.perform_back.entity.Comment;
import com.example.perform_back.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    @Operation(summary = "모든 댓글 가져오기")
    @GetMapping
    public List<Comment> getAllComments(){
        return this.commentService.getAllComments();
    }


    @Operation(summary = "댓글 달기")
    @PostMapping("/{postId}")
    public Comment uploadComment(@PathVariable Long postId, @RequestBody CommentDto commentDto) {
        return commentService.createComment(postId, commentDto);
    }

    @Operation(summary = "id로 댓글 가져오기")
    @GetMapping("/{id}")
    public Comment getComment(@PathVariable Long id) {
        return commentService.findById(id);
    }

    @Operation(summary = "id로 댓글 삭제")
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) { commentService.deleteById(id);}
}
