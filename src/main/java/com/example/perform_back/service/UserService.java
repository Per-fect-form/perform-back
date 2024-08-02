package com.example.perform_back.service;

import com.example.perform_back.entity.User;
import com.example.perform_back.exception.UserNotFoundException;
import com.example.perform_back.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private KakaoService kakaoService;

    @Autowired
    public UserService(UserRepository userRepository, KakaoService kakaoService){
        this.userRepository = userRepository;
        this.kakaoService = kakaoService;
    }

    // 사용자 정보 DB저장
    public User saveOrUpdateUser(Long id, String username, String profile, String email) {
        // 주어진 ID로 사용자 검색
        Optional<User> userOptional = userRepository.findById(id);
        User user;

        if (userOptional.isPresent()) {
            // 사용자가 존재하면 정보 업데이트
            user = userOptional.get();
            user.updateUserInfo(username, profile, user.getSnsUrl(), email);
        } else {
            // 사용자가 존재하지 않으면 새로운 사용자 생성
            user = new User();
            user.setId(id);
            user.setUsername(username);
            user.setProfile(profile);
            user.setEmail(email);
            user.setCreatedDate(new Date());
        }

        //반환
        return userRepository.save(user);
    }

    // 전체 정보 수정
    public void updateUser(Long userId, String username, String profile, String snsUrl, String email) {
        User user = findById(userId);
        user.updateUserInfo(username, profile, snsUrl, email);
        userRepository.save(user);
    }

    // 개별 정보 수정
    public void updateUsername(Long userId, String username) {
        User user = findById(userId);
        user.updateUsername(username);
        userRepository.save(user);
    }

    public void updateProfile(Long userId, String profile) {
        User user = findById(userId);
        user.updateProfile(profile);
        userRepository.save(user);
    }

    public void updateSnsUrl(Long userId, String snsUrl) {
        User user = findById(userId);
        user.updateSnsUrl(snsUrl);
        userRepository.save(user);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    public User findByAccessToken(String token) throws JsonProcessingException {
        Long userId = kakaoService.getUserInfo(token).getId();
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent())
            return user.get();
        else
            throw new RuntimeException("유효하지 않은 토큰입니다.");

    }
}
