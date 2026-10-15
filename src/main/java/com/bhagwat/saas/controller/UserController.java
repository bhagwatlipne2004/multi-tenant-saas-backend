package com.bhagwat.saas.controller;

import com.bhagwat.saas.dto.request.CreateUserRequest;
import com.bhagwat.saas.dto.response.UserResponse;
import com.bhagwat.saas.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @PathVariable Long tenantId,
            @Valid @RequestBody CreateUserRequest request) {

        UserResponse response =
                userService.createUser(tenantId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long tenantId,
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                userService.getUserById(userId)
        );
    }
}