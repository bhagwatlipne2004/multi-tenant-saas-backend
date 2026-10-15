package com.bhagwat.saas.service.impl;

import com.bhagwat.saas.dto.request.CreateTenantRequest;
import com.bhagwat.saas.dto.response.TenantResponse;
import com.bhagwat.saas.entity.Tenant;
import com.bhagwat.saas.exception.ResourceNotFoundException;
import com.bhagwat.saas.exception.TenantAlreadyExistsException;
import com.bhagwat.saas.repository.TenantRepository;
import com.bhagwat.saas.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    @Override
    public TenantResponse createTenant(CreateTenantRequest request) {

        if (tenantRepository.existsByName(request.name())) {
            throw new TenantAlreadyExistsException(
                    "Tenant with name '" + request.name() + "' already exists"
            );
        }

        if (tenantRepository.existsBySlug(request.slug())) {
            throw new TenantAlreadyExistsException(
                    "Tenant with slug '" + request.slug() + "' already exists"
            );
        }

        Tenant tenant = Tenant.builder()
                .name(request.name())
                .slug(request.slug())
                .active(true)
                .build();

        Tenant savedTenant = tenantRepository.save(tenant);

        return toResponse(savedTenant);
    }

    @Override
    public TenantResponse getTenantById(Long id) {

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tenant with id " + id + " not found"
                        ));

        return toResponse(tenant);
    }

    @Override
    public Page<TenantResponse> getAllTenants(
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        PageRequest pageable = PageRequest.of(page, size, sort);

        return tenantRepository
                .findAll(pageable)
                .map(this::toResponse);
    }
}