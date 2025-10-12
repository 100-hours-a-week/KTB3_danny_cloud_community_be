package com.ktb.community.controller;

import com.ktb.community.dto.response.ApiResponseDto;
import com.ktb.community.dto.response.CursorPageResponseDto;
import com.ktb.community.dto.response.PostDetailResponseDto;
import com.ktb.community.dto.response.PostResponseDto;
import com.ktb.community.entity.Post;
import com.ktb.community.service.PostService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        try {
            CursorPageResponseDto<PostResponseDto> result = postService.getPostList(cursor, size);
            return ResponseEntity.ok(ApiResponseDto.success(result));


        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponseDto.error("Internal server error occured"));
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<PostDetailResponseDto>> getPostDetail(@PathVariable @Positive Long postId) {

        try {
            PostDetailResponseDto post = this.postService.getPostContent(postId);
            return ResponseEntity.ok().body(ApiResponseDto.success(post));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponseDto.error("Internal server error occured"));
        }


    }

}
