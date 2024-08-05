package com.example.perform_back.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ReviewPostDto {
    private Long id;
    private Long userId;
    private String username;
    private String title;
    private String content;
    private Long voteId;
    private Date createdDate;
    private String reviewStatus;
    private List<AttachmentDto> attachments;

    @Builder
    public ReviewPostDto(Long id, Long userId, String username, String title, String content, Long voteId, Date createdDate, String reviewStatus, List<AttachmentDto> attachments) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.title = title;
        this.content = content;
        this.voteId = voteId;
        this.createdDate = createdDate;
        this.reviewStatus = reviewStatus;
        this.attachments = attachments;
    }
}
