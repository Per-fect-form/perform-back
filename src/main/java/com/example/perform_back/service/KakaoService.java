package com.example.perform_back.service;

import com.example.perform_back.dto.KakaoInfoDto;
import com.example.perform_back.dto.KakaoUserInfoResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
public class KakaoService {

    private final UserService userService;
    private String clientId;
    private String clientSecret;
    private String redirectUri;

    public KakaoService(@Value("${kakao.client_id}") String clientId,
                        @Value("${kakao.client_secret}") String clientSecret,
                        @Value("${kakao.redirect_uri}") String redirectUri, UserService userService) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.userService = userService;
    }

    public String getAccessToken(String code) throws JsonProcessingException {
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

        return jsonNode.get("access_token").asText();
    }

    public String getAccessTokenFromKakao(String code) {
        String accessToken = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=").append(clientId);
            sb.append("&client_secret=").append(clientSecret);
            sb.append("&redirect_uri=").append(redirectUri);
            sb.append("&code=").append(code);
            writer.write(sb.toString());
            writer.flush();

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            System.out.println("response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result.toString());

            accessToken = element.getAsJsonObject().get("access_token").getAsString();

            System.out.println("access_token : " + accessToken);

            br.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return accessToken;
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        KakaoUserInfoResponseDto userInfo = null;

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            System.out.println("response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result.toString());

            userInfo = new KakaoUserInfoResponseDto();
            userInfo.id = element.getAsJsonObject().get("id").getAsLong();

            JsonElement kakaoAccountElement = element.getAsJsonObject().get("kakao_account");
            JsonElement profileElement = kakaoAccountElement.getAsJsonObject().get("profile");

            KakaoUserInfoResponseDto.KakaoAccount kakaoAccount = userInfo.new KakaoAccount();
            KakaoUserInfoResponseDto.KakaoAccount.Profile profile = kakaoAccount.new Profile();
            profile.nickName = profileElement.getAsJsonObject().get("nickname").getAsString();
            profile.profileImageUrl = profileElement.getAsJsonObject().get("profile_image_url").getAsString();
            kakaoAccount.profile = profile;
            userInfo.kakaoAccount = kakaoAccount;

            System.out.println("User ID : " + userInfo.getId());
            System.out.println("NickName : " + userInfo.getKakaoAccount().getProfile().getNickName());
            System.out.println("ProfileImageUrl : " + userInfo.getKakaoAccount().getProfile().getProfileImageUrl());

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userInfo;
    }

    public KakaoInfoDto getKakaoInfo(String accessToken) throws JsonProcessingException {
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
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();

        return new KakaoInfoDto(id, nickname, email);
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
        Long id = jsonNode.get("id").asLong();
        System.out.println("반환된 id: " + id);
        return id;
    }

}
