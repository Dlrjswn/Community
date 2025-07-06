package com.example.community.domain.post.controller;

import com.example.community.domain.post.dto.PostRequest;
import com.example.community.domain.post.dto.PostResponse;
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
    public ApiResponse<PostResponse.SaveDto> savePost(@AuthenticationPrincipal UserDetails userDetails, @RequestBody PostRequest.SaveDto saveDto) {
        return ApiResponse.onSuccess(postService.savePost(userDetails.getUsername(), saveDto));
    }

    @PatchMapping("/modify")
    public ApiResponse<PostResponse.ModifyDto> modifyPost(@RequestBody PostRequest.ModifyDto modifyDto) {
        return ApiResponse.onSuccess(postService.modifyPost(modifyDto));
    }
}
