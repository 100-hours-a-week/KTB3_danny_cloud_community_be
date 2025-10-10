package com.ktb.community.controller;

import com.ktb.community.dto.request.EmailCheckRequestDto;
import com.ktb.community.dto.response.ApiResponseDto;
import com.ktb.community.dto.response.EmailAvailabilityResponseDto;
import com.ktb.community.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/email")
    ResponseEntity<ApiResponseDto<EmailAvailabilityResponseDto>> checkEmail(@RequestBody @Valid EmailCheckRequestDto emailCheckDto, BindingResult bindingResult) {
        // 검증에서 문제가 발생했다면
        if (bindingResult.hasErrors()) {
            String message = (bindingResult.getFieldError("email") != null) ? bindingResult.getFieldError("email").getDefaultMessage() : "Not a valid request";

            return ResponseEntity.badRequest().body(ApiResponseDto.error(message));

        }
        if(this.userService.checkDuplicateEmail(emailCheckDto.getEmail())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponseDto.error("This email already exists. Please enter a different email.\""));
        }

        return ResponseEntity.ok().body(ApiResponseDto.success(new EmailAvailabilityResponseDto(true)));
    }
}
