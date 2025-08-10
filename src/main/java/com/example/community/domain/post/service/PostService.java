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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    private static final long VIEW_IP_TTL_SECONDS = 300;

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
        increaseViewCountByIp(postId, request); // 중복 방지 + delta 증가

        PostRes.GetPostDetailDto dto = loadPostDetail(postId); // 캐시 활용

        String deltaStr = redisTemplate.opsForValue().get("post:views:" + postId);
        int delta = (deltaStr == null) ? 0 : Integer.parseInt(deltaStr);

        // DTO가 빌더를 지원하지 않으면, 새 builder로 복사 생성
        return PostRes.GetPostDetailDto.builder()
                .nickname(dto.getNickname())
                .category(dto.getCategory())
                .title(dto.getTitle())
                .content(dto.getContent())
                .likeCount(dto.getLikeCount())
                .viewCount(dto.getViewCount() + delta) // DB + 미반영분
                .totalCommentCount(dto.getTotalCommentCount())
                .totalCommentPageCount(dto.getTotalCommentPageCount())
                .createdAt(dto.getCreatedAt())
                .modifiedAt(dto.getModifiedAt())
                .imageUrls(dto.getImageUrls())
                .comments(dto.getComments())
                .build();
    }

    /** 원자적 SET NX EX + delta 증가 */
    public void increaseViewCountByIp(Long postId, HttpServletRequest request) {
        String ip = extractClientIp(request);
        String key = "viewed:ip:" + ip + ":" + postId;

        Boolean firstTime = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", VIEW_IP_TTL_SECONDS, TimeUnit.SECONDS); // NX + EX
        if (Boolean.TRUE.equals(firstTime)) {
            redisTemplate.opsForValue().increment("post:views:" + postId);
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
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

