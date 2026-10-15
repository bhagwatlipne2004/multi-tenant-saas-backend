package com.bhagwat.saas.service;

import com.bhagwat.saas.dto.request.CreateProjectRequest;
import com.bhagwat.saas.dto.response.ProjectResponse;
import org.springframework.data.domain.Page;

public interface ProjectService {

    ProjectResponse createProject(CreateProjectRequest request);

    ProjectResponse getProjectById(Long projectId);

    Page<ProjectResponse> getAllProjects(
            int page,
            int size,
            String sortBy,
            String direction
    );
}