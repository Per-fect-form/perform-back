package com.example.perform_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class KakaoLoginDto {

    public String accessToken;
    public String refreshToken;
}