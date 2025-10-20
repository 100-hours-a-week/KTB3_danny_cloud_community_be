package com.ktb.community.controller;

import com.ktb.community.dto.request.CreatePostRequestDto;
import com.ktb.community.dto.request.ModifyPostRequestDto;
import com.ktb.community.dto.response.*;
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

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
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
    public ResponseEntity<ApiResponseDto<CreatePostResponseDto>> createPost(@RequestBody @Valid CreatePostRequestDto createPostRequestDto, Authentication authentication) {
        CreatePostResponseDto createPostResponseDto = this.postService.createPost(createPostRequestDto, authentication.getName());
        return ResponseEntity.ok().body(ApiResponseDto.success(createPostResponseDto));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<?>> modifyPost(@PathVariable Long postId, @RequestBody ModifyPostRequestDto modifyPostRequestDto) {
        CreatePostResponseDto modifiedPost = this.postService.modifyPostContent(postId, modifyPostRequestDto);
        return ResponseEntity.ok().body(ApiResponseDto.success(modifiedPost));
    }


}
