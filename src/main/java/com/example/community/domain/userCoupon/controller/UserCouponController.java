package com.example.community.domain.userCoupon.controller;

import com.example.community.domain.userCoupon.dto.UserCouponReq;
import com.example.community.domain.userCoupon.dto.UserCouponRes;
import com.example.community.domain.userCoupon.service.UserCouponService;
import com.example.community.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/coupon")
@RequiredArgsConstructor
public class UserCouponController {

    private final UserCouponService userCouponService;

    @PostMapping("/issue")
    public ApiResponse<UserCouponRes.IssueCouponDto> issueCoupon(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserCouponReq.IssueCouponDto issueCouponDto ) {
        return ApiResponse.onSuccess(userCouponService.issueCoupon(userDetails.getUsername(), issueCouponDto));
    }

    @PatchMapping("/use")
    public ApiResponse<UserCouponRes.UseCouponDto> useCoupon(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserCouponReq.UseCouponDto useCouponDto ) {
        return ApiResponse.onSuccess(userCouponService.useCoupon(userDetails.getUsername(),useCouponDto));
    }
}
