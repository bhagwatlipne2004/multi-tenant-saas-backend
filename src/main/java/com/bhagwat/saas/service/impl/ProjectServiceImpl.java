package com.bhagwat.saas.service.impl;

import com.bhagwat.saas.dto.request.CreateProjectRequest;
import com.bhagwat.saas.dto.response.ProjectResponse;
import com.bhagwat.saas.entity.Project;
import com.bhagwat.saas.entity.Tenant;
import com.bhagwat.saas.exception.ResourceNotFoundException;
import com.bhagwat.saas.repository.ProjectRepository;
import com.bhagwat.saas.repository.TenantRepository;
import com.bhagwat.saas.service.ProjectService;
import com.bhagwat.saas.service.RedisService;
import com.bhagwat.saas.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TenantRepository tenantRepository;
    private final RedisService redisService;

    @Override
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {

        Long tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            throw new IllegalStateException("Tenant context is missing");
        }

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tenant not found"
                        )
                );

        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .active(true)
                .tenant(tenant)
                .build();

        Project savedProject = projectRepository.save(project);

        return toResponse(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "projects",
            key = "'tenant:' + T(com.bhagwat.saas.tenant.TenantContext).getTenantId() + ':project:' + #projectId"
    )
    public ProjectResponse getProjectById(Long projectId) {

        Long tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            throw new IllegalStateException("Tenant context is missing");
        }

        System.out.println(">>> Current tenant ID: " + tenantId);
        System.out.println(">>> Requested project ID: " + projectId);
        Project project = projectRepository
                .findByIdAndTenantId(projectId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project with id " + projectId + " not found"
                        )
                );

        return toResponse(project);
    }


    private ProjectResponse toResponse(Project project) {

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getActive(),
                project.getTenant().getId(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponse> getAllProjects(
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        Long tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            throw new IllegalStateException("Tenant context is missing");
        }

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(
                page,
                size,
                sort
        );

        return projectRepository
                .findAllByTenantId(tenantId, pageable)
                .map(this::toResponse);
    }
}