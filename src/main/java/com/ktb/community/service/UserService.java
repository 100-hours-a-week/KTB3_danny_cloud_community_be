package com.ktb.community.service;

import com.ktb.community.dto.request.ModifyNicknameRequestDto;
import com.ktb.community.dto.response.AvailabilityResponseDto;
import com.ktb.community.dto.response.CrudUserResponseDto;
import com.ktb.community.dto.response.UserInfoResponseDto;
import com.ktb.community.entity.User;
import com.ktb.community.exception.custom.DuplicateNicknameException;
import com.ktb.community.exception.custom.InvalidNicknameException;
import com.ktb.community.exception.custom.UserNotFoundException;
import com.ktb.community.jwt.JwtUtil;
import com.ktb.community.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
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

    @Transactional
    public CrudUserResponseDto changeNickname(String email, ModifyNicknameRequestDto modifyNicknameRequestDto) {
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Not found user"));

        String newNickname = modifyNicknameRequestDto.getNickname();

        if (user.getNickname().equals(newNickname)){
            throw new InvalidNicknameException("Same nickname is not acceptable");
        }

        if (this.userRepository.existsByNicknameAndIdNot(newNickname, user.getId())) {
            throw new DuplicateNicknameException("This nickname is already in use");
        }

        user.setNickname(newNickname);
        return new CrudUserResponseDto(user.getId());
    }
}
