package com.example.community.domain.post.dto;

import com.example.community.domain.post.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class CommentRes {

    @Getter
    @Builder
    public static class SaveCommentDto{
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class ModifyCommentDto{
        private LocalDateTime modifiedAt;
    }

    @Getter
    @Builder
    public static class CommentDto {
        private Long commentId;
        private String nickname;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }

    public static CommentDto toCommentDto(Comment comment){
        return CommentDto.builder()
                .commentId(comment.getId())
                .nickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build();
    }
}
