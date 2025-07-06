package com.example.community.domain.post.dto;

import lombok.Builder;
import lombok.Getter;


import java.time.LocalDateTime;


public class PostRes {
    @Getter
    @Builder
    public static class SavePostDto{
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class ModifyPostDto{
        private LocalDateTime modifiedAt;
    }


}
