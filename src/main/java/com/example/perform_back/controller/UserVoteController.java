package com.example.perform_back.controller;

import com.example.perform_back.service.UserVoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/uservote")
@Tag(name = "UserVote", description = "UserVote API")
public class UserVoteController {

    private final UserVoteService userVoteService;

    public UserVoteController(UserVoteService userVoteService) {
        this.userVoteService = userVoteService;
    }

    @Operation(summary = "심사 게시물에 대한 투표")
    @PostMapping("/{reviewPostId}")
    public void vote(@PathVariable Long reviewPostId, @RequestParam Long userId, @RequestParam String isAgree) {
        userVoteService.vote(reviewPostId, userId, isAgree);
    }
}

