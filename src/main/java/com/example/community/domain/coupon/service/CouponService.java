package com.example.community.domain.coupon.service;

import com.example.community.domain.coupon.dto.CouponReq;
import com.example.community.domain.coupon.dto.CouponRes;
import com.example.community.domain.coupon.entity.Coupon;
import com.example.community.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;


    public CouponRes.CreateCouponDto createCoupon(CouponReq.CreateCouponDto createCouponDto) {
        Coupon coupon = Coupon.builder()
                .name(createCouponDto.getName())
                .code(createCouponDto.getCode())
                .isActive(true)
                .validDays(createCouponDto.getValidDays())
                .amount(createCouponDto.getAmount())
                .build();
        couponRepository.save(coupon);
        return  CouponRes.CreateCouponDto.builder()
                .createdAt(coupon.getCreatedAt())
                .isActive(coupon.getIsActive())
                .build();


    }
}
