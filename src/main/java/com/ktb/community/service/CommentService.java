package com.ktb.community.service;

import com.ktb.community.dto.request.CreateCommentRequestDto;
import com.ktb.community.dto.request.UpdateCommentRequestDto;
import com.ktb.community.dto.response.CrudCommentResponseDto;
import com.ktb.community.entity.Comment;
import com.ktb.community.entity.Post;
import com.ktb.community.entity.User;
import com.ktb.community.exception.custom.CommentNotFoundException;
import com.ktb.community.exception.custom.PostNotFoundException;
import com.ktb.community.exception.custom.UnauthorizedException;
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
    public CrudCommentResponseDto writeComment(Long postId, String token, CreateCommentRequestDto createCommentRequestDto) {
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
        return new CrudCommentResponseDto(savedComment.getId());
    }

    @Transactional
    public CrudCommentResponseDto modifyComment(String token, UpdateCommentRequestDto updateCommentRequestDto) {
        Long userId = this.jwtUtil.extractUserIdFromToken(token);
        // 작성자가 맞는지부터확인
        Comment comment = this.commentRepository.findById(updateCommentRequestDto.getCommentId())
                .orElseThrow(() -> new CommentNotFoundException("Not found comment"));

        if (!userId.equals(comment.getUser().getId())) {
            throw new UnauthorizedException("You are not authorized to modify this comment");
        }

        comment.setContent(updateCommentRequestDto.getContent());
        // @Transactional에 의해 자동으로 UPDATE 쿼리 실행 (Dirty Checking)
        return new CrudCommentResponseDto(comment.getId());
    }

    @Transactional
    public CrudCommentResponseDto removeComment(Long commentId, String token) {
        Long userId = this.jwtUtil.extractUserIdFromToken(token);
        Comment comment = this.commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Not found comment"));

        if (!userId.equals(comment.getUser().getId())) {
            throw new UnauthorizedException("You are not authorized to delete this comment");
        }

        comment.setDeletedAt(java.time.LocalDateTime.now());
        // @Transactional에 의해 자동으로 UPDATE 쿼리 실행 (Dirty Checking)

        return new CrudCommentResponseDto(commentId);
    }
}
