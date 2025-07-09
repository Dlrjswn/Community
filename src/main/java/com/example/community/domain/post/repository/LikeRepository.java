package com.example.community.domain.post.repository;

import com.example.community.domain.post.entity.Like;
import com.example.community.domain.post.entity.Post;
import com.example.community.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserAndPost(User user, Post post);
}
