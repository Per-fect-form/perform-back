package com.example.perform_back.dto;

import lombok.Data;

@Data
public class CommentDto {
    private String content;

    private Long id;
    private Long postId;
}
