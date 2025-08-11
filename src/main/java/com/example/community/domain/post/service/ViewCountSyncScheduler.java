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

        var conn = redisTemplate.getConnectionFactory().getConnection();
        var scanOptions = ScanOptions.scanOptions().match(pattern).count(500).build();

        try (var cursor = conn.scan(scanOptions)) {
            while (cursor.hasNext()) {
                String key = new String(cursor.next(), java.nio.charset.StandardCharsets.UTF_8);
                Long postId = Long.parseLong(key.substring("post:views:".length()));

                String val = redisTemplate.opsForValue().get(key);
                if (val == null) continue;

                int delta = Integer.parseInt(val);            // int 사용 요청 반영
                postRepository.increaseViewCount(postId, delta); // view_count += :delta
                redisTemplate.delete(key);
            }
        } catch (Exception e) {
        }
    }
}