package com.ktb.community.service;

import com.ktb.community.dto.request.CreateCommentRequestDto;
import com.ktb.community.dto.response.CreateCommentResponseDto;
import com.ktb.community.entity.Comment;
import com.ktb.community.entity.Post;
import com.ktb.community.entity.User;
import com.ktb.community.exception.custom.PostNotFoundException;
import com.ktb.community.exception.custom.UserNotFoundException;
import com.ktb.community.jwt.JwtUtil;
import com.ktb.community.repository.CommentRepository;
import com.ktb.community.repository.PostRepository;
import com.ktb.community.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
public class CommentService {
    CommentRepository commentRepository;
    PostRepository postRepository;
    UserRepository userRepository;
    JwtUtil jwtUtil;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public CreateCommentResponseDto writeComment(Long postId, String token, CreateCommentRequestDto createCommentRequestDto) {
        Long userId = this.jwtUtil.extractUserIdFromToken(token);
        Post post = this.postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Not found post"));
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Not found user"));

        Comment comment = new Comment();
        comment.setContent(createCommentRequestDto.getContent());
        comment.setPost(post);
        comment.setUser(user);


        Comment savedComment = this.commentRepository.save(comment);
        return new CreateCommentResponseDto(savedComment.getId());
    }
}
