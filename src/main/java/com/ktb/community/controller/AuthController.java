package com.ktb.community.controller;

import com.ktb.community.dto.request.LoginRequestDto;
import com.ktb.community.dto.request.SignUpRequestDto;
import com.ktb.community.dto.response.ApiResponseDto;
import com.ktb.community.dto.response.CreateUserResponseDto;
import com.ktb.community.dto.response.LoginResponseDto;
import com.ktb.community.service.AuthService;
import com.ktb.community.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @Value("${jwt.expiration.refresh}")
    private long refreshTokenExpiration;

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
    public ResponseEntity<ApiResponseDto<?>> login(
            @RequestBody @Valid LoginRequestDto loginRequestDto,
            HttpServletResponse response) {
        try {
            LoginResponseDto loginResponse = this.authService.login(loginRequestDto);

            // Refresh Token을 HttpOnly 쿠키로 설정
            Cookie refreshTokenCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge((int) (refreshTokenExpiration / 1000)); // application.yml 값 사용 (밀리초 → 초)
            response.addCookie(refreshTokenCookie);

            // 응답 DTO에서는 refreshToken을 null로 설정 (보안)
            LoginResponseDto responseDto = new LoginResponseDto(
                    loginResponse.getAccessToken(),
                    null,
                    loginResponse.getUserId()
            );

            return ResponseEntity.ok(ApiResponseDto.success(responseDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDto.error("Invalide email or password"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<?>> logout(HttpServletResponse response) {
        // Refresh Token 쿠키 삭제 (MaxAge를 0으로 설정)
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);  // 즉시 만료
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(ApiResponseDto.success("Logout successful"));
    }

//    @GetMapping("/refresh")
//    public ResponseEntity<ApiResponseDto<?>> refresh(@RequestHeader("Authorization") String refreshToken){
//        System.out.println();
//    }
}