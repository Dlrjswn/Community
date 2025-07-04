package com.example.community.auth.dto;

import lombok.Builder;
import lombok.Getter;

public class AuthResponseDto {

    @Builder
    @Getter
    public static class Login{
        private String token;
    }
}
