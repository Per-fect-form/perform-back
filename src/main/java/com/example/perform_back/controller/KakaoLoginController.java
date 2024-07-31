package com.example.perform_back.controller;

import com.example.perform_back.dto.KakaoInfoDto;
import com.example.perform_back.service.KakaoService;
import com.example.perform_back.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {

    @Value("${kakao.restApi_key}")
    private String restApi_key;

    @Value("${kakao.logout_uri}")
    private String logout_uri;

    private final KakaoService kakaoService;
    private final UserService userService;

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code, HttpSession session, Model model) {
        log.info(code);
        String accessToken = null;
        try {
            accessToken = kakaoService.getAccessToken(code);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("엑세스 토큰  " + accessToken);

//        String accessToken = kakaoService.getAccessTokenFromKakao(code);

//        log.info(accessToken);

        KakaoInfoDto kakaoInfoDto = null;
        try {
            kakaoInfoDto = kakaoService.getKakaoInfo(accessToken);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("이메일 확인 " + kakaoInfoDto.getEmail());


        session.setAttribute("loginMember", kakaoInfoDto);
        // session.setMaxInactiveInterval( ) : 세션 타임아웃을 설정하는 메서드
        // 로그인 유지 시간 설정 (1800초 == 30분)
        session.setMaxInactiveInterval(60 * 30);
        // 로그아웃 시 사용할 카카오토큰 추가
        session.setAttribute("kakaoToken", accessToken);

//        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

        userService.saveOrUpdateUser(userInfo.getId(), userInfo.getKakaoAccount().getProfile().getNickName(),
                userInfo.getKakaoAccount().getProfile().getProfileImageUrl(), userInfo.getKakaoAccount().getEmail());
//      User 로그인, 또는 회원가입 로직 추가

//      return new ResponseEntity<>(HttpStatus.OK);

        String location = "https://kauth.kakao.com/oauth/logout?client_id=" + restApi_key + "&logout_redirect_uri=" + logout_uri;
        model.addAttribute("location", location);
        return "mypage";
    }

    @GetMapping("/logout")
    public String kakaoLogout(HttpSession session) {
        String accessToken = (String) session.getAttribute("kakaoToken");

        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                kakaoService.kakaoDisconnect(accessToken);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            session.removeAttribute("kakaoToken");
            session.removeAttribute("loginMember");
            session.invalidate();
        } else {
            System.out.println("accessToken is null");
        }

        return "redirect:/login/page";
    }

}

