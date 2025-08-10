package com.example.community.domain.post.service;

import com.example.community.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ViewCountSyncScheduler {

    private final StringRedisTemplate redisTemplate;

    private final PostRepository postRepository;

    // 1분마다 실행 (원하는 주기로 조절 가능)
    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void syncViewCountToDB() {
        // post:views:* 키들을 SCAN으로 순회
        String pattern = "post:views:*";
        String cursor = "0";
        do {
            ScanOptions options = ScanOptions.scanOptions().match(pattern).count(500).build();
            Cursor<byte[]> scan = redisTemplate.getConnectionFactory()
                    .getConnection().scan(options);

            while (scan.hasNext()) {
                String key = new String(scan.next());
                String postIdStr = key.substring("post:views:".length());
                Long postId = Long.parseLong(postIdStr);

                String val = redisTemplate.opsForValue().get(key);
                if (val == null) continue;

                int delta = Integer.parseInt(val);
                postRepository.increaseViewCount(postId, delta);
                redisTemplate.delete(key);
            }
            cursor = "0"; // Spring Data Redis scan 커서 관리 단순화 (위 방식이면 한 번에 소화)
        } while (!"0".equals(cursor));
    }
}