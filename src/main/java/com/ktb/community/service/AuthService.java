package com.ktb.community.service;

import com.ktb.community.dto.request.SignUpRequestDto;
import com.ktb.community.entity.User;
import com.ktb.community.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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
}
