package com.example.community.domain.post.service;

import com.example.community.domain.post.dto.PostLikeReq;
import com.example.community.domain.post.dto.PostLikeRes;
import com.example.community.domain.post.entity.Post;
import com.example.community.domain.post.entity.PostLike;
import com.example.community.domain.post.repository.PostLikeRepository;
import com.example.community.domain.post.repository.PostRepository;
import com.example.community.domain.user.entity.User;
import com.example.community.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public PostLikeRes.LikePostDto likePost(PostLikeReq.LikePostDto likePostDto, String username) {
        Post post = postRepository.findByIdForUpdate(likePostDto.getPostId()).orElseThrow(() -> new RuntimeException("해당 게시물을 찾을 수 없습니다."));
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        if (postLikeRepository.existsByUserAndPost(user, post)) {
            return PostLikeRes.LikePostDto.builder()
                    .message("이미 좋아요를 눌렀습니다.")
                    .build();
        }


        postLikeRepository.save(PostLike.builder().post(post).user(user).build());
        post.increaseLikeCount();

        return PostLikeRes.LikePostDto.builder()
                .message("좋아요 +1")
                .build();
    }
}