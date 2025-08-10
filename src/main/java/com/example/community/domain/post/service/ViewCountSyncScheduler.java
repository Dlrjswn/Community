package com.example.community.domain.post.service;

import com.example.community.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.connection.RedisConnection;

import java.util.*;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ViewCountSyncScheduler {

    private final StringRedisTemplate redisTemplate;
    private final PostRepository postRepository;

    @Scheduled(fixedRate = 60_000) // 1분
    @Transactional
    public void syncViewCountToDB() {
        final String pattern = "post:views:*";

        RedisConnection conn = redisTemplate.getConnectionFactory().getConnection();
        ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern).count(500).build();

        try (var cursor = conn.scan(scanOptions)) {
            while (cursor.hasNext()) {
                String key = new String(cursor.next(), StandardCharsets.UTF_8);
                String postIdStr = key.substring("post:views:".length());
                Long postId = Long.parseLong(postIdStr);

                String val = redisTemplate.opsForValue().get(key);
                if (val == null) continue;

                int delta = Integer.parseInt(val);
                postRepository.increaseViewCount(postId, delta); // 누적 증가
                redisTemplate.delete(key); // 처리 완료 후 삭제
            }
        } catch (Exception e) {
        }
    }
}