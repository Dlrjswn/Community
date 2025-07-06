package com.example.community.auth.jwt;


import com.example.community.auth.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + header); // 로그 찍기

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            System.out.println("JWT 토큰: " + token); // 로그 찍기

            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                System.out.println("토큰에서 추출한 username: " + username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                System.out.println("유효하지 않은 JWT 토큰입니다.");
            }
        } else {
            System.out.println("Authorization 헤더가 없거나 Bearer로 시작하지 않습니다.");
        }

        filterChain.doFilter(request, response);
    }

}
