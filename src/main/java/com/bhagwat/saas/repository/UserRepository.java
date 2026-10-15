package com.bhagwat.saas.repository;

import com.bhagwat.saas.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByTenantIdAndEmail(
            Long tenantId,
            String email
    );

    boolean existsByTenantIdAndEmail(
            Long tenantId,
            String email
    );

    Optional<User> findByIdAndTenantId(Long id, Long tenantId);
}