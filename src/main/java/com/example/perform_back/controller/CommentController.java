package com.example.perform_back.controller;

import com.example.perform_back.dto.CommentDto;
import com.example.perform_back.entity.Comment;
import com.example.perform_back.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private CommentService commentService;

    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    @GetMapping
    public List<Comment> getAllComments(){
        return this.commentService.getAllComments();
    }

    @PostMapping//("/upload")
    public String uploadComment(@RequestBody CommentDto commentDto) {
        Comment comment = this.commentService.createComment(commentDto);
        return comment.getContent();
        //여기서 포스트의 아이디도 받아서 post id 를 comment
        //
    }

    @GetMapping("/{id}")
    public String getComment(@PathVariable Long id) {
        Comment comment = commentService.findById(id);
        if (comment == null) {
            throw new RuntimeException("comment");
        }
        return comment.getContent();//id를 통해 내용을 가져오는게 맞는지?
    }
}
