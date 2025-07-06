package com.example.community.auth.controller;

import com.example.community.auth.dto.AuthRequestDto;
import com.example.community.auth.dto.AuthResponseDto;
import com.example.community.auth.jwt.JwtUtil;
import com.example.community.domain.user.entity.User;
import com.example.community.domain.user.repository.UserRepository;
import com.example.community.global.common.ApiResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final BCryptPasswordEncoder encoder;


    public AuthController(UserRepository userRepository, JwtUtil jwtUtil, AuthenticationManager authManager, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
        this.encoder = encoder;
    }
    @PostMapping("/signup")
    public ApiResponse<String> signup(@RequestBody AuthRequestDto.Signup signupDto) {
        if (userRepository.existsByUsername(signupDto.getUsername())) {
            return ApiResponse.onFailure("아이디가 이미 존재합니다");
        }

        User user = User.builder()
                .nickname(signupDto.getNickname())
                .username(signupDto.getUsername())
                .password(encoder.encode(signupDto.getPassword()))
                .email(signupDto.getEmail())
                .build();

        userRepository.save(user);

        return ApiResponse.onSuccess("회원가입 성공");
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponseDto.Login> login(@RequestBody AuthRequestDto.Login loginDto) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());

        return ApiResponse.onSuccess(AuthResponseDto.Login.builder()
                .token(token)
                .build());
    }
}
