package com.example.perform_back.service;

import com.example.perform_back.dto.KakaoUserInfoResponseDto;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
public class KakaoService {

    private String clientId;
    private String clientSecret;
    private String redirectUri;

    public KakaoService(@Value("${kakao.client_id}") String clientId,
                        @Value("${kakao.client_secret}") String clientSecret,
                        @Value("${kakao.redirect_uri}") String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    //토큰 발급
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

            // 응답을 파싱하여 각 필드에 저장
            String tokenType = element.getAsJsonObject().get("token_type").getAsString();
            accessToken = element.getAsJsonObject().get("access_token").getAsString();
            int expiresIn = element.getAsJsonObject().get("expires_in").getAsInt();
            String refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();
            int refreshTokenExpiresIn = element.getAsJsonObject().get("refresh_token_expires_in").getAsInt();

            // 각 필드 출력
            System.out.println("token_type ------------->: " + tokenType);
            System.out.println("access_token ------------->: " + accessToken);
            System.out.println("expires_in ------------->: " + expiresIn);
            System.out.println("refresh_token ------------->: " + refreshToken);
            System.out.println("refresh_token_expires_in ------------->: " + refreshTokenExpiresIn);

            br.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return accessToken;
    }

    //사용자 정보 가져오기
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

            if (kakaoAccountElement.getAsJsonObject().has("email")) {
                kakaoAccount.email = kakaoAccountElement.getAsJsonObject().get("email").getAsString();
            }

            userInfo.kakaoAccount = kakaoAccount;

            System.out.println("User ID : " + userInfo.getId());
            System.out.println("NickName : " + userInfo.getKakaoAccount().getProfile().getNickName());
            System.out.println("ProfileImageUrl : " + userInfo.getKakaoAccount().getProfile().getProfileImageUrl());
            System.out.println("Email : " + userInfo.getKakaoAccount().getEmail());

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userInfo;
    }
}
