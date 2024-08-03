package com.example.perform_back.service;

import com.example.perform_back.dto.UserDto;
import com.example.perform_back.entity.User;
import com.example.perform_back.exception.UserNotFoundException;
import com.example.perform_back.repository.UserRepository;

import java.util.List;
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
    public void saveUser(Long id, String username, String profile, String email) {
        // 주어진 ID로 사용자 검색
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) return;

        // 사용자가 존재하지 않으면 새로운 사용자 생성
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setProfile(profile);
        user.setEmail(email);
        user.setCreatedDate(new Date());
        userRepository.save(user);
    }

    // 전체 정보 수정
    public void updateUser(UserDto userDto) {
//        User user = findById(userId);
//        user.updateUserInfo(username, profile, snsUrl, email);
//        userRepository.save(user);
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


    public void adOff(Long userId, Boolean state) {
        User user = findById(userId);
        findById(userId).setAd(state);
        userRepository.save(user);
    }

    public List<User> getAllExpertsForAd() {
        return userRepository.findAllExpertsForAd();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));
    }

    public User findByAccessToken(String token) {
        Long userId = kakaoService.getUserInfo(token).getId();
        return findById(userId);
    }
}
