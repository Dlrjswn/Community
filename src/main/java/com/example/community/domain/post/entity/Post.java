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
@Table(
        name = "post",
        indexes = {
                @Index(name = "idx_post_category_createdAt", columnList = "category, created_at"),
                @Index(name = "idx_post_category_likeCount", columnList = "category, like_count")
        }
)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void modifyTitle(String title){
        this.title = title;
    }

    public void modifyContent(String content){
        this.content = content;
    }

    public void increaseViewCount(){
        this.viewCount++;
    }

    public void increaseLikeCount(){
        this.likeCount++;
    }


}
