package com.ktb.community.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModifyPostRequestDto {
    @Nullable
    public String title;
    @Nullable
    public String content;
    @Nullable
    List<String> images = new ArrayList<>();
}
