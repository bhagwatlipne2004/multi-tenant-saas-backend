package com.bhagwat.saas.service;

import com.bhagwat.saas.entity.RefreshToken;
import com.bhagwat.saas.entity.RefreshTokenResult;
import com.bhagwat.saas.entity.User;

public interface RefreshTokenService {

    RefreshTokenResult createRefreshToken(User user);

    RefreshToken verifyRefreshToken(String rawToken);

    void revokeToken(RefreshToken refreshToken);
}