package com.example.community.domain.post.entity;

import com.example.community.domain.user.entity.User;
import com.example.community.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.auditing.config.AuditingConfiguration;

@Entity
@Builder
@EntityListeners(AuditingConfiguration.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"}))
public class PostLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
