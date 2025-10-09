package com.ktb.community.controller;

import com.ktb.community.dto.request.EmailCheckRequestDto;
import com.ktb.community.dto.response.ApiResponse;
import com.ktb.community.dto.response.EmailAvailabilityResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    @PostMapping("/email")
    ResponseEntity<ApiResponse<EmailAvailabilityResponseDto>> checkEmail(@RequestBody @Valid EmailCheckRequestDto emailCheckDto, BindingResult bindingResult) {
        // 검증에서 문제가 발생했다면
        if (bindingResult.hasErrors()) {
            String message = (bindingResult.getFieldError("email") != null) ? bindingResult.getFieldError("email").getDefaultMessage() : "Not a valid request";

            return ResponseEntity.badRequest().body(ApiResponse.error(message));

        }
        return ResponseEntity.ok().body(ApiResponse.success(new EmailAvailabilityResponseDto(true)));

    }
}
