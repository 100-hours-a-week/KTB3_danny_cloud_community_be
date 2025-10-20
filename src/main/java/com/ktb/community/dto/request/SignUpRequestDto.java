package com.ktb.community.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {
    @Email
    @NotNull
    @NotBlank(message = "please enter the email")
    private String email;

    @NotNull
    @NotBlank(message = "please enter the password")
    private String password;

    @NotBlank(message = "please enter the password")
    @NotNull
    private String passwordConfirm;

    @NotNull
    @NotBlank
    private String nickname;

    @Nullable
    private String profileImage;


}
