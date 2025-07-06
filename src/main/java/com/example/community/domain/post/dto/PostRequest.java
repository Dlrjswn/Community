package com.example.community.domain.post.dto;

import lombok.Getter;

import java.util.List;

public class PostRequest {

    @Getter
    public static class SaveDto{
        private String title;
        private String content;
        private String category;
        private List<String> imageUrls;

    }

    @Getter
    public static class ModifyDto{
        private Long postId;
        private String title;
        private String content;
        private List<String> addedImageUrls;
        private List<Long> removedImageIds;
    }

}
