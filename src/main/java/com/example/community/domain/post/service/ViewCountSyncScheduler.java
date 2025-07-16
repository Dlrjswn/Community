package com.example.community.domain.post.service;

import com.example.community.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
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
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void syncViewCountToDB() {
        // Redis에 저장된 모든 조회수 키 가져오기
        Set<String> keys = redisTemplate.keys("post:views:*");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            try {
                // 키에서 postId 추출
                String postIdStr = key.replace("post:views:", "");
                Long postId = Long.parseLong(postIdStr);

                // Redis에서 조회수 값 가져오기
                String value = redisTemplate.opsForValue().get(key);
                if (value == null) continue;

                Integer viewCount = Integer.parseInt(value);

                // DB에 조회수 누적 반영
                postRepository.increaseViewCount(postId, viewCount);

                // 처리 완료 후 Redis 키 삭제
                redisTemplate.delete(key);

            } catch (Exception e) {
                // 로그 기록 후 계속 처리
                // (예: NumberFormatException, DB 예외 등)
                e.printStackTrace();
            }
        }
    }
}