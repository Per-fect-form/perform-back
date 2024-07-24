package com.example.perform_back.controller;

import com.example.perform_back.service.VoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vote")
@Tag(name = "Vote", description = "Vote API")
public class VoteController {

    private final VoteService voteService;


    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PatchMapping("/{voteId}/agree")
    public void agree(@PathVariable Long voteId) {
        voteService.agree(voteId);
    }

    @PatchMapping("/{voteId}/disagree")
    public void disagree(@PathVariable Long voteId) {
        voteService.disagree(voteId);
    }
}
