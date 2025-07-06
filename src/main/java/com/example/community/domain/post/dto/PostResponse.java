package com.example.community.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;


public class PostResponse {
    @Getter
    @Builder
    public static class SaveDto{
        private LocalDate createdAt;
        private String message;
    }

}
