package com.bhagwat.saas.dto.response;

public record OnboardingResponse(
        TenantResponse tenant,
        UserResponse admin
) {
}