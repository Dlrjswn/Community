package com.example.community.domain.post.controller;

import com.example.community.domain.post.dto.CommentReq;
import com.example.community.domain.post.dto.CommentRes;
import com.example.community.domain.post.service.CommentService;
import com.example.community.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PatchMapping("/modify")
    public ApiResponse<CommentRes.ModifyCommentDto> modifyComment(@RequestBody CommentReq.ModifyCommentDto modifyCommentDto) {
        return ApiResponse.onSuccess(commentService.modifyComment(modifyCommentDto));
    }

    @GetMapping("/my-comment")
    public ApiResponse<List<CommentRes.CommentDto>> getMyCommentList(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.onSuccess(commentService.getMyCommentList(userDetails.getUsername()));
    }

    @GetMapping("/list")
    public ApiResponse<Page<CommentRes.CommentDto>> getCommentList(@RequestBody CommentReq.GetCommentDto getCommentDto) {
        return ApiResponse.onSuccess(commentService.getCommentList(getCommentDto));
    }
}
