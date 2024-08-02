package com.example.perform_back.controller;

import com.example.perform_back.dto.CommentDto;
import com.example.perform_back.entity.Comment;
import com.example.perform_back.service.CommentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<CommentDto> uploadComment(@PathVariable Long postId, @RequestBody CommentDto commentDto,
                                                 @RequestHeader("Authorization") String accessToken) throws JsonProcessingException {
        CommentDto comment = commentService.createComment(postId, commentDto, accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @Operation(summary = "Post id로 게시글 댓글 목록 가져오기")
    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId,
                                                        @RequestHeader("Authorization") String accessToken) throws JsonProcessingException {
        List<CommentDto> commentDtoList = commentService.findByPostAndUser(postId, accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(commentDtoList);
    }

    @Operation(summary = "Comment id로 댓글 삭제하기")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id, @RequestHeader("Authorization") String accessToken) throws JsonProcessingException {
        commentService.deleteById(id, accessToken);
        return ResponseEntity.status(HttpStatus.OK).body("댓글이 삭제되었습니다.");
    }

}
