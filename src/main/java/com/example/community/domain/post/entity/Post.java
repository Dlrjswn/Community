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
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private Category category;

    private int likeCount;

    private int viewCount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
