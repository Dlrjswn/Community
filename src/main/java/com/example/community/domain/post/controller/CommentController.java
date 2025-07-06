package com.example.community.domain.post.controller;

import com.example.community.domain.post.dto.CommentReq;
import com.example.community.domain.post.dto.CommentRes;
import com.example.community.domain.post.service.CommentService;
import com.example.community.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/save")
    public ApiResponse<CommentRes.SaveCommentDto> saveComment(@AuthenticationPrincipal UserDetails userDetails,
                                                              @RequestBody CommentReq.SaveCommentDto saveCommentDto) {
        return ApiResponse.onSuccess(commentService.saveComment(userDetails.getUsername(),saveCommentDto));
    }
}
