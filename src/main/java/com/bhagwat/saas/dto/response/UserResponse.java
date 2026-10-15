package com.bhagwat.saas.dto.response;

import com.bhagwat.saas.entity.RoleName;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Boolean active,
        Long tenantId,
        Set<RoleName> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}