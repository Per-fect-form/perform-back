package com.example.perform_back.controller;

import com.example.perform_back.entity.Likes;
import com.example.perform_back.service.LikesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@Tag(name = "Likes", description = "Likes API")
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;

    @Operation(summary = "게시글 공감 누르기")
    @PostMapping("/post/{postId}/{username}")
    public ResponseEntity<Likes> likesPost(@PathVariable Long postId,@PathVariable String username) {
        Likes likes =  likesService.likesPost(postId, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(likes);
    }

    @Operation(summary = "심사 게시글 공감 누르기")
    @PostMapping("/reviewpost/{reviewpostId}/{username}")
    public ResponseEntity<Likes> likesReviewPost(@PathVariable Long reviewPostId,@PathVariable String username) {
        Likes likes = likesService.likesReviewPost(reviewPostId, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(likes);
    }

    @Operation(summary = "댓글 공감 누르기")
    @PostMapping("/comment/{commentId}/{username}")
    public ResponseEntity<Likes> likesComment(@PathVariable Long commentId, @PathVariable String username) {
        Likes likes = likesService.likesComment(commentId, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(likes);
    }


    @Operation(summary = "게시글 공감 내역 조회")
    @GetMapping("/post/{postId}")
    public List<Likes> getPostLikes(@PathVariable Long postId) {
        return likesService.findPostLikes(postId);
    }

    @Operation(summary = "심사 게시글 공감 내역 조회")
    @GetMapping("/reviewpost/{reviewPostId}")
    public List<Likes> getReviewPostLikes(@PathVariable Long reviewPostId) {
        return likesService.findReviewPostLikes(reviewPostId);
    }

    @Operation(summary = "댓글 공감 내역 조회")
    @GetMapping("/comment/{commentId}")
    public List<Likes> getCommentLikes(@PathVariable Long commentId) {
        return likesService.findCommentLikes(commentId);
    }

    @Operation(summary = "공감 삭제")
    @GetMapping("/{id}")
    public void deleteLikes(@PathVariable Long id) {
        likesService.deleteById(id);
    }
}
