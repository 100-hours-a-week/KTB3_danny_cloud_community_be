package com.ktb.community.service;

import com.ktb.community.dto.response.AvailabilityResponseDto;
import com.ktb.community.dto.response.UserInfoResponseDto;
import com.ktb.community.entity.User;
import com.ktb.community.exception.custom.UserNotFoundException;
import com.ktb.community.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AvailabilityResponseDto checkDuplicateEmail(String email) {
        return new AvailabilityResponseDto(!this.userRepository.existsByEmail(email));
    }

    public AvailabilityResponseDto checkValidityPassword(String password) {
        // 최소 8자, 소문자 1개 이상, 숫자 1개 이상, 특수문자 1개 이상
        String regex = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";
        boolean isValid = password.matches(regex);
        return new AvailabilityResponseDto(isValid);
    }

    public UserInfoResponseDto readMyInfo(String email) {
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Not found user."));
        return new UserInfoResponseDto(user.getEmail(), user.getNickname());
    }
}
