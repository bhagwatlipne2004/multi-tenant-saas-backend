package com.bhagwat.saas.service.impl;

import com.bhagwat.saas.dto.request.OnboardingRequest;
import com.bhagwat.saas.dto.request.CreateUserRequest;
import com.bhagwat.saas.dto.response.OnboardingResponse;
import com.bhagwat.saas.dto.response.TenantResponse;
import com.bhagwat.saas.dto.response.UserResponse;
import com.bhagwat.saas.entity.Role;
import com.bhagwat.saas.entity.RoleName;
import com.bhagwat.saas.entity.Tenant;
import com.bhagwat.saas.entity.User;
import com.bhagwat.saas.exception.TenantAlreadyExistsException;
import com.bhagwat.saas.exception.UserAlreadyExistsException;
import com.bhagwat.saas.repository.RoleRepository;
import com.bhagwat.saas.repository.TenantRepository;
import com.bhagwat.saas.repository.UserRepository;
import com.bhagwat.saas.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OnboardingResponse onboard(OnboardingRequest request) {

        if (tenantRepository.existsByName(request.tenantName())) {
            throw new TenantAlreadyExistsException(
                    "Tenant with name '" +
                            request.tenantName() +
                            "' already exists"
            );
        }

        if (tenantRepository.existsBySlug(request.tenantSlug())) {
            throw new TenantAlreadyExistsException(
                    "Tenant with slug '" +
                            request.tenantSlug() +
                            "' already exists"
            );
        }

        Tenant tenant = Tenant.builder()
                .name(request.tenantName())
                .slug(request.tenantSlug())
                .active(true)
                .build();

        Tenant savedTenant = tenantRepository.save(tenant);

        CreateUserRequest adminRequest = request.admin();

        if (userRepository.existsByTenantIdAndEmail(
                savedTenant.getId(),
                adminRequest.email())) {

            throw new UserAlreadyExistsException(
                    "Admin email already exists"
            );
        }

        Role adminRole = roleRepository
                .findByName(RoleName.TENANT_ADMIN)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "TENANT_ADMIN role not found"
                        )
                );

        User admin = User.builder()
                .firstName(adminRequest.firstName())
                .lastName(adminRequest.lastName())
                .email(adminRequest.email())
                .password(
                        passwordEncoder.encode(
                                adminRequest.password()
                        )
                )
                .active(true)
                .tenant(savedTenant)
                .roles(Set.of(adminRole))
                .build();

        User savedAdmin = userRepository.save(admin);

        TenantResponse tenantResponse =
                new TenantResponse(
                        savedTenant.getId(),
                        savedTenant.getName(),
                        savedTenant.getSlug(),
                        savedTenant.getActive(),
                        savedTenant.getCreatedAt(),
                        savedTenant.getUpdatedAt()
                );

        UserResponse userResponse =
                new UserResponse(
                        savedAdmin.getId(),
                        savedAdmin.getFirstName(),
                        savedAdmin.getLastName(),
                        savedAdmin.getEmail(),
                        savedAdmin.getActive(),
                        savedTenant.getId(),
                        Set.of(RoleName.TENANT_ADMIN),
                        savedAdmin.getCreatedAt(),
                        savedAdmin.getUpdatedAt()
                );

        return new OnboardingResponse(
                tenantResponse,
                userResponse
        );
    }
}