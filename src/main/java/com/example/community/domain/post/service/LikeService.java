package com.example.community.domain.post.service;

import com.example.community.domain.post.dto.LikeReq;
import com.example.community.domain.post.dto.LikeRes;
import com.example.community.domain.post.entity.Like;
import com.example.community.domain.post.entity.Post;
import com.example.community.domain.post.repository.LikeRepository;
import com.example.community.domain.post.repository.PostRepository;
import com.example.community.domain.user.entity.User;
import com.example.community.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public LikeRes.LikePostDto likePost(LikeReq.LikePostDto likePostDto, String username) {
        Post post = postRepository.findById(likePostDto.getPostId()).orElseThrow(()->new RuntimeException("해당 게시물을 찾을 수 없습니다."));
        User user = userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("해당 사용자를 찾을 수 없습니다."));


        if(likeRepository.existsByUserAndPost(user,post)){
            return LikeRes.LikePostDto.builder()
                    .message("이미 좋아요를 눌렀습니다.")
                    .build();
        }
        else {
            Like like = Like.builder()
                    .post(post)
                    .user(user)
                    .build();
            likeRepository.save(like);

            post.increaseLikeCount();

            return LikeRes.LikePostDto.builder()
                    .message("좋아요 +1")
                    .build();
        }

    }
}
