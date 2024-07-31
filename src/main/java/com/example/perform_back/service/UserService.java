package com.example.perform_back.service;

import com.example.perform_back.entity.User;
import com.example.perform_back.exception.UserNotFoundException;
import com.example.perform_back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    //전체 정보 수정
    public void updateUser(Long userId, String username, String profile, String snsUrl, String email) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        user.updateUserInfo(username, profile, snsUrl, email);
        userRepository.save(user);
    }


    //개별 정보 수정
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
