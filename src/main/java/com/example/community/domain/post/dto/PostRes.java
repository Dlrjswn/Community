package com.example.community.domain.post.dto;

import com.example.community.domain.post.entity.Post;
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
        private long totalCommentCount;
        private int totalCommentPageCount;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private List<String> imageUrls;
        private List<CommentRes.CommentDto> comments;
    }


    @Getter
    @Builder
    public static class PostPreviewDto{
        private Long postId;
        private String nickname;
        private String category;
        private String title;
        private int likeCount;
        private int viewCount;
        private LocalDateTime createdAt;
    }


    public static PostPreviewDto toPostPreviewDto(Post post){
        return PostPreviewDto.builder()
                .postId(post.getId())
                .nickname(post.getUser().getNickname())
                .category(post.getCategory().name())
                .title(post.getTitle())
                .likeCount(post.getLikeCount())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public static PostPreviewDto toPostPreviewDto(PostPreviewProjection projection){
        return PostPreviewDto.builder()
                .postId(projection.getPostId())
                .nickname(projection.getNickname())
                .category(projection.getCategory())
                .title(projection.getTitle())
                .likeCount(projection.getLikeCount())
                .viewCount(projection.getViewCount())
                .createdAt(projection.getCreatedAt())
                .build();
    }





}
