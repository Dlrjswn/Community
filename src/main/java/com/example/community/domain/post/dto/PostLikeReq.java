package com.example.community.domain.post.dto;


import lombok.Getter;

public class PostLikeReq {

    @Getter
    public static class LikePostDto{
        private Long postId;
    }
}
