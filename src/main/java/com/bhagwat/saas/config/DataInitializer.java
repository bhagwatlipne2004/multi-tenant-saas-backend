package com.bhagwat.saas.config;

import com.bhagwat.saas.entity.Role;
import com.bhagwat.saas.entity.RoleName;
import com.bhagwat.saas.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        createRoleIfNotExists(RoleName.TENANT_ADMIN);
        createRoleIfNotExists(RoleName.USER);
    }

    private void createRoleIfNotExists(RoleName roleName) {

        roleRepository.findByName(roleName)
                .orElseGet(() ->
                        roleRepository.save(
                                Role.builder()
                                        .name(roleName)
                                        .build()
                        )
                );
    }
}