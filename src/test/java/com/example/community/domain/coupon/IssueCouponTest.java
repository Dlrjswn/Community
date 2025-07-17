package com.example.community.domain.coupon;

import com.example.community.domain.coupon.entity.Coupon;
import com.example.community.domain.coupon.repository.CouponRepository;
import com.example.community.domain.user.entity.Role;
import com.example.community.domain.user.entity.User;
import com.example.community.domain.user.repository.UserRepository;
import com.example.community.domain.userCoupon.dto.UserCouponReq;
import com.example.community.domain.userCoupon.service.UserCouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
/*
@SpringBootTest
@ActiveProfiles("test")
public class IssueCouponTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCouponService userCouponService;

    private final int threadCount = 300;

    @BeforeEach
    void setUp() {
        int userCount = 300;
        for (int i = 1; i <= userCount; i++) {
            User user = User.builder()
                    .password("1234")
                    .nickname("kim" + i)
                    .username("kim" + i)
                    .email("kim" + i + "@gmail.com")
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
        }
        userRepository.flush();  // DB에 즉시 반영
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)  // 트랜잭션 끔
    void 동시에_쿠폰_요청_테스트() throws InterruptedException {

        Coupon coupon = couponRepository.save(Coupon.builder()
                .name("치킨 쿠폰")
                .code("1234")
                .currentAmount(0)
                .isActive(true)
                .maxAmount(100)
                .validDays(7)
                .build());

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 1; i <= threadCount; i++) {
            int userId = i;
            executorService.submit(() -> {
                try {
                    userCouponService.issueCoupon("kim" + userId, UserCouponReq.IssueCouponDto.builder()
                            .couponId(coupon.getId()).build());
                } catch (Exception e) {
                    System.out.println("발급 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        int usedCount = couponRepository.findById(coupon.getId()).get().getCurrentAmount();
        System.out.println("사용된 쿠폰 수: " + usedCount);

        // then
        assertThat(usedCount).isEqualTo(100); // 수량 초과 사용 방지
    }
}
*/
