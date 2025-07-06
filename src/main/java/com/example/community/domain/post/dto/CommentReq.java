package com.example.community.domain.post.dto;

import lombok.Getter;

public class CommentReq {

    @Getter
    public static class SaveCommentDto{
        private Long postId;
        private String content;
    }
}
