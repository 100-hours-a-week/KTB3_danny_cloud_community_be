package com.ktb.community.service;

import com.ktb.community.dto.request.LoginRequestDto;
import com.ktb.community.dto.request.SignUpRequestDto;
import com.ktb.community.dto.response.LoginResponseDto;
import com.ktb.community.entity.User;
import com.ktb.community.jwt.JwtUtil;
import com.ktb.community.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public Long signUpUser(SignUpRequestDto signUpRequestDto) throws Exception {
        // email이 중복되는지 확인
        if (this.userRepository.existsByEmail(signUpRequestDto.getEmail())) {
            throw new IllegalArgumentException("this email already exists");
        }

        if (this.userRepository.existsByNickname(signUpRequestDto.getNickname())) {
            throw new IllegalArgumentException("this nickname is already exist");
        }

        User user = new User();
        user.setEmail(signUpRequestDto.getEmail());
        if (!this.userService.checkValidityPassword(signUpRequestDto.getPassword())) {
            throw new IllegalArgumentException("Password does not meet requirements");
        }
        // bcyrpt로 암호화 추가하기
        user.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        user.setNickname(signUpRequestDto.getNickname());
        user.setProfileImage(signUpRequestDto.getProfileImage());

        return this.userRepository.save(user).getId();
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );

        User user = this.userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(() -> new RuntimeException("Users not found"));

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        return new LoginResponseDto(accessToken,refreshToken,user.getId());

    }
}
