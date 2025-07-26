package com.example.community.domain.post;

import com.example.community.domain.post.dto.PostReq;
import com.example.community.domain.post.entity.Post;
import com.example.community.domain.post.repository.PostRepository;
import com.example.community.domain.post.service.PostService;
import com.example.community.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*
@SpringBootTest
@ActiveProfiles("test")
class GetPostDetailTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;



    @Test
    void 조회수_동시성_테스트() throws InterruptedException {
        int numberOfThreads = 300;
        Long postId = postRepository.findById(1L).get().getId();
        PostReq.GetPostDetailDto dto = PostReq.GetPostDetailDto.builder().postId(postId).build();

        ExecutorService executor = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.execute(() -> {
                try {
                    postService.getPostDetail(dto);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 쓰레드 작업 완료 대기

        Post updatedPost = postRepository.findById(postId).orElseThrow();
        System.out.println("최종 조회수: " + updatedPost.getViewCount());

        Assertions.assertEquals(numberOfThreads+100, updatedPost.getViewCount());
    }
}
*/