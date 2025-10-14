package com.ktb.community.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailCheckRequestDto {
    @NotBlank(message = "Please enter the email")
    @Email(message = "Not valid form")
    private String email;
}
