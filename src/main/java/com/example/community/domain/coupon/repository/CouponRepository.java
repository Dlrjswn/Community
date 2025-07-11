package com.example.community.domain.coupon.repository;

import com.example.community.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Coupon c
           SET c.currentAmount = c.currentAmount + 1
         WHERE c.id = :couponId
           AND c.currentAmount < c.maxAmount
    """)
    int increaseCurrentAmountSafely(@Param("couponId") Long couponId);
}
