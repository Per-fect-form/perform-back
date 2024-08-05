package com.example.perform_back.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.DeleteMapping;

@Data
public class ExpertDto {
    private Long id;
    private String username;
    private String profile;
    private String snsUrl;

    @Builder
    public ExpertDto(Long id, String username, String profile, String snsUrl) {
        this.id = id;
        this.username = username;
        this.profile = profile;
        this.snsUrl = snsUrl;
    }
}
