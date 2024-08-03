package com.example.perform_back.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String profile;
    private String email;
    private String snsUrl;
    private boolean isExpert;
    private boolean ad;

    @Builder
    public UserDto(Long id, String username, String profile, String email, String snsUrl, boolean isExpert, boolean ad){
        this.id = id;
        this.username = username;
        this.profile = profile;
        this.email = email;
        this.snsUrl = snsUrl;
        this.isExpert = isExpert;
        this.ad = ad;
    }
}
