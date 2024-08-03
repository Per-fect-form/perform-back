package com.example.perform_back.controller;

import com.example.perform_back.entity.User;
import com.example.perform_back.exception.UserNotFoundException;
import com.example.perform_back.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "User API")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 전체 업데이트
    @PutMapping("/{id}")
    public void updateUser(@PathVariable Long id,
                           @RequestParam(required = false) String username,
                           @RequestParam(required = false) String profile,
                           @RequestParam(required = false) String snsUrl,
                           @RequestParam(required = false) String email) {
        try {
            userService.updateUser(id, username, profile, snsUrl, email);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    // 개별 필드 업데이트
    @Operation(summary = "유저 네임 수정")
    @PatchMapping("/{id}/username")
    public void updateUsername(@PathVariable Long id, @RequestParam String username) {
        try {
            userService.updateUsername(id, username);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Operation(summary = "유저 사진 수정")
    @PatchMapping("/{id}/profile")
    public void updateProfile(@PathVariable Long id, @RequestParam String profile) {
        try {
            userService.updateProfile(id, profile);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Operation(summary = "유저 sns 수정")
    @PatchMapping("/{id}/snsUrl")
    public void updateSnsUrl(@PathVariable Long id, @RequestParam String snsUrl) {
        try {
            userService.updateSnsUrl(id, snsUrl);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Operation(summary = "유저 고수홍보 온오프")
    @PatchMapping("/{id}/ad")
    public void adOff(@PathVariable Long id, @RequestParam Boolean state) { //boolean으로 on이나 off를 입력
        userService.adOff(id, state);
    }

    @Operation(summary = "홍보유저 전체 불러오기")
    @GetMapping("/experts")
    public List<User> getAllExpertsForAd() {
        return userService.getAllExpertsForAd();
    }

}
