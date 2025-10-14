package com.ktb.community.controller;

import com.ktb.community.dto.request.LoginRequestDto;
import com.ktb.community.dto.request.SignUpRequestDto;
import com.ktb.community.dto.response.ApiResponseDto;
import com.ktb.community.dto.response.CreateUserResponseDto;
import com.ktb.community.dto.response.LoginResponseDto;
import com.ktb.community.service.AuthService;
import com.ktb.community.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }


    @PostMapping()
    ResponseEntity<ApiResponseDto<?>> signUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            // 모든 필드를 확인하는 로직이 너무 길어 하나로 통합해서 유효하지 않은 필드를 가졌음을 표현
            String message = "Not valid form";

            return ResponseEntity.badRequest().body(ApiResponseDto.error(message));
        }

        // 비밀번호와 비밀번호 확인이 동일한지 검사
        if (!signUpRequestDto.getPassword().equals(signUpRequestDto.getPasswordConfirm())) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("Password do not match"));
        }

        try {
            Long userId = this.authService.signUpUser(signUpRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(new CreateUserResponseDto(userId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            throw e;
        }

    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<?>> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        try {
            LoginResponseDto response = this.authService.login(loginRequestDto);
            return ResponseEntity.ok(ApiResponseDto.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDto.error("Invalide email or password"));
        }
    }

//    @GetMapping("/refresh")
//    public ResponseEntity<ApiResponseDto<?>> refresh(@RequestHeader("Authorization") String refreshToken){
//        System.out.println();
//    }
}