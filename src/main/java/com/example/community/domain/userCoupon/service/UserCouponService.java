package com.example.community.domain.userCoupon.service;

import com.example.community.domain.coupon.entity.Coupon;
import com.example.community.domain.coupon.repository.CouponRepository;
import com.example.community.domain.coupon.service.CouponService;
import com.example.community.domain.user.entity.User;
import com.example.community.domain.user.repository.UserRepository;
import com.example.community.domain.userCoupon.dto.UserCouponReq;
import com.example.community.domain.userCoupon.dto.UserCouponRes;
import com.example.community.domain.userCoupon.entity.UserCoupon;
import com.example.community.domain.userCoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    public UserCouponRes.IssueCouponDto issueCoupon(String username, UserCouponReq.IssueCouponDto issueCouponDto) {
        User user = userRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
        Coupon coupon = couponRepository.findById(issueCouponDto.getCouponId()).orElseThrow(()->new RuntimeException("해당 쿠폰을 찾을 수 없습니다."));

        UserCoupon userCoupon = UserCoupon.builder()
                .user(user)
                .coupon(coupon)
                .expiredAt(LocalDateTime.now().plusDays(coupon.getValidDays()))
                .isUsed(false)
                .usedAt(null)
                .build();

        userCouponRepository.save(userCoupon);

        return UserCouponRes.IssueCouponDto.builder()
                .issuedAt(userCoupon.getCreatedAt())
                .expiredAt(userCoupon.getExpiredAt())
                .build();
    }
}
