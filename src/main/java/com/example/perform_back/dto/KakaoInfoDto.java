package com.example.perform_back.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoInfoDto {
    private Long id;
    private String nickname;
    private String email;
    private String profileImageUrl;

    public KakaoInfoDto(Long id, String nickname, String email, String profileImageUrl) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }
}