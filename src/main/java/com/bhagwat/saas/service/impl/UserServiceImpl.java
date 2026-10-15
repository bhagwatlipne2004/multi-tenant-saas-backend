package com.bhagwat.saas.service.impl;

import com.bhagwat.saas.dto.request.CreateUserRequest;
import com.bhagwat.saas.dto.response.UserResponse;
import com.bhagwat.saas.entity.Tenant;
import com.bhagwat.saas.entity.User;
import com.bhagwat.saas.exception.ResourceNotFoundException;
import com.bhagwat.saas.exception.UserAlreadyExistsException;
import com.bhagwat.saas.repository.RoleRepository;
import com.bhagwat.saas.repository.TenantRepository;
import com.bhagwat.saas.repository.UserRepository;
import com.bhagwat.saas.service.UserService;
import com.bhagwat.saas.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.bhagwat.saas.entity.Role;
import com.bhagwat.saas.entity.RoleName;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponse createUser(
            Long tenantId,
            CreateUserRequest request) {

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tenant with id " + tenantId + " not found"
                        )
                );

        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Default USER role not found"
                        ));

        if (userRepository.existsByTenantIdAndEmail(
                tenantId,
                request.email())) {

            throw new UserAlreadyExistsException(
                    "User with email '" + request.email()
                            + "' already exists in this tenant"
            );
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .active(true)
                .tenant(tenant)
                .roles(Set.of(userRole))
                .build();

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    private UserResponse toResponse(User user) {
        Set<RoleName> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getActive(),
                user.getTenant().getId(),
                roles,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Override
    public UserResponse getUserById(Long userId) {

        Long tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            throw new IllegalStateException("Tenant context is missing");
        }

        User user = userRepository
                .findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with id " + userId + " not found"
                        )
                );

        return toResponse(user);
    }
}