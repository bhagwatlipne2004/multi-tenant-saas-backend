package com.bhagwat.saas.service;

import com.bhagwat.saas.dto.request.OnboardingRequest;
import com.bhagwat.saas.dto.response.OnboardingResponse;

public interface OnboardingService {

    OnboardingResponse onboard(OnboardingRequest request);
}