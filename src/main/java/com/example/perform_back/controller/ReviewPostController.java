package com.example.perform_back.controller;

import com.example.perform_back.entity.ReviewPost;
import com.example.perform_back.service.ReviewPostService;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviewpost")
@Tag(name = "Reviewpost", description = "Reviewpost API")
public class ReviewPostController {

    private final ReviewPostService reviewPostService;

    @Autowired
    public ReviewPostController(ReviewPostService reviewPostService) {
        this.reviewPostService = reviewPostService;
    }

    @PostMapping("/upload")
    public void createReviewPost(@RequestParam("title") String title,
        @RequestParam("content") String content) {
        reviewPostService.createReviewPost(title, content);
    }

    @GetMapping("/{id}")
    public ReviewPost getReviewPostById(@PathVariable Long id) {
        return reviewPostService.getReviewPostById(id);
    }

}
