package com.example.community.domain.userCoupon.repository;

import com.example.community.domain.userCoupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    @Query("SELECT uc FROM UserCoupon uc " +
            "JOIN FETCH uc.user u " +
            "JOIN FETCH uc.coupon c " +
            "WHERE u.username = :username AND c.id = :couponId")
    Optional<UserCoupon> findByUsernameAndCouponIdWithUserAndCoupon(@Param("username") String username,
                                                   @Param("couponId") Long couponId);
}
