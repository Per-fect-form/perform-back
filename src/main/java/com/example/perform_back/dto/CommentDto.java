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
    private int likesNum;
    private boolean liked;
    private boolean isExpert;

    @Builder
    public CommentDto(Long id, String content, Date date, Long postId, Long userId, int likesNum, boolean liked, boolean isExpert) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.postId = postId;
        this.userId = userId;
        this.likesNum = likesNum;
        this.liked = liked;
        this.isExpert = isExpert;
    }
}
