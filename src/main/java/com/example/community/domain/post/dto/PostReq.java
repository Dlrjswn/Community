package com.example.community.domain.post.dto;


import lombok.Getter;

import java.util.List;

public class PostReq {

    @Getter
    public static class SavePostDto{
        private String title;
        private String content;
        private String category;
        private List<String> imageUrls;

    }

    @Getter
    public static class ModifyPostDto{
        private Long postId;
        private String title;
        private String content;
        private List<String> addedImageUrls;
        private List<Long> removedImageIds;
    }

    @Getter
    public static class GetPostDetailDto{
        private Long postId;
        private int page;
        private int pageSize;
    }

    @Getter
    public static class GetPostListDto{
        private String category;
        private int page;
        private int pageSize;
        private String sort;  //"like" or "latest"

    }

    @Getter
    public static class SearchPostListDto{
        private String keyword;
        private int page;
        private int pageSize;
    }






}
