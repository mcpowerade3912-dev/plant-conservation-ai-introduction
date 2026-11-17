package com.example.plant.controller;

import com.example.plant.common.ApiResponse;
import com.example.plant.dto.IntroductionAssistRequest;
import com.example.plant.dto.IntroductionAssistResponse;
import com.example.plant.service.AiIntroductionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/ai")
public class AiIntroductionController {
    private final AiIntroductionService aiIntroductionService;

    public AiIntroductionController(AiIntroductionService aiIntroductionService) {
        this.aiIntroductionService = aiIntroductionService;
    }

    @PostMapping("/introduction-assist")
    public ApiResponse<IntroductionAssistResponse> assist(
            Authentication authentication,
            @Valid @RequestBody IntroductionAssistRequest request) {
        return ApiResponse.ok(aiIntroductionService.assist(request, authentication.getName()));
    }
}
