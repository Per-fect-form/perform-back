package com.example.perform_back.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private Date date;
    private Long postId;
    private Long userId;

    @Builder
    public CommentDto(Long id, String content, Date date, Long postId, Long userId) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.postId = postId;
        this.userId = userId;
    }
}
