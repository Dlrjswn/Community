package com.example.community.domain.post.dto;

import lombok.Builder;
import lombok.Getter;


import java.time.LocalDateTime;
import java.util.List;


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

    @Getter
    @Builder
    public static class GetPostDetailDto {
        private String nickname;
        private String category;
        private String title;
        private String content;
        private int viewCount;
        private int likeCount;
        private long commentCount;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private List<String> imageUrls;
        private List<CommentRes.CommentDto> comments;
    }




}
