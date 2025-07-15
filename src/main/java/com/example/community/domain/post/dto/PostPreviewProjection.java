package com.example.community.domain.post.dto;

import java.time.LocalDateTime;

public interface PostPreviewProjection {
    Long getPostId();
    String getNickname();
    String getCategory();
    String getTitle();
    Integer getLikeCount();
    Integer getViewCount();
    LocalDateTime getCreatedAt();
}
