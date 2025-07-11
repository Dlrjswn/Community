package com.example.community.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

public class PostLikeRes {
    @Getter
    @Builder
    public static class LikePostDto{
        private String message;
    }
}
