package com.example.community.domain.userCoupon.dto;

import lombok.Builder;
import lombok.Getter;

public class UserCouponReq {

    @Builder
    @Getter
    public static class IssueCouponDto{
        private Long couponId;
    }

    @Getter
    public static class UseCouponDto{
        private Long couponId;
    }
}
