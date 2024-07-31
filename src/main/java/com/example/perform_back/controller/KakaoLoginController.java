package com.example.perform_back.controller;

import com.example.perform_back.dto.KakaoUserInfoResponseDto;
import com.example.perform_back.service.KakaoService;
import com.example.perform_back.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final UserService userService;

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        log.info(code);
        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        log.info(accessToken);

        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

        userService.saveOrUpdateUser(userInfo.getId(), userInfo.getKakaoAccount().getProfile().getNickName(),
                userInfo.getKakaoAccount().getProfile().getProfileImageUrl(), userInfo.getKakaoAccount().getEmail());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
