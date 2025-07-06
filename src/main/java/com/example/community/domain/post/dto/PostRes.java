package com.example.community.domain.post.dto;

import lombok.Builder;
import lombok.Getter;


import java.time.LocalDateTime;


public class PostRes {
    @Getter
    @Builder
    public static class SavePostDto{
        private LocalDateTime createdAt;
        private String message;
    }

    @Getter
    @Builder
    public static class ModifyPostDto{
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private String message;
    }

}
