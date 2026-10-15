package com.bhagwat.saas.service;

import com.bhagwat.saas.dto.request.CreateUserRequest;
import com.bhagwat.saas.dto.response.UserResponse;

public interface UserService {

    UserResponse createUser(
            Long tenantId,
            CreateUserRequest request
    );

    UserResponse getUserById(Long userId);
}