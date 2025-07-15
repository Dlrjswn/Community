package com.example.community.domain.post;

import com.example.community.domain.post.dto.PostPreviewProjection;
import com.example.community.domain.post.dto.PostReq;
import com.example.community.domain.post.dto.PostRes;
import com.example.community.domain.post.entity.Post;
import com.example.community.domain.post.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Rollback(false)
public class SearchPostTest {

    @Autowired
    private PostRepository postRepository;


    @Test
    public void 게시물_검색_테스트(){
        PostReq.SearchPostListDto dto = PostReq.SearchPostListDto.builder()
                .page(0)
                .pageSize(20)
                .keyword("Post")
                .build();

        Pageable pageable = PageRequest.of(
                dto.getPage(),
                dto.getPageSize()
        );

        long start = System.nanoTime();

        Page<PostPreviewProjection> result = postRepository.findByTitleContainingWithUser(dto.getKeyword(),pageable);

        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;
        System.out.println("[성능] 게시물 검색 소요 시간: " + durationMs + "ms");
        System.out.println("[성능] 게시물 개수: " + result.getTotalElements());
        System.out.println("[성능] 페이지 개수: " + result.getTotalPages());

        Assertions.assertFalse(result.isEmpty());

    }

}
