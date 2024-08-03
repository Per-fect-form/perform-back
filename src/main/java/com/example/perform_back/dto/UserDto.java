package com.example.perform_back.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String profile;
    private String email;
    private String snsUrl;
    private boolean isExpert;
    private boolean ad;
}
