package com.bhagwat.saas.entity;

public record RefreshTokenResult(
        String rawToken,
        RefreshToken refreshToken
) {
}