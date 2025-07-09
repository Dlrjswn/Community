package com.example.community.domain.post.controller;

import com.example.community.domain.post.dto.LikeReq;
import com.example.community.domain.post.dto.LikeRes;
import com.example.community.domain.post.service.LikeService;
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
public class LikeController {
    private final LikeService likeService;

    @PostMapping("")
    public ApiResponse<LikeRes.LikePostDto> likePost(@RequestBody LikeReq.LikePostDto likePostDto, @AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.onSuccess(likeService.likePost(likePostDto, userDetails.getUsername()));
    }
}
