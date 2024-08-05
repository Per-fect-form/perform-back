package com.example.perform_back.service;

import com.example.perform_back.dto.CommentDto;
import com.example.perform_back.dto.ExpertDto;
import com.example.perform_back.dto.UserDto;
import com.example.perform_back.entity.Comment;
import com.example.perform_back.entity.User;
import com.example.perform_back.exception.UserNotFoundException;
import com.example.perform_back.global.service.FileS3Service;
import com.example.perform_back.repository.UserRepository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final FileS3Service fileS3Service;
    private UserRepository userRepository;
    private KakaoService kakaoService;

    @Autowired
    public UserService(UserRepository userRepository, KakaoService kakaoService, FileS3Service fileS3Service){
        this.userRepository = userRepository;
        this.kakaoService = kakaoService;
        this.fileS3Service = fileS3Service;
    }

    public UserDto getUserInfo(String accessToken) {
        User user = findByAccessToken(accessToken);
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .profile(user.getProfile())
                .email(user.getEmail())
                .snsUrl(user.getSnsUrl())
                .isExpert(user.isExpert())
                .ad(user.isAd()).build();
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
        user.setExpert(false);
        user.setAd(true);
        userRepository.save(user);
    }

    // 전체 정보 수정
    public void updateUser(UserDto userDto, MultipartFile profile, String accessToken) {
        User user = findByAccessToken(accessToken);
        user.setUsername(userDto.getUsername());
        user.setSnsUrl(userDto.getSnsUrl());
        if(profile != null) {
            String profileUrl = fileS3Service.uploadProfileImage(profile);
            user.setProfile(profileUrl);
        }
        userRepository.save(user);
    }

    // 개별 정보 수정
    public void updateUsername(Long userId, String username) {
        User user = findById(userId);
        user.setUsername(username);
        userRepository.save(user);
    }

    public void updateProfile(Long userId, String profile) {
        User user = findById(userId);
        user.setProfile(profile);
        userRepository.save(user);
    }

    public void updateSnsUrl(Long userId, String snsUrl) {
        User user = findById(userId);
        user.setSnsUrl(snsUrl);
        userRepository.save(user);
    }


    public void adOff(Long userId, Boolean state) {
        User user = findById(userId);
        findById(userId).setAd(state);
        userRepository.save(user);
    }

    public List<ExpertDto> getAllExpertsForAd() {
        List<User> experts = userRepository.findAllExpertsForAd();
        return convertToExpertDtoList(experts);
    }

    public ExpertDto converToExpertDto(User user) {
        return ExpertDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .snsUrl(user.getSnsUrl())
                .profile(user.getProfile())
                .build();
    }

    private List<ExpertDto> convertToExpertDtoList(List<User> users) {
        return users.stream()
                .map(this::converToExpertDto)
                .collect(Collectors.toList());
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));
    }

    public User findByAccessToken(String token) {
        Long userId = kakaoService.getUserInfo(token).getId();
        return findById(userId);
    }

}
