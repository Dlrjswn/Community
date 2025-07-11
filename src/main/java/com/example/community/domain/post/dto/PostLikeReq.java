package com.example.community.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

public class PostLikeReq {
    @Builder
    @Getter
    public static class LikePostDto{
        private Long postId;
    }
}
