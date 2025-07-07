package com.example.community.domain.post.controller;

import com.example.community.domain.post.dto.PostReq;
import com.example.community.domain.post.dto.PostRes;
import com.example.community.domain.post.service.PostService;
import com.example.community.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;



    @PostMapping("/save")
    public ApiResponse<PostRes.SavePostDto> savePost(@AuthenticationPrincipal UserDetails userDetails, @RequestBody PostReq.SavePostDto savePostDto) {
        return ApiResponse.onSuccess(postService.savePost(userDetails.getUsername(), savePostDto));
    }

    @PatchMapping("/modify")
    public ApiResponse<PostRes.ModifyPostDto> modifyPost(@RequestBody PostReq.ModifyPostDto modifyPostDto) {
        return ApiResponse.onSuccess(postService.modifyPost(modifyPostDto));
    }

    @GetMapping("/detail")
    public ApiResponse<PostRes.GetPostDetailDto> getPostDetail(@RequestBody PostReq.GetPostDetailDto getPostDetailDto) {
        return ApiResponse.onSuccess(postService.getPostDetail(getPostDetailDto));
    }
}
