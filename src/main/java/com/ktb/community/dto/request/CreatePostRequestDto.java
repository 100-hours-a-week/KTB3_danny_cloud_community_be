package com.ktb.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CreatePostRequestDto {
    @NotBlank
    String title;
    @NotBlank
    String content;

    List<String> images = new ArrayList<>();


}
