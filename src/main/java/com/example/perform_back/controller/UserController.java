package com.example.perform_back.controller;

import com.example.perform_back.exception.UserNotFoundException;
import com.example.perform_back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

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
    @PatchMapping("/{id}/username")
    public void updateUsername(@PathVariable Long id, @RequestParam String username) {
        try {
            userService.updateUsername(id, username);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PatchMapping("/{id}/profile")
    public void updateProfile(@PathVariable Long id, @RequestParam String profile) {
        try {
            userService.updateProfile(id, profile);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PatchMapping("/{id}/snsUrl")
    public void updateSnsUrl(@PathVariable Long id, @RequestParam String snsUrl) {
        try {
            userService.updateSnsUrl(id, snsUrl);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

}
