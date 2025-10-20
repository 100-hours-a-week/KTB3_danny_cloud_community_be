package com.ktb.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponseDto {
    private Long id;
    private String title;
    private String content;
    private String author;
    private Long views;
    private Long comments;
    private Long likes;
    private LocalDateTime createAt;
    private List<String> images;
}
