package com.example.community.domain.post.service;

import com.example.community.domain.post.dto.PostReq;
import com.example.community.domain.post.dto.PostRes;
import com.example.community.domain.post.entity.Category;
import com.example.community.domain.post.entity.Post;
import com.example.community.domain.post.entity.PostImage;
import com.example.community.domain.post.repository.PostImageRepository;
import com.example.community.domain.post.repository.PostRepository;
import com.example.community.domain.user.entity.User;
import com.example.community.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
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
                .message("게시글 저장 완료")
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
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .message("게시물 수정 완료")
                .build();


    }
}
