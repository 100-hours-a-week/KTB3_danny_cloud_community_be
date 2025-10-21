package com.ktb.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordCheckRequestDto {
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
