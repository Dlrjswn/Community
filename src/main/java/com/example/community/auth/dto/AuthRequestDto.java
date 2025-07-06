package com.example.community.auth.dto;

import lombok.Getter;

public class AuthRequestDto {

    @Getter
    public static class Signup {
        private String nickname;
        private String username;
        private String password;
        private String email;
    }

    @Getter
    public static class Login {
        private String username;
        private String password;
    }

}
