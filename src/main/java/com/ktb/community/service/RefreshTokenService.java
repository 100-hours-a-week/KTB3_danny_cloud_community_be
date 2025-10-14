package com.ktb.community.service;

import com.ktb.community.entity.Refresh;
import com.ktb.community.entity.User;
import com.ktb.community.jwt.JwtUtil;
import com.ktb.community.repository.RefreshRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RefreshTokenService {
    private final RefreshRepository refreshRepository;
    private final JwtUtil jwtUtil;


    public RefreshTokenService(RefreshRepository refreshRepository, JwtUtil jwtUtil) {
        this.refreshRepository = refreshRepository;
        this.jwtUtil = jwtUtil;
    }

    public void saveRefreshToken(String token, User user, LocalDateTime expiredAt) {
        Refresh refreshToken = new Refresh();
        refreshToken.setRefreshToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpirationAt(expiredAt);

        refreshRepository.save(refreshToken);
    }

    public Refresh findByToken(String token) {
        return refreshRepository.findByRefreshToken(token).orElseThrow(() -> new RuntimeException("Invalid refresh token"));
    }

    public Refresh checkExistRefreshToken(String token) {
        try {
            return findByToken(token);
        } catch (Exception e) {
            throw new RuntimeException("Not a valid token");
        }
    }

    public String reIssueAccessToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }
        Refresh refresh = checkExistRefreshToken(refreshToken);

        if (refresh.getExpirationAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return this.jwtUtil.generateAccessToken(refresh.getUser().getId(), refresh.getUser().getEmail());
    }

    public List<Refresh> findAllTokens(Long userId) {
        return refreshRepository.findByUserId(userId);
    }

    public void removeRefreshToken(String token) {
        this.refreshRepository.deleteByRefreshToken(token);
    }

    public void removeAllRefreshToken(Long userId) {
        this.refreshRepository.deleteAllByUserId(userId);
    }

    public void deleteExpiredTokens() {
        List<Refresh> tokens = this.refreshRepository.findAll();
        List<Refresh> expiredTokens = tokens.stream().filter((token) -> token.getExpirationAt().isBefore(LocalDateTime.now())).toList();
        this.refreshRepository.deleteAll(expiredTokens);
    }
}
