package com.bhagwat.saas.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OnboardingRequest(

        @NotBlank(message = "Tenant name is required")
        @Size(max = 100)
        String tenantName,

        @NotBlank(message = "Tenant slug is required")
        @Size(max = 100)
        String tenantSlug,

        @Valid
        CreateUserRequest admin
) {
}