package com.example.perform_back.controller;

import com.example.perform_back.dto.PostDto;
import com.example.perform_back.dto.ReviewPostDto;
import com.example.perform_back.entity.ReviewPost;
import com.example.perform_back.service.ReviewPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reviewpost")
@Tag(name = "Reviewpost", description = "Reviewpost API")
public class ReviewPostController {

    private final ReviewPostService reviewPostService;

    @Autowired
    public ReviewPostController(ReviewPostService reviewPostService) {
        this.reviewPostService = reviewPostService;
    }

    @Operation(summary = "전체 심사 게시글 조회")
    @GetMapping
    public ResponseEntity<List<ReviewPostDto>> getAllReviewPost() {
        List<ReviewPostDto> reviewPostDtoList = reviewPostService.getAllReviewPosts();
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewPostDtoList);
    }

    @Operation(summary = "심사 게시글 업로드")
    @PostMapping("/upload")
    public ResponseEntity<ReviewPostDto> createReviewPost(@RequestPart("reviewPost") ReviewPostDto reviewPostDto,
                                       @RequestPart("files") MultipartFile[] files,
                                       @RequestHeader("Authorization") String accessToken) {
        ReviewPostDto reviewPost = reviewPostService.createReviewPost(reviewPostDto, files, accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewPost);
    }

    @Operation(summary = "특정 심사 게시글 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ReviewPostDto> getReviewPostById(@PathVariable Long id) {
        ReviewPostDto reviewPost = reviewPostService.getReviewPostById(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewPost);
    }

    @Operation(summary = "특정 심사 게시글 삭제")
    @DeleteMapping("/{id}")
    public void deleteReviewPostById(@PathVariable Long id) {
        reviewPostService.deleteReviewPostById(id);
    }

    @Operation(summary = "제목으로 심사 게시글 조회")
    @GetMapping("/search/{title}")
    public ResponseEntity<List<ReviewPostDto>> getReviewPostByTitle(@PathVariable String title) {
        List<ReviewPostDto> reviewPostDtoList = reviewPostService.getReviewPostByTitle(title);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewPostDtoList);
    }

    @Operation(summary = "특정 심사 게시글 수정")
    @PatchMapping("/{id}")
    public ResponseEntity<ReviewPostDto> updateReviewPostByTitle(@PathVariable Long id,
                                              @RequestPart("reviewPost") ReviewPostDto reviewPostDto,
                                              @RequestPart("files") MultipartFile[] files) {
        ReviewPostDto reviewPost = reviewPostService.updateReviewPostById(id, reviewPostDto, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewPost);
    }

    @Operation(summary = "내 심사게시글 조회")
    @GetMapping("/my")
    public ResponseEntity<List<ReviewPostDto>> getMyReviewPost(@RequestHeader("Authorization") String accessToken) {
        List<ReviewPostDto> reviewPostDtoList = reviewPostService.findMyReviewPosts(accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(reviewPostDtoList);
    }
}
