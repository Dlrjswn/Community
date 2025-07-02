package com.example.community.domain.admin.entity;

import com.example.community.global.common.BaseEntity;
import jakarta.persistence.*;
import org.springframework.data.auditing.config.AuditingConfiguration;

@Entity
@EntityListeners(AuditingConfiguration.class)
public class Admin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
