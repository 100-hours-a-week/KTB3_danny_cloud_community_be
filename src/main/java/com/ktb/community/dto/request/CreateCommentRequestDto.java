package com.ktb.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequestDto {
    @NotNull(message = "게시글 ID를 입력해주세요")
    public Long postId;
    @NotBlank(message = "내용을 입력해주세요")
    public String content;
}
