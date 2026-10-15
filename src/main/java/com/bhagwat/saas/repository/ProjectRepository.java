package com.bhagwat.saas.repository;

import com.bhagwat.saas.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByIdAndTenantId(Long id, Long tenantId);

    Page<Project> findAllByTenantId(
            Long tenantId,
            Pageable pageable
    );
}