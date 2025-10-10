package com.ktb.community.service;

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

    public Boolean checkDuplicateEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public Boolean checkValidityPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?]).{8,}$";
        return password.matches(regex);
    }
}
