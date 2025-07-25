package com.example.community.domain.userCoupon.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class UserCouponRes {

    @Getter
    @Builder
    public static class IssueCouponDto{
        private String message;
        private LocalDateTime issuedAt;
        private LocalDateTime expiredAt;
    }

    @Getter
    @Builder
    public static class UseCouponDto{
        private String message;
        private boolean isUsed;
        private LocalDateTime usedAt;

    }

}
