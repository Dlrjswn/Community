package com.example.community.domain.coupon.repository;

import com.example.community.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
