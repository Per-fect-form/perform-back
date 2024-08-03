package com.example.perform_back.controller;

import com.example.perform_back.dto.LikesDto;
import com.example.perform_back.entity.Likes;
import com.example.perform_back.service.LikesService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    @PostMapping("/post/{postId}")
    public ResponseEntity<LikesDto> likesPost(@PathVariable Long postId,
                                              @RequestHeader("Authorization") String accessToken) throws JsonProcessingException {
        LikesDto likesDto =  likesService.likesPost(postId, accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(likesDto);
    }

    @Operation(summary = "심사 게시글 공감 누르기")
    @PostMapping("/reviewpost/{reviewpostId}")
    public ResponseEntity<LikesDto> likesReviewPost(@PathVariable Long reviewPostId,
                                                    @RequestHeader("Authorization") String accessToken) throws JsonProcessingException {
        LikesDto likesDto = likesService.likesReviewPost(reviewPostId, accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(likesDto);
    }

    @Operation(summary = "댓글 공감 누르기")
    @PostMapping("/comment/{commentId}")
    public ResponseEntity<LikesDto> likesComment(@PathVariable Long commentId,
                                                 @RequestHeader("Authorization") String accessToken) throws JsonProcessingException {
        LikesDto likesDto = likesService.likesComment(commentId, accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(likesDto);
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
