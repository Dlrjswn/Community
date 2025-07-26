package com.example.community.domain.post.repository;

import com.example.community.domain.post.dto.PostPreviewProjection;
import com.example.community.domain.post.dto.PostRes;
import com.example.community.domain.post.entity.Post;
import com.example.community.domain.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>,PostRepositoryCustom {
    @Query("select p from Post p join fetch p.user where p.user = :user")
    List<Post> findAllByUserWithUser(@Param("user") User user);

    @Query(
            value = """
        SELECT
            p.id AS postId,
            u.nickname AS nickname,
            p.category AS category,
            p.title AS title,
            p.likeCount AS likeCount,
            p.viewCount AS viewCount,
            p.createdAt AS createdAt
        FROM Post p
        JOIN p.user u
        WHERE p.title LIKE CONCAT('%', :keyword, '%')
        ORDER BY p.createdAt DESC
        """,
            countQuery = """
        SELECT COUNT(p)
        FROM Post p
        WHERE p.title LIKE CONCAT('%', :keyword, '%')
        """
    )
    Page<PostPreviewProjection> findByTitleContainingWithUser(@Param("keyword") String keyword, Pageable pageable);



    @Query("select p from Post p join fetch p.user where p.id = :postId")
    Optional<Post> findByIdWithUser(@Param("postId") Long postId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Post p WHERE p.id = :postId")
    Optional<Post> findByIdForUpdate(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + :count WHERE p.id = :postId")
    void increaseViewCount(@Param("postId") Long postId, @Param("count") Integer count);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);
}
