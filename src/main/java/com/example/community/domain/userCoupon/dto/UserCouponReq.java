package com.example.community.domain.userCoupon.dto;

import lombok.Getter;

public class UserCouponReq {


    @Getter
    public static class IssueCouponDto{
        private Long couponId;
    }

    @Getter
    public static class UseCouponDto{
        private Long couponId;
    }
}
