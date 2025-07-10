package com.example.community.domain.coupon.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class CouponRes {

    @Getter
    @Builder
    public static class CreateCouponDto{
        private LocalDateTime createdAt;
        private boolean isActive;
    }
}
