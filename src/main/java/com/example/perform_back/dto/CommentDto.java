package com.example.perform_back.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private Date date;
    private Long postId;
    private Long userId;
}
