package com.bhagwat.saas.controller;

import com.bhagwat.saas.dto.request.OnboardingRequest;
import com.bhagwat.saas.dto.response.OnboardingResponse;
import com.bhagwat.saas.service.OnboardingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;

    @PostMapping
    public ResponseEntity<OnboardingResponse> onboard(
            @Valid @RequestBody OnboardingRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(onboardingService.onboard(request));
    }
}