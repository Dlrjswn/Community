package com.example.community.domain.post.service;


import com.example.community.domain.post.dto.CommentReq;
import com.example.community.domain.post.dto.CommentRes;
import com.example.community.domain.post.entity.Comment;
import com.example.community.domain.post.entity.Post;
import com.example.community.domain.post.repository.CommentRepository;
import com.example.community.domain.post.repository.PostRepository;
import com.example.community.domain.user.entity.User;
import com.example.community.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public CommentRes.SaveCommentDto saveComment(String username, CommentReq.SaveCommentDto saveCommentDto) {
        User user = userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("해당 사용자를 찾을 수 없습니다."));
        Post post = postRepository.findById(saveCommentDto.getPostId()).orElseThrow(()->new RuntimeException("해당 게시물을 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .content(saveCommentDto.getContent())
                .user(user)
                .post(post)
                .build();

        commentRepository.save(comment);

        return CommentRes.SaveCommentDto.builder()
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public CommentRes.ModifyCommentDto modifyComment(CommentReq.ModifyCommentDto modifyCommentDto) {
        Comment comment = commentRepository.findById(modifyCommentDto.getCommentId()).orElseThrow(()->new RuntimeException("해당 댓글을 찾을 수 없습니다."));
        comment.modifyContent(modifyCommentDto.getContent());

        return CommentRes.ModifyCommentDto.builder()
                .modifiedAt(comment.getModifiedAt())
                .build();
    }

    public List<CommentRes.CommentDto> getMyCommentList(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("해당 사용자를 찾을 수 없습니다."));
        return commentRepository.findAllByUserWithUser(user).stream().map(CommentRes::toCommentDto).toList();

    }


    public Page<CommentRes.CommentDto> getCommentList(CommentReq.GetCommentDto getCommentDto) {
        Post post = postRepository.findById(getCommentDto.getPostId()).orElseThrow(()-> new RuntimeException("해당 게시물을 찾을 수 없습니다."));
        Pageable pageable = PageRequest.of(getCommentDto.getPage(),getCommentDto.getPageSize(), Sort.by("createdAt").descending());

        return commentRepository.findByPostIdWithUser(post.getId(),pageable).map(CommentRes::toCommentDto);
    }
}
