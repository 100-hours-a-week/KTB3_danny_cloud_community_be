package com.ktb.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CreatePostRequestDto {
    @NotBlank(message = "제목은 필수 입력 입니다.")
    String title;
    @NotBlank(message = "내용은 필수 입력입니다.")
    String content;

    List<String> images = new ArrayList<>();


}
