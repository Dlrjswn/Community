package com.example.community.domain.post.controller;

import com.example.community.domain.post.dto.PostReq;
import com.example.community.domain.post.dto.PostRes;
import com.example.community.domain.post.service.PostService;
import com.example.community.global.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/generateTestPosts")
    public String generateTestPosts() {
        postService.generateTestPosts();
        return "테스트용 10만 개 게시물 생성 완료 (createdAt 시간 차이 포함)";
    }

    @PostMapping("/save")
    public ApiResponse<PostRes.SavePostDto> savePost(@AuthenticationPrincipal UserDetails userDetails, @RequestBody PostReq.SavePostDto savePostDto) {
        return ApiResponse.onSuccess(postService.savePost(userDetails.getUsername(), savePostDto));
    }

    @PostMapping("/saveTest")
    public ApiResponse<PostRes.SavePostDto> savePostTest(@RequestParam String username, @RequestBody PostReq.SavePostTestDto savePostTestDto) {
        return ApiResponse.onSuccess(postService.savePostTest(username, savePostTestDto));
    }

    @PatchMapping("/modify")
    public ApiResponse<PostRes.ModifyPostDto> modifyPost(@RequestBody PostReq.ModifyPostDto modifyPostDto) {
        return ApiResponse.onSuccess(postService.modifyPost(modifyPostDto));
    }

    @GetMapping("/detail")
    public ApiResponse<PostRes.GetPostDetailDto> getPostDetail(@RequestBody PostReq.GetPostDetailDto getPostDetailDto, HttpServletRequest request) {
        return ApiResponse.onSuccess(postService.getPostDetail(getPostDetailDto, request));
    }

    @GetMapping("/list")
    public ApiResponse<Page<PostRes.PostPreviewDto>> getPostList(@RequestBody PostReq.GetPostListDto getPostListDto) {
        return ApiResponse.onSuccess(postService.getPostList(getPostListDto));
    }

    @GetMapping("/my-post")
    public ApiResponse<List<PostRes.PostPreviewDto>> getMyPostList(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.onSuccess(postService.getMyPostList(userDetails.getUsername()));
    }

    @GetMapping("search")
    public ApiResponse<Page<PostRes.PostPreviewDto>> searchPostList(@RequestBody PostReq.SearchPostListDto savePostListDto){
        return ApiResponse.onSuccess(postService.searchPostList(savePostListDto));
    }



}
