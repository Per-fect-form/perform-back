package com.example.perform_back.service;

import com.example.perform_back.dto.KakaoInfoDto;
import com.example.perform_back.dto.KakaoUserInfoResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final UserService userService;
    @Value("${kakao.client_id}")
    private String clientId;
    @Value("${kakao.client_secret}")
    private String clientSecret;
    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    // 토큰 발급
    public String getAccessTokenFromKakao(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        body.add("client_secret", clientSecret);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String tokenType = jsonNode.get("token_type").asText();
        String accessToken = jsonNode.get("access_token").asText();
        int expiresIn = jsonNode.get("expires_in").asInt();
        String refreshToken = jsonNode.get("refresh_token").asText();
        int refreshTokenExpiresIn = jsonNode.get("refresh_token_expires_in").asInt();

        System.out.println("tokenType: " + tokenType);
        System.out.println("accessToken: " + accessToken);
        System.out.println("expiresIn: " + expiresIn);
        System.out.println("refreshToken: " + refreshToken);
        System.out.println("refreshTokenExpiresIn: " + refreshTokenExpiresIn);

        return accessToken;
    }

    //사용자 정보 가져오기
    public KakaoInfoDto getUserInfo(String accessToken) throws JsonProcessingException {
        KakaoInfoDto userInfo = null;

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        // responseBody에 있는 정보 꺼내기
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String nickname = jsonNode.path("kakao_account").path("profile").path("nickname").asText();
        String profileImageUrl = jsonNode.path("kakao_account").path("profile").path("profile_image_url").asText();

        System.out.println("User ID : " + id);
        System.out.println("NickName : " + nickname);
        System.out.println("ProfileImageUrl : " + profileImageUrl);
        System.out.println("Email : " + email);

        userInfo = new KakaoInfoDto(id, nickname, email, profileImageUrl);
        return userInfo;
    }

    public Long kakaoDisconnect(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoLogoutRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response;

        try {
            response = rt.exchange(
                    "https://kapi.kakao.com/v1/user/logout",
                    HttpMethod.POST,
                    kakaoLogoutRequest,
                    String.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                System.out.println("Failed to log out from Kakao: " + response.getStatusCode());
                return null;
            }

        } catch (RestClientException e) {
            System.out.println("Error during Kakao logout: " + e.getMessage());
            return null;
        }

        // responseBody에 있는 정보를 꺼냄
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("id").asLong();
    }

}
