package com.example.community.domain.userCoupon.repository;

import com.example.community.domain.userCoupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
}
