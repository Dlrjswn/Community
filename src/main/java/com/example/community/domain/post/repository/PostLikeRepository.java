package com.example.community.domain.post.repository;

import com.example.community.domain.post.entity.PostLike;
import com.example.community.domain.post.entity.Post;
import com.example.community.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUserAndPost(User user, Post post);
}
