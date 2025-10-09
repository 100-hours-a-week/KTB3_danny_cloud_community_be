package com.ktb.community.controller;

import com.ktb.community.dto.request.EmailCheckRequestDto;
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
    ResponseEntity<Map<String, String>> checkEmail(@RequestBody @Valid EmailCheckRequestDto emailCheckDto, BindingResult bindingResult) {
        // 검증에서 문제가 발생했다면
        if (bindingResult.hasErrors()) {
            String message = (bindingResult.getFieldError("email") != null) ? bindingResult.getFieldError("email").getDefaultMessage() : "Not a valid request";

            return ResponseEntity.badRequest().body(Map.of("message", message));
        }
        return ResponseEntity.accepted().body(Map.of("is_available", "true"));
    }
}
