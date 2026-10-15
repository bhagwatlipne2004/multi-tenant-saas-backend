package com.bhagwat.saas.service;

import com.bhagwat.saas.dto.request.CreateTenantRequest;
import com.bhagwat.saas.dto.response.TenantResponse;
import com.bhagwat.saas.entity.Tenant;
import org.springframework.data.domain.Page;

public interface TenantService {

    TenantResponse createTenant(CreateTenantRequest request);

    TenantResponse getTenantById(Long id);

    Page<TenantResponse> getAllTenants(
            int page,
            int size,
            String sortBy,
            String direction
    );

    public default TenantResponse toResponse(Tenant tenant) {

        return new TenantResponse(
                tenant.getId(),
                tenant.getName(),
                tenant.getSlug(),
                tenant.getActive(),
                tenant.getCreatedAt(),
                tenant.getUpdatedAt()
        );
    }
}