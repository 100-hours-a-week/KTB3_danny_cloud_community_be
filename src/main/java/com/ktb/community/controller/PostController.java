package com.ktb.community.controller;

import com.ktb.community.dto.request.CreateCommentRequestDto;
import com.ktb.community.dto.request.CreatePostRequestDto;
import com.ktb.community.dto.request.ModifyPostRequestDto;
import com.ktb.community.dto.request.UpdateCommentRequestDto;
import com.ktb.community.dto.response.*;
import com.ktb.community.service.CommentService;
import com.ktb.community.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping()
    public ResponseEntity<ApiResponseDto<CursorPageResponseDto<PostResponseDto>>> getPosts(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int size
    ) {
        CursorPageResponseDto<PostResponseDto> result = postService.getPostList(cursor, size);
        return ResponseEntity.ok(ApiResponseDto.success(result));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<PostDetailResponseDto>> getPostDetail(@PathVariable @Positive Long postId) {
        PostDetailResponseDto post = this.postService.getPostContent(postId);
        return ResponseEntity.ok().body(ApiResponseDto.success(post));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponseDto<?>> getComment(@PathVariable Long postId, @RequestParam(required = false) Long cursor,
                                                        @RequestParam(defaultValue = "5") int size) {
        CursorCommentResponseDto<CommentResponseDto> cursorCommentResponseDto = this.postService.getCommentList(postId, cursor, size);
        return ResponseEntity.ok().body(ApiResponseDto.success(cursorCommentResponseDto));
    }

    @PostMapping()
    public ResponseEntity<ApiResponseDto<CrudPostResponseDto>> createPost(@RequestBody @Valid CreatePostRequestDto createPostRequestDto, Authentication authentication) {
        CrudPostResponseDto crudPostResponseDto = this.postService.createPost(createPostRequestDto, authentication.getName());
        return ResponseEntity.ok().body(ApiResponseDto.success(crudPostResponseDto));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<?>> modifyPost(@PathVariable Long postId, @RequestBody ModifyPostRequestDto modifyPostRequestDto, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        CrudPostResponseDto modifiedPost = this.postService.modifyPostContent(postId, token, modifyPostRequestDto);
        return ResponseEntity.ok().body(ApiResponseDto.success(modifiedPost));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<?>> deletePost(@PathVariable Long postId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        CrudPostResponseDto crudPostResponseDto = this.postService.removePost(postId, token);
        return ResponseEntity.ok().body(ApiResponseDto.success(crudPostResponseDto));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponseDto<?>> createComment(@PathVariable Long postId, @RequestBody @Valid CreateCommentRequestDto createCommentRequestDto, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        CrudCommentResponseDto crudCommentResponseDto = this.commentService.writeComment(postId, token, createCommentRequestDto);
        return ResponseEntity.ok().body(ApiResponseDto.success(crudCommentResponseDto));
    }

    @PatchMapping("/{postId}/comments")
    public ResponseEntity<ApiResponseDto<?>> updateComment(@PathVariable Long postId, @RequestBody @Valid UpdateCommentRequestDto updateCommentRequestDto, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        CrudCommentResponseDto updateCommentResponseDto = this.commentService.modifyComment(token, updateCommentRequestDto);
        return ResponseEntity.ok().body(ApiResponseDto.success(updateCommentResponseDto));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponseDto<?>> deleteComment(@PathVariable Long commentId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        CrudCommentResponseDto deletedCommentResponseDto = this.commentService.removeComment(commentId, token);

        return ResponseEntity.ok().body(ApiResponseDto.success(deletedCommentResponseDto));
    }


}
