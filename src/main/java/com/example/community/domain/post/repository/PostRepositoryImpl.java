package com.example.community.domain.post.repository;

import com.example.community.domain.post.dto.PostReq;
import com.example.community.domain.post.entity.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final EntityManager em;

    @Override
    public Page<Post> getPostsWithUser(PostReq.GetPostListDto dto) {

        // 1) 본문 JPQL (user 즉시 로딩 + 중복 제거)
        StringBuilder jpql = new StringBuilder(
                "SELECT DISTINCT p FROM Post p " +
                        "JOIN FETCH p.user " +              // ← N+1 해결
                        "WHERE 1=1"
        );

        // 2) 카운트 JPQL (fetch 불가)
        StringBuilder countJpql = new StringBuilder(
                "SELECT COUNT(p) FROM Post p WHERE 1=1"
        );

        Map<String, Object> params = new HashMap<>();

        // ▸ 카테고리 필터
        if (dto.getCategory() != null && !dto.getCategory().isBlank()) {
            jpql.append(" AND p.category = :category");
            countJpql.append(" AND p.category = :category");
            params.put("category", dto.getCategory());
        }

        // ▸ 정렬
        if ("like".equalsIgnoreCase(dto.getSort())) {
            jpql.append(" ORDER BY p.likeCount DESC");
        } else {
            jpql.append(" ORDER BY p.createdAt DESC");
        }

        /* ---------- 본문 쿼리 ---------- */
        TypedQuery<Post> query = em.createQuery(jpql.toString(), Post.class);
        params.forEach(query::setParameter);

        // Hibernate 6: DISTINCT 를 메모리에서 처리하도록 힌트
        query.setHint("hibernate.query.passDistinctThrough", false);

        query.setFirstResult(dto.getPage() * dto.getPageSize());
        query.setMaxResults(dto.getPageSize());

        /* ---------- 카운트 쿼리 ---------- */
        TypedQuery<Long> countQuery = em.createQuery(countJpql.toString(), Long.class);
        params.forEach(countQuery::setParameter);
        long total = countQuery.getSingleResult();

        return new PageImpl<>(
                query.getResultList(),
                PageRequest.of(dto.getPage(), dto.getPageSize()),
                total
        );
    }
}


