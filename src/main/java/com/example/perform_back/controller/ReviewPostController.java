package com.example.perform_back.controller;

import com.example.perform_back.dto.ReviewPostDto;
import com.example.perform_back.entity.ReviewPost;
import com.example.perform_back.service.ReviewPostService;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    public ReviewPost createReviewPost(@RequestPart("reviewPost") ReviewPostDto reviewPost,
                                       @RequestPart("file") MultipartFile[] files) throws Exception {
        return this.reviewPostService.createReviewPost(reviewPost, files);
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
    @GetMapping("/search/{id}")
    public List<ReviewPost> getReviewPostByTitle(@PathVariable String title) {
        return reviewPostService.getReviewPostByTitle(title);
    }

    @Operation(summary = "특정 심사 게시글 수정")
    @PatchMapping("/{id}")
    public ReviewPost updateReviewPostByTitle(@PathVariable Long id,
                                              @RequestPart("reviewPost") ReviewPostDto reviewPost,
                                              @RequestPart("file") MultipartFile[] files) {
        return reviewPostService.updateReviewPostById(id, reviewPost, files);
    }
}
