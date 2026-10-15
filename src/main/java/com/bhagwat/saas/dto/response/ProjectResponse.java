package com.bhagwat.saas.dto.response;

import java.time.LocalDateTime;

public record ProjectResponse(

        Long id,
        String name,
        String description,
        Boolean active,
        Long tenantId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}