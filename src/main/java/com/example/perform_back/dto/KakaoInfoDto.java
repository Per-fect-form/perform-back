package com.example.perform_back.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoInfoDto {
    private Long id;
    private String nickname;
    private String email;

    public KakaoInfoDto(Long id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }
}