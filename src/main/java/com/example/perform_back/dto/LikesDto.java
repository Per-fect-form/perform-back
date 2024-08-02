package com.example.perform_back.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
public class LikesDto {
    private Long id;
    private Long userId;
    private Long fromUserId;
    private Date date;

    @Builder
    public LikesDto(Long id, Long userId, Long fromUserId, Date date){
        this.id = id;
        this.userId = userId;
        this.fromUserId = fromUserId;
        this.date = date;
    }
}
