package com.example.perform_back.dto;

import com.example.perform_back.entity.Vote;
import lombok.Data;

@Data
public class ReviewPostDto {
    private String title;
    private String content;
}
