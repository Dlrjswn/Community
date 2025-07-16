package com.example.community.domain.post.repository;


import com.example.community.domain.post.entity.Comment;
import com.example.community.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c join fetch c.user where c.user = :user")
    List<Comment> findAllByUserWithUser(@Param("user") User user);

    @Query(value = "SELECT c FROM Comment c JOIN FETCH c.user WHERE c.post.id = :postId",
            countQuery = "SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    Page<Comment> findByPostIdWithUser(@Param("postId") Long postId, Pageable pageable);
}

