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
        Long couponId = issueCouponDto.getCouponId();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(() -> new RuntimeException("해당 쿠폰을 찾을 수 없습니다."));

        if(userCouponRepository.existsByUserAndCoupon(user,coupon)){
            return UserCouponRes.IssueCouponDto.builder()
                    .message("이미 쿠폰을 발급받았습니다.")
                    .issuedAt(null)
                    .expiredAt(null)
                    .build();
        }

                UserCoupon userCoupon = userCouponRepository.save(
                        UserCoupon.builder()
                                .user(user)
                                .coupon(coupon)      // coupon은 proxy이어도 무방
                                .expiredAt(LocalDateTime.now()
                                        .plusDays(coupon.getValidDays()))
                                .isUsed(false)
                                .build()
                );

        couponRepository.decreaseAmount(couponId);

                return UserCouponRes.IssueCouponDto.builder()
                        .message("쿠폰 발급 완료")
                        .issuedAt(userCoupon.getCreatedAt())
                        .expiredAt(userCoupon.getExpiredAt())
                        .build();

            }




    public UserCouponRes.UseCouponDto useCoupon(String username, UserCouponReq.UseCouponDto useCouponDto) {
        UserCoupon userCoupon = userCouponRepository.findByUsernameAndCouponIdWithUserAndCoupon(username, useCouponDto.getCouponId()).orElseThrow(()->new RuntimeException("해당 발급 쿠폰을 찾을 수 없습니다."));
        if(userCoupon.getIsUsed()){
            return UserCouponRes.UseCouponDto.builder()
                    .isUsed(userCoupon.getIsUsed())
                    .usedAt(userCoupon.getUsedAt())
                    .message("이미 사용한 쿠폰입니다.")
                    .build();
        }
        else {
            userCoupon.useCoupon(LocalDateTime.now());

            return UserCouponRes.UseCouponDto.builder()
                    .isUsed(userCoupon.getIsUsed())
                    .usedAt(userCoupon.getUsedAt())
                    .message("쿠폰이 사용되었습니다.")
                    .build();
        }
    }
}
