package com.example.perform_back.service;

import com.example.perform_back.entity.User;
import com.example.perform_back.exception.UserNotFoundException;
import com.example.perform_back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        user.updateUserInfo(username, profile, snsUrl, email);
        userRepository.save(user);
    }

    // 개별 정보 수정
    public void updateUsername(Long userId, String username) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        user.updateUsername(username);
        userRepository.save(user);
    }

    public void updateProfile(Long userId, String profile) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        user.updateProfile(profile);
        userRepository.save(user);
    }

    public void updateSnsUrl(Long userId, String snsUrl) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        user.updateSnsUrl(snsUrl);
        userRepository.save(user);
    }
}
