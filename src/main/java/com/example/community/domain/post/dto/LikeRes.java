package com.example.community.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class LikeRes {
    @Getter
    @Builder
    public static class LikePostDto{
        private String message;
    }
}
