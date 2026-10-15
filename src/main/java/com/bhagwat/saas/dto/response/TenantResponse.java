package com.bhagwat.saas.dto.response;

import java.time.LocalDateTime;

public record TenantResponse(
        Long id,
        String name,
        String slug,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}