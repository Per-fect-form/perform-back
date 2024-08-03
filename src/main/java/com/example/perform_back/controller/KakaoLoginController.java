package com.example.perform_back.controller;

import com.example.perform_back.dto.CommentDto;
import com.example.perform_back.dto.KakaoInfoDto;
import com.example.perform_back.dto.KakaoLoginDto;
import com.example.perform_back.service.KakaoService;
import com.example.perform_back.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping
public class KakaoLoginController {

    @Value("${kakao.restApi_key}")
    private String restApi_key;

    @Value("${kakao.logout_uri}")
    private String logout_uri;

    private final KakaoService kakaoService;
    private final UserService userService;


    @GetMapping("/callback")
    public String backendCallback(@RequestParam("code") String code, HttpSession session, Model model) {

        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        KakaoInfoDto userInfo = kakaoService.getUserInfo(accessToken);

//      User 로그인, 또는 회원가입 로직 추가
        Long id = userInfo.getId();
        String nickname = userInfo.getNickname();
        String profileImageUrl = userInfo.getProfileImageUrl();
        String email = userInfo.getEmail();
        userService.saveUser(id, nickname, profileImageUrl, email);

        System.out.println("AccessToken: " + accessToken);
        System.out.println("Email: " + userInfo.getEmail());

        session.setMaxInactiveInterval(60 * 30);    // 로그인 유지 시간 30분
        session.setAttribute("Authorization", accessToken);    // 세션에 accessToken 저장
        // 로그인 성공 후 리다이렉트 테스트
        String location = "https://kauth.kakao.com/oauth/logout?client_id=" + restApi_key + "&logout_redirect_uri=" + logout_uri;
        model.addAttribute("location", location);
        return "mypage";
    }

    @GetMapping("/auth/kakao/callback")
    @ResponseBody
    public ResponseEntity<KakaoLoginDto> frontCallback(@RequestParam("code") String code) {

        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        KakaoInfoDto userInfo = kakaoService.getUserInfo(accessToken);

//      User 로그인, 또는 회원가입 로직 추가
        Long id = userInfo.getId();
        String nickname = userInfo.getNickname();
        String profileImageUrl = userInfo.getProfileImageUrl();
        String email = userInfo.getEmail();
        userService.saveUser(id, nickname, profileImageUrl, email);

        System.out.println("AccessToken: " + accessToken);
        System.out.println("Email: " + userInfo.getEmail());
        KakaoLoginDto kakaoLoginDto = KakaoLoginDto.builder().accessToken(accessToken).build();
        return ResponseEntity.status(HttpStatus.OK).body(kakaoLoginDto);
    }

    @GetMapping("/logout")
    public String kakaoLogout(HttpSession session) {
        String accessToken = (String) session.getAttribute("Authorization");

        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                Long id =kakaoService.kakaoDisconnect(accessToken);
                System.out.println("반환된 id: "+id);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            session.removeAttribute("Authorization");
            session.invalidate();
        } else {
            System.out.println("accessToken is null");
        }

        return "redirect:/login/page";
    }

}

