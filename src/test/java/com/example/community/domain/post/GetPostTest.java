package com.example.community.domain.post;

import com.example.community.domain.post.dto.PostReq;
import com.example.community.domain.post.dto.PostRes;
import com.example.community.domain.post.entity.Category;
import com.example.community.domain.post.entity.Post;
import com.example.community.domain.post.repository.PostLikeRepository;
import com.example.community.domain.post.repository.PostRepository;
import com.example.community.domain.user.entity.Role;
import com.example.community.domain.user.entity.User;
import com.example.community.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
/*
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Rollback(false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetPostTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    // @BeforeAll
    void setUp() {
        // 테스트용 사용자 1명
        User user = userRepository.save(
                User.builder()
                .username("testuser")
                        .email("testuser@example.com")
                        .password("1234")
                        .nickname("test")
                        .role(Role.USER)
                .build());

        // 1,000개의 게시물 생성 (카테고리 2종류, likeCount 랜덤)
        for (int i = 0; i < 10000; i++) {
            String category;
            int mod = i % 3;
            if (mod == 0) category = "BASEBALL";
            else if (mod == 1) category = "BASKETBALL";
            else category = "SOCCER";

            Post post = Post.builder()
                    .title("Post " + i)
                    .content("post1234")
                    .category(Category.valueOf(category))
                    .likeCount((int) (Math.random() * 100))
                    .viewCount(100)// 0~99
                    .user(user)
                    .build();

            postRepository.save(post);

            if (i % 1000 == 0) { // 주기적으로 flush/clear
                postRepository.flush();
                em.clear(); // 이 부분은 EntityManager 직접 사용 시 필요
            }
        }

    }

    @Test
    void 축구게시물_좋아요순_테스트() {
        PostReq.GetPostListDto dto = PostReq.GetPostListDto.builder()
                .category("SOCCER")
                .page(0)
                .pageSize(20)
                .sort("like")
                .build();

        long start = System.nanoTime();

        Page<PostRes.PostPreviewDto> result = postRepository.getPostsWithUser(dto).map(PostRes::toPostPreviewDto);

        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;
        System.out.println("[성능] 좋아요순 조회 소요 시간: " + durationMs + "ms");

        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void 야구게시물_최신순_테스트() {
        PostReq.GetPostListDto dto = PostReq.GetPostListDto.builder()
                .category("BASEBALL")
                .page(0)
                .pageSize(20)
                .sort("latest")
                .build();

        long start = System.nanoTime();

        Page<PostRes.PostPreviewDto> result = postRepository.getPostsWithUser(dto).map(PostRes::toPostPreviewDto);

        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;
        System.out.println("[성능] 최신순 조회 소요 시간: " + durationMs + "ms");

        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void 전체게시물_좋아요순_테스트() {
        PostReq.GetPostListDto dto = PostReq.GetPostListDto.builder()
                .category(null)
                .page(0)
                .pageSize(20)
                .sort("like")
                .build();

        long start = System.nanoTime();

        Page<PostRes.PostPreviewDto> result = postRepository.getPostsWithUser(dto).map(PostRes::toPostPreviewDto);

        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;
        System.out.println("[성능] 좋아요순 조회 소요 시간: " + durationMs + "ms");

        Assertions.assertFalse(result.isEmpty());
    }
}
*/
