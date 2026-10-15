package com.bhagwat.saas.service;

import com.bhagwat.saas.dto.request.LoginRequest;
import com.bhagwat.saas.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse refreshAccessToken(String rawRefreshToken);

    void logout(String rawRefreshToken);
}