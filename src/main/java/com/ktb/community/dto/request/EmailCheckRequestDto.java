package com.ktb.community.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailCheckRequestDto {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "유효하지 않은 이메일형식입니다.")
    private String email;
}
