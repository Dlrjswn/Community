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
    public Page<Post> getPosts(PostReq.GetPostListDto getPostListDto) {
        StringBuilder jpql = new StringBuilder("SELECT p FROM Post p WHERE 1=1");
        StringBuilder countJpql = new StringBuilder("SELECT COUNT(p) FROM Post p WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        // 카테고리 조건
        if (getPostListDto.getCategory() != null && !getPostListDto.getCategory().isBlank()) {
            jpql.append(" AND p.category = :category");
            countJpql.append(" AND p.category = :category");
            params.put("category", getPostListDto.getCategory());
        }

        // 정렬 조건
        if ("like".equalsIgnoreCase(getPostListDto.getSort())) {
            jpql.append(" ORDER BY p.likeCount DESC");
        } else {
            jpql.append(" ORDER BY p.createdAt DESC");
        }

        // 본문 쿼리
        TypedQuery<Post> query = em.createQuery(jpql.toString(), Post.class);
        params.forEach(query::setParameter);
        query.setFirstResult(getPostListDto.getPage() * getPostListDto.getPageSize());
        query.setMaxResults(getPostListDto.getPageSize());

        // 카운트 쿼리
        TypedQuery<Long> countQuery = em.createQuery(countJpql.toString(), Long.class);
        params.forEach(countQuery::setParameter);
        Long total = countQuery.getSingleResult();

        return new PageImpl<>(query.getResultList(), PageRequest.of(getPostListDto.getPage(), getPostListDto.getPageSize()), total);
    }
}


