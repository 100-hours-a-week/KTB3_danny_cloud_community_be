package com.ktb.community.controller;

import com.ktb.community.dto.response.ApiResponseDto;
import com.ktb.community.dto.response.CursorPageResponseDto;
import com.ktb.community.dto.response.PostResponseDto;
import com.ktb.community.service.PostService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @GetMapping
    public ResponseEntity<ApiResponseDto<CursorPageResponseDto<PostResponseDto>>> getPosts(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            CursorPageResponseDto<PostResponseDto> result = postService.getPosts(cursor, size);
            return ResponseEntity.ok(ApiResponseDto.success(result));


        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponseDto.error("Internal server error occured"));
        }
    }
}
