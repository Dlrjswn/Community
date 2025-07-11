package com.example.community.domain.post.controller;

import com.example.community.domain.post.dto.PostLikeReq;
import com.example.community.domain.post.dto.PostLikeRes;
import com.example.community.domain.post.service.PostLikeService;
import com.example.community.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/like")
@RequiredArgsConstructor
public class PostLikeController {
    private final PostLikeService postLikeService;

    @PostMapping("")
    public ApiResponse<PostLikeRes.LikePostDto> likePost(@RequestBody PostLikeReq.LikePostDto likePostDto, @AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.onSuccess(postLikeService.likePost(likePostDto, userDetails.getUsername()));
    }
}
