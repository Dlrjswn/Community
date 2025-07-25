package com.example.community.domain.coupon.dto;

import lombok.Getter;

public class CouponReq {

    @Getter
    public static class CreateCouponDto{
        private String name;
        private String code;
        private int validDays;
        private int amount;
    }
}
