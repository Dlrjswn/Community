package com.example.community.domain.post;


import com.example.community.domain.post.dto.PostLikeReq;
import com.example.community.domain.post.entity.Category;
import com.example.community.domain.post.entity.Post;
import com.example.community.domain.post.repository.PostRepository;
import com.example.community.domain.post.service.PostLikeService;
import com.example.community.domain.user.entity.Role;
import com.example.community.domain.user.entity.User;
import com.example.community.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
    @Transactional
    @ActiveProfiles("test")
    class LikePostTest {

        @Autowired
        private PostRepository postRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PostLikeService postLikeService;

        private final int threadCount = 300;
        private Post post;

        @BeforeEach
        void setUp() {
            // 게시물 생성자
            User user = userRepository.save(User.builder()
                    .username("author")
                    .password("1234")
                    .email("author"+"@gmail.com")
                    .nickname("authorLee")
                    .role(Role.USER)
                    .build());
            // 게시물 생성
            post = postRepository.save(Post.builder()
                    .title("동시성 테스트용 게시물")
                    .content("좋아요 테스트")
                    .viewCount(0)
                    .likeCount(0)
                    .category(Category.BASEBALL)
                    .user(user)
                    .build());

            // 사용자 300명 생성
            for (int i = 1; i <= threadCount; i++) {
                User users = User.builder()
                        .username("user" + i)
                        .password("pass")
                        .nickname("닉" + i)
                        .email("user" + i + "@test.com")
                        .role(Role.USER)
                        .build();
                userRepository.save(users);
            }

            userRepository.flush();
        }

        @Test
        @Transactional(propagation = Propagation.NOT_SUPPORTED) // 트랜잭션 끔
        void 동시에_좋아요_요청_테스트() throws InterruptedException {
            ExecutorService executorService = Executors.newFixedThreadPool(32);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int i = 1; i <= threadCount; i++) {
                int userId = i;
                executorService.submit(() -> {
                    try {
                        String username = "user" + userId;
                        postLikeService.likePost(PostLikeReq.LikePostDto.builder().postId(post.getId()).build(), username);
                    } catch (Exception e) {
                        System.out.println("좋아요 실패: " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executorService.shutdown();

            int likeCount = postRepository.findById(post.getId()).get().getLikeCount();
            System.out.println("최종 좋아요 수: " + likeCount);

            assertThat(likeCount).isEqualTo(300); // 유저 300명 = 좋아요 300건
        }
    }

