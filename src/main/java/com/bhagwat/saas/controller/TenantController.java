package com.bhagwat.saas.controller;

import com.bhagwat.saas.dto.request.CreateTenantRequest;
import com.bhagwat.saas.dto.response.TenantResponse;
import com.bhagwat.saas.service.TenantService;
import com.bhagwat.saas.tenant.TenantContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<TenantResponse> createTenant(
            @Valid @RequestBody CreateTenantRequest request) {

        TenantResponse response = tenantService.createTenant(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenantResponse> getTenantById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                tenantService.getTenantById(id)
        );
    }

    @GetMapping
    public ResponseEntity<Page<TenantResponse>> getAllTenants(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "createdAt") String sortBy,

            @RequestParam(defaultValue = "desc") String direction) {

        return ResponseEntity.ok(
                tenantService.getAllTenants(
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok(
                "Welcome Tenant Admin"
        );
    }

    @GetMapping("/current")
    public ResponseEntity<Long> getCurrentTenant() {

        return ResponseEntity.ok(
                TenantContext.getTenantId()
        );
    }


}