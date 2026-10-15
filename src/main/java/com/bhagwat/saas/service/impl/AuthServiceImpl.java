package com.bhagwat.saas.service.impl;

import com.bhagwat.saas.dto.request.LoginRequest;
import com.bhagwat.saas.dto.response.LoginResponse;
import com.bhagwat.saas.entity.RefreshToken;
import com.bhagwat.saas.entity.RefreshTokenResult;
import com.bhagwat.saas.entity.User;
import com.bhagwat.saas.repository.UserRepository;
import com.bhagwat.saas.security.JwtService;
import com.bhagwat.saas.service.AuthService;
import com.bhagwat.saas.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid email or password"
                        )
                );

        if (!user.getActive()) {
            throw new BadCredentialsException(
                    "User account is inactive"
            );
        }

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword())) {

            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }

        String roles = user.getRoles()
                .stream()
                .map(role -> role.getName().name())
                .collect(Collectors.joining(","));

        String accessToken = jwtService.generateToken(
                user.getId(),
                user.getTenant().getId(),
                user.getEmail(),
                roles
        );

        RefreshTokenResult refreshTokenResult =
                refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                accessToken,
                refreshTokenResult.rawToken(),
                "Bearer",
                900
        );
    }

    @Override
    @Transactional
    public LoginResponse refreshAccessToken(String rawRefreshToken) {

        RefreshToken oldRefreshToken =
                refreshTokenService.verifyRefreshToken(rawRefreshToken);

        User user = oldRefreshToken.getUser();

        if (!user.getActive()) {
            throw new BadCredentialsException(
                    "User account is inactive"
            );
        }

        String roles = user.getRoles()
                .stream()
                .map(role -> role.getName().name())
                .collect(Collectors.joining(","));

        String accessToken = jwtService.generateToken(
                user.getId(),
                user.getTenant().getId(),
                user.getEmail(),
                roles
        );

        refreshTokenService.revokeToken(oldRefreshToken);

        RefreshTokenResult newRefreshToken =
                refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                accessToken,
                newRefreshToken.rawToken(),
                "Bearer",
                900
        );
    }

    @Override
    @Transactional
    public void logout(String rawRefreshToken) {

        RefreshToken refreshToken =
                refreshTokenService.verifyRefreshToken(rawRefreshToken);

        refreshTokenService.revokeToken(refreshToken);
    }
}