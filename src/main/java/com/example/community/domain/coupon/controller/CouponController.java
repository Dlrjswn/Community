package com.example.community.domain.coupon.controller;

import com.example.community.domain.coupon.dto.CouponReq;
import com.example.community.domain.coupon.dto.CouponRes;
import com.example.community.domain.coupon.service.CouponService;
import com.example.community.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/coupon")
    ApiResponse<CouponRes.CreateCouponDto> createCoupon(@RequestBody CouponReq.CreateCouponDto createCouponDto) {
        return ApiResponse.onSuccess(couponService.createCoupon(createCouponDto));
    }

}
