package com.example.perform_back.controller;

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
    public List<ReviewPost> getAllReviewPost() {
        return this.reviewPostService.getAllReviewPosts();
    }

    @Operation(summary = "심사 게시글 업로드")
    @PostMapping("/upload")
    public ResponseEntity<ReviewPost> createReviewPost(@RequestPart("reviewPost") ReviewPostDto reviewPostDto,
                                       @RequestPart("files") MultipartFile[] files,
                                       @RequestHeader("Authorization") String accessToken) {
        ReviewPost reviewPost = reviewPostService.createReviewPost(reviewPostDto, files, accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewPost);
    }

    @Operation(summary = "특정 심사 게시글 조회")
    @GetMapping("/{id}")
    public ReviewPost getReviewPostById(@PathVariable Long id) {
        return reviewPostService.getReviewPostById(id);
    }

    @Operation(summary = "특정 심사 게시글 삭제")
    @DeleteMapping("/{id}")
    public void deleteReviewPostById(@PathVariable Long id) {
        reviewPostService.deleteReviewPostById(id);
    }

    @Operation(summary = "제목으로 심사 게시글 조회")
    @GetMapping("/search/{title}")
    public List<ReviewPost> getReviewPostByTitle(@PathVariable String title) {
        return reviewPostService.getReviewPostByTitle(title);
    }

    @Operation(summary = "특정 심사 게시글 수정")
    @PatchMapping("/{id}")
    public ReviewPost updateReviewPostByTitle(@PathVariable Long id,
                                              @RequestPart("reviewPost") ReviewPostDto reviewPostDto,
                                              @RequestPart("files") MultipartFile[] files) {
        return reviewPostService.updateReviewPostById(id, reviewPostDto, files);
    }
}
