package com.example.community.global;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class AnonIdCookieFilter extends OncePerRequestFilter {

    private static final String COOKIE_NAME = "ANON_ID";  // 쿠키 이름
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 30; // 30일

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 인증 안 된 상태 + 쿠키 없음
        if ((auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal()))
                && getCookie(request, COOKIE_NAME) == null) {

            String anonId = UUID.randomUUID().toString();

            Cookie cookie = new Cookie(COOKIE_NAME, anonId);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(COOKIE_MAX_AGE);
            // cookie.setSecure(true); // HTTPS만 쓸 때

            response.addCookie(cookie);
        }

        filterChain.doFilter(request, response);
    }

    private Cookie getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (name.equals(c.getName())) {
                    return c;
                }
            }
        }
        return null;
    }
}
