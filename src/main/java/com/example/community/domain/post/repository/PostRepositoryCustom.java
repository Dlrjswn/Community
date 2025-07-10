package com.example.community.domain.post.repository;

import com.example.community.domain.post.dto.PostReq;
import com.example.community.domain.post.entity.Post;
import org.springframework.data.domain.Page;

public interface PostRepositoryCustom {
    Page<Post> getPostsWithUser(PostReq.GetPostListDto getPostListDto);
}
