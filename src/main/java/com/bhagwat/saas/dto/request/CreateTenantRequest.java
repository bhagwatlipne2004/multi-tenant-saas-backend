package com.bhagwat.saas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTenantRequest(

        @NotBlank(message = "Tenant name is required")
        @Size(max = 100, message = "Tenant name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Tenant slug is required")
        @Size(max = 100, message = "Tenant slug must not exceed 100 characters")
        String slug
) {
}