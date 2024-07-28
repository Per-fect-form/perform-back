package com.example.perform_back.login.src.main.java.com.kakaologin.demo.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kakaologin.demo.dto.KakaoUserInfoResponseDto;
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
}
