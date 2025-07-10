package com.example.community.domain.post;


import com.example.community.domain.post.entity.Category;
import com.example.community.domain.post.entity.Post;
import com.example.community.domain.post.repository.PostRepository;
import com.example.community.domain.user.entity.Role;
import com.example.community.domain.user.entity.User;
import com.example.community.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
    class PostRepositoryNPlusOneTest {

        @Autowired
        private PostRepository postRepository;

        @Autowired
        private UserRepository userRepository;

        @PersistenceContext
        private EntityManager em;

        @BeforeEach
        void setUp() {
            // 테스트용 유저 10명 + 게시글 10개 생성
            for (int i = 1; i <= 10; i++) {
                User user = userRepository.save(User.builder()
                        .email("lee"+i+"@gmail.com")
                        .role(Role.USER)
                        .nickname("Apple"+i)
                        .username("lee"+i)
                        .password("1234")
                        .build());

                postRepository.save(Post.builder()
                        .user(user)
                        .title("by Lee"+i)
                        .content("by Apple"+i)
                        .category(Category.BASEBALL)
                        .likeCount(0)
                        .viewCount(0)
                        .build());
            }

            // 영속성 컨텍스트 초기화 → Lazy 로딩 강제로 유도
            em.flush();
            em.clear();
        }

        @Test
        void nPlusOne_문제_테스트() {
            List<Post> posts = postRepository.findAll(); // 여기서 post만 가져옴

            for (Post post : posts) {
                // user는 LAZY 로딩이라 여기에 접근하는 순간 추가 쿼리 발생
                System.out.println("작성자 이름: " + post.getUser().getUsername());
            }

            // 콘솔에 찍히는 SQL 로그를 통해 쿼리 횟수가 1 + N인지 확인
        }
    }

