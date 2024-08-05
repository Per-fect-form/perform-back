package com.example.perform_back.controller;

import com.example.perform_back.dto.ExpertDto;
import com.example.perform_back.dto.UserDto;
import com.example.perform_back.entity.User;
import com.example.perform_back.exception.UserNotFoundException;
import com.example.perform_back.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User API")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "유저 정보 조회")
    @GetMapping("/my")
    public ResponseEntity<UserDto> getUserInfo(@RequestHeader("Authorization") String accessToken) {
        UserDto userDto = userService.getUserInfo(accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }

    // 전체 업데이트
    @Operation(summary = "유저 네임 수정")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@RequestPart("user") UserDto userDto, @RequestPart(value = "profile", required = false) MultipartFile profile,
                                             @RequestHeader("Authorization") String accessToken) {
        userService.updateUser(userDto, profile, accessToken);
        return ResponseEntity.status(HttpStatus.OK).body("수정이 완료되었습니다.");
    }

    // 개별 필드 업데이트
    @Operation(summary = "유저 네임 수정")
    @PatchMapping("/{id}/username")
    public void updateUsername(@PathVariable Long id, @RequestParam String username) {
        userService.updateUsername(id, username);
    }

    @Operation(summary = "유저 사진 수정")
    @PatchMapping("/{id}/profile")
    public void updateProfile(@PathVariable Long id, @RequestParam String profile) {
        userService.updateProfile(id, profile);
    }

    @Operation(summary = "유저 sns 수정")
    @PatchMapping("/{id}/snsUrl")
    public void updateSnsUrl(@PathVariable Long id, @RequestParam String snsUrl) {
        userService.updateSnsUrl(id, snsUrl);
    }

    @Operation(summary = "유저 고수홍보 온오프")
    @PatchMapping("/{id}/ad")
    public ResponseEntity<String> adOff(@PathVariable Long id, @RequestParam Boolean state) { //boolean으로 on이나 off를 입력
        userService.adOff(id, state);
        return ResponseEntity.status(HttpStatus.OK).body("온오프가 완료되었습니다.");
    }

    @Operation(summary = "홍보유저 전체 불러오기")
    @GetMapping("/experts")
    public ResponseEntity<List<ExpertDto>> getAllExpertsForAd() {
        List<ExpertDto> experts = userService.getAllExpertsForAd();
        return ResponseEntity.status(HttpStatus.OK).body(experts);
    }

}
