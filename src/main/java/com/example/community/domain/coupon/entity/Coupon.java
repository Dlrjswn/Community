package com.example.community.domain.coupon.entity;

import com.example.community.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.auditing.config.AuditingConfiguration;


import java.time.LocalDateTime;

@Entity
@Builder
@EntityListeners(AuditingConfiguration.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private int validDays;

    @Column(nullable = false)
    private int maxAmount;

    @Column(nullable = false)
    private int currentAmount;

}
