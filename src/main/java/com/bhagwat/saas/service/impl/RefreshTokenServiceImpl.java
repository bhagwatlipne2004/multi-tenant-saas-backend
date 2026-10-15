package com.bhagwat.saas.service.impl;

import com.bhagwat.saas.entity.RefreshToken;
import com.bhagwat.saas.entity.RefreshTokenResult;
import com.bhagwat.saas.entity.User;
import com.bhagwat.saas.repository.RefreshTokenRepository;
import com.bhagwat.saas.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl
        implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.jwt.refresh-expiration}")
    private long refreshExpiration;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public RefreshTokenResult createRefreshToken(User user) {

        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        String tokenHash = hashToken(rawToken);

        LocalDateTime expiresAt = LocalDateTime.now()
                .plusNanos(refreshExpiration * 1_000_000);

        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(tokenHash)
                .user(user)
                .expiresAt(expiresAt)
                .revoked(false)
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        return new RefreshTokenResult(rawToken, savedToken);
    }

    @Override
    public RefreshToken verifyRefreshToken(String rawToken) {

        RefreshToken token = refreshTokenRepository
                .findByTokenHash(hashToken(rawToken))
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid refresh token"
                        )
                );

        if (token.isRevoked()) {
            throw new BadCredentialsException(
                    "Refresh token has been revoked"
            );
        }

        if (token.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new BadCredentialsException(
                    "Refresh token has expired"
            );
        }

        return token;
    }

    @Override
    @Transactional
    public void revokeToken(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }




    private String hashToken(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getEncoder()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 algorithm not available",
                    e
            );
        }
    }
}