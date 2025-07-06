package com.example.community.domain.post.service;

import com.example.community.domain.post.dto.PostRequest;
import com.example.community.domain.post.dto.PostResponse;
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

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    public PostResponse.SaveDto savePost(String username, PostRequest.SaveDto saveDto) {
        User user = userRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
        Post post = Post.builder()
                .title(saveDto.getTitle())
                .content(saveDto.getContent())
                .category(Category.valueOf(saveDto.getCategory()))
                .likeCount(0)
                .viewCount(1)
                .user(user)
                .build();
        postRepository.save(post);

        for(String imageUrl : saveDto.getImageUrls()) {
            PostImage postImage = PostImage.builder()
                    .imageUrl(imageUrl)
                    .post(post)
                    .build();
            postImageRepository.save(postImage);
        }



        return PostResponse.SaveDto.builder()
                .createdAt(post.getCreatedAt())
                .message("게시글 저장 완료")
                .build();


    }

    public PostResponse.ModifyDto modifyPost(PostRequest.ModifyDto modifyDto) {
        Post post = postRepository.findById(modifyDto.getPostId()).orElseThrow(()-> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

        for(Long removedImageId : modifyDto.getRemovedImageIds()){
            postImageRepository.deleteById(removedImageId);
        }


        for(String addedImageUrl : modifyDto.getAddedImageUrls()){
            PostImage postImage = PostImage.builder()
                    .imageUrl(addedImageUrl)
                    .post(post)
                    .build();

            postImageRepository.save(postImage);
        }

        post.modifyTitle(modifyDto.getTitle());
        post.modifyContent(modifyDto.getContent());


        return PostResponse.ModifyDto.builder()
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .message("게시물 수정 완료")
                .build();


    }
}
