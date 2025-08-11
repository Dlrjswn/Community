package com.example.community.global.config;

import com.example.community.auth.jwt.JwtFilter;
import com.example.community.auth.jwt.JwtUtil;
import com.example.community.auth.service.CustomUserDetailsService;
import com.example.community.global.AnonIdCookieFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final AnonIdCookieFilter anonIdCookieFilter; // ← 주입

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtFilter jwtFilter = new JwtFilter(jwtUtil, userDetailsService);

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/coupon/issueTest").permitAll()
                        .requestMatchers("/api/v1/post/saveTest").permitAll()
                        .requestMatchers("/api/v1/post/generateTestPosts").permitAll()
                        .requestMatchers("/api/v1/post/detail").permitAll()
                        .requestMatchers("/api/v1/post/list").permitAll()
                        .requestMatchers("/api/v1/post/search").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                // 비로그인 쿠키 발급 필터를 JWT 필터보다 먼저 실행
                .addFilterBefore(anonIdCookieFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtFilter, AnonIdCookieFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}