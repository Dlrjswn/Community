package com.example.community.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class CommentRes {

    @Getter
    @Builder
    public static class SaveCommentDto{
        private LocalDateTime createdAt;
    }
}
