package com.example.community.domain.post.service;

import com.example.community.domain.post.dto.CommentRes;
import com.example.community.domain.post.dto.PostPreviewProjection;
import com.example.community.domain.post.dto.PostReq;
import com.example.community.domain.post.dto.PostRes;
import com.example.community.domain.post.entity.Category;
import com.example.community.domain.post.entity.Comment;
import com.example.community.domain.post.entity.Post;
import com.example.community.domain.post.entity.PostImage;
import com.example.community.domain.post.repository.CommentRepository;
import com.example.community.domain.post.repository.PostImageRepository;
import com.example.community.domain.post.repository.PostRepository;
import com.example.community.domain.user.entity.User;
import com.example.community.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final CommentRepository commentRepository;

    private final StringRedisTemplate redisTemplate;

    private static final long EXPIRE_SECONDS = 60 * 5;

    public PostRes.SavePostDto savePost(String username, PostReq.SavePostDto savePostDto) {
        User user = userRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
        Post post = Post.builder()
                .title(savePostDto.getTitle())
                .content(savePostDto.getContent())
                .category(Category.valueOf(savePostDto.getCategory()))
                .likeCount(0)
                .viewCount(1)
                .user(user)
                .build();
        postRepository.save(post);

        for(String imageUrl : savePostDto.getImageUrls()) {
            PostImage postImage = PostImage.builder()
                    .imageUrl(imageUrl)
                    .post(post)
                    .build();
            postImageRepository.save(postImage);
        }



        return PostRes.SavePostDto.builder()
                .createdAt(post.getCreatedAt())
                .build();


    }

    public PostRes.ModifyPostDto modifyPost(PostReq.ModifyPostDto modifyPostDto) {
        Post post = postRepository.findById(modifyPostDto.getPostId()).orElseThrow(()-> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

        for(Long removedImageId : modifyPostDto.getRemovedImageIds()){
            postImageRepository.deleteById(removedImageId);
        }


        for(String addedImageUrl : modifyPostDto.getAddedImageUrls()){
            PostImage postImage = PostImage.builder()
                    .imageUrl(addedImageUrl)
                    .post(post)
                    .build();

            postImageRepository.save(postImage);
        }

        post.modifyTitle(modifyPostDto.getTitle());
        post.modifyContent(modifyPostDto.getContent());


        return PostRes.ModifyPostDto.builder()
                .modifiedAt(post.getModifiedAt())
                .build();


    }

    public PostRes.GetPostDetailDto getPostDetail(PostReq.GetPostDetailDto getPostDetailDto) {
        Post post = postRepository.findByIdWithUser(getPostDetailDto.getPostId()).orElseThrow(()->new RuntimeException("해당 게시물을 찾을 수 없습니다."));
        postRepository.incrementViewCount(post.getId());
        Pageable pageable = PageRequest.of(0,20,Sort.by("createdAt").descending());
        Page<Comment> commentPage =  commentRepository.findByPostIdWithUser(post.getId(),pageable);
        List<CommentRes.CommentDto> comments = commentPage.getContent().stream().map(CommentRes::toCommentDto).toList();
        List<String> imageUrls = postImageRepository.findAllByPostId(post.getId()).stream().map(PostImage::getImageUrl).toList();

        return PostRes.GetPostDetailDto.builder()
                .nickname(post.getUser().getNickname())
                .category(post.getCategory().name())
                .title(post.getTitle())
                .content(post.getContent())
                .likeCount(post.getLikeCount())
                .viewCount(post.getViewCount())
                .totalCommentCount(commentPage.getTotalElements())
                .totalCommentPageCount(commentPage.getTotalPages())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .imageUrls(imageUrls)
                .comments(comments)
                .build();
    }

   public void increaseViewCountByIp(Long postId, HttpServletRequest request) {
        String ip = extractClientIp(request);
        String key = "viewed:ip:" + ip + ":" + postId;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return; // 이미 조회한 기록 있음
        }

        redisTemplate.opsForValue().set(key, "1", EXPIRE_SECONDS, TimeUnit.SECONDS); // 5분 중복 방지
        redisTemplate.opsForValue().increment("post:views:" + postId); // 조회수 증가
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    // 카테고리별
    public Page<PostRes.PostPreviewDto> getPostList(PostReq.GetPostListDto getPostListDto) {
        return postRepository.getPostsWithUser(getPostListDto).map(PostRes::toPostPreviewDto);
    }

    public List<PostRes.PostPreviewDto> getMyPostList(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("해당 사용자를 찾을 수 없습니다."));
        return postRepository.findAllByUserWithUser(user).stream().map(PostRes::toPostPreviewDto).toList();
    }

    public Page<PostRes.PostPreviewDto> searchPostList(PostReq.SearchPostListDto savePostListDto) {
        Pageable pageable = PageRequest.of(
                savePostListDto.getPage(),
                savePostListDto.getPageSize()
        );
        return postRepository.findByTitleContainingWithUser(savePostListDto.getKeyword(), pageable).map(PostRes::toPostPreviewDto);
    }
}
