package com.ktb.community.service;

import com.ktb.community.entity.Refresh;
import com.ktb.community.entity.User;
import com.ktb.community.repository.RefreshRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RefreshTokenService {
    private final RefreshRepository refreshRepository;

    public RefreshTokenService(RefreshRepository refreshRepository) {
        this.refreshRepository = refreshRepository;
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

    public boolean validateRefreshToken(String token) {
        try {
            Refresh refresh = findByToken(token);

            return !refresh.getExpirationAt().isBefore(LocalDateTime.now());
        } catch (Exception e) {
            return false;
        }
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
