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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private static final long VIEW_KEY_TTL_SECONDS = 300;

    public void generateTestPosts() {
        String[] keywordsKor = {"축구", "야구", "농구"};
        String[] categoriesEng = {"SOCCER", "BASEBALL", "BASKETBALL"};
        List<Post> allPosts = new ArrayList<>(200_000);

        LocalDateTime baseTime = LocalDateTime.now();
        long postCounter = 0;

        int totalPosts = 200_000;
        int users = 1000;
        int postsPerUser = totalPosts / users; // 200개씩

        for (int userIndex = 1; userIndex <= users; userIndex++) {
            User user = userRepository.findByUsername("user" + userIndex)
                    .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

            for (int postIndex = 1; postIndex <= postsPerUser; postIndex++) {
                int keywordIndex = (postIndex - 1) % keywordsKor.length;

                String title = keywordsKor[keywordIndex] + " 테스트 제목 " + userIndex + "-" + postIndex;
                String categoryEng = categoriesEng[keywordIndex];

                Post post = Post.builder()
                        .title(title) // 제목에 한글 키워드
                        .content("테스트 내용입니다. 작성자: " + user.getUsername())
                        .category(Category.valueOf(categoryEng)) // 카테고리 영문
                        .user(user)
                        .likeCount(0)
                        .viewCount(0)
                        .build();

                // createdAt을 1초씩 증가
                post.setCreatedAt(baseTime.plusSeconds(postCounter));

                allPosts.add(post);
                postCounter++;
            }
        }

        postRepository.saveAll(allPosts);
    }



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

    public PostRes.SavePostDto savePostTest(String username, PostReq.SavePostTestDto savePostTestDto) {
        User user = userRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
        Post post = Post.builder()
                .title(savePostTestDto.getTitle())
                .content(savePostTestDto.getContent())
                .category(Category.valueOf(savePostTestDto.getCategory()))
                .likeCount(0)
                .viewCount(1)
                .user(user)
                .build();
        postRepository.save(post);

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

    @Cacheable(value = "postDetail", key = "#postId")
    @Transactional(readOnly = true)
    public PostRes.GetPostDetailDto loadPostDetail(long postId) {
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new RuntimeException("해당 게시물을 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        Page<Comment> commentPage = commentRepository.findByPostIdWithUser(postId, pageable);

        List<CommentRes.CommentDto> comments = commentPage.getContent()
                .stream().map(CommentRes::toCommentDto).toList();

        List<String> imageUrls = postImageRepository.findAllByPostId(postId)
                .stream().map(PostImage::getImageUrl).toList();

        return PostRes.GetPostDetailDto.builder()
                .nickname(post.getUser().getNickname())
                .category(post.getCategory().name())
                .title(post.getTitle())
                .content(post.getContent())
                .likeCount(post.getLikeCount())
                .viewCount(post.getViewCount()) // DB 기준 값
                .totalCommentCount(commentPage.getTotalElements())
                .totalCommentPageCount(commentPage.getTotalPages())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .imageUrls(imageUrls)
                .comments(comments)
                .build();
    }

    /** 컨트롤러가 호출하는 진입점: 조회수 증가 + 캐시된 상세 + delta 합산 */
    @Transactional(readOnly = true)
    public PostRes.GetPostDetailDto getPostDetail(long postId, HttpServletRequest request) {
        // 1) 중복 방지 + Redis 델타 증가 (DB는 바로 안 건드림)
        increaseViewCount(postId, request);

        // 2) 상세는 매번 DB에서 조회 (캐싱 없음)
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new RuntimeException("해당 게시물을 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        Page<Comment> commentPage = commentRepository.findByPostIdWithUser(postId, pageable);

        List<CommentRes.CommentDto> comments = commentPage.getContent()
                .stream().map(CommentRes::toCommentDto).toList();

        List<String> imageUrls = postImageRepository.findAllByPostId(postId)
                .stream().map(PostImage::getImageUrl).toList();

        // 3) 응답의 viewCount는 DB 기준 값만 사용 (Redis 델타 합산 X)
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

    /** 로그인 유저=userId, 비로그인=쿠키(ANON_ID), 최후수단=IP로 중복 방지하여 delta 증가 */
    public void increaseViewCount(Long postId, HttpServletRequest request) {
        String who = resolveViewerIdentifier(request); // "user:123" / "anon:uuid" / "ip:1.2.3.4"
        String lockKey = "viewed:" + who + ":" + postId;   // 중복 방지 키
        String counterKey = "post:views:" + postId;        // 델타 카운터 키

        Boolean firstTime = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", VIEW_KEY_TTL_SECONDS, TimeUnit.SECONDS); // NX + EX(5분)
        if (Boolean.TRUE.equals(firstTime)) {
            redisTemplate.opsForValue().increment(counterKey); // 최초 조회만 +1
        }
    }

    /** viewer 식별자: userId(로그인) > 쿠키(ANON_ID) > IP */
    private String resolveViewerIdentifier(HttpServletRequest request) {
        // 1) 로그인 유저
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() != null
                    && !"anonymousUser".equals(String.valueOf(auth.getPrincipal()))) {

                // CustomUserDetails#getId()가 있다면 우선 사용
                Object principal = auth.getPrincipal();
                try {
                    var method = principal.getClass().getMethod("getId");
                    Object id = method.invoke(principal);
                    if (id != null) return "user:" + id.toString();
                } catch (NoSuchMethodException ignore) {
                    // 없으면 username 사용
                    String name = auth.getName();
                    if (name != null && !name.isBlank()) return "user:" + name;
                }
            }
        } catch (Exception ignored) {}

        // 2) 비로그인: 쿠키(ANON_ID)
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("ANON_ID".equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
                    return "anon:" + c.getValue();
                }
            }
        }

        // 3) 최후: IP
        String forwarded = request.getHeader("X-Forwarded-For");
        String ip = (forwarded != null && !forwarded.isBlank())
                ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
        return "ip:" + ip;
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

