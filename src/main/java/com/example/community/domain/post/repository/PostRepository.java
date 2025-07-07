package com.example.community.domain.post.repository;

import com.example.community.domain.post.entity.Post;
import com.example.community.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>,PostRepositoryCustom {
    List<Post> findAllByUser(User user);
}
