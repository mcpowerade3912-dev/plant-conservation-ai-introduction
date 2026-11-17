package com.example.plant.controller;

import com.example.plant.common.ApiResponse;
import com.example.plant.dto.SystemSettingsRequest;
import com.example.plant.dto.SystemSettingsResponse;
import com.example.plant.service.SystemSettingsService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SystemSettingsController {
    private final SystemSettingsService service;

    public SystemSettingsController(SystemSettingsService service) {
        this.service = service;
    }

    @GetMapping("/api/settings/public")
    public ApiResponse<Map<String, Object>> publicSettings() {
        return ApiResponse.ok(service.getPublicSettings());
    }

    @GetMapping("/api/admin/settings")
    public ApiResponse<SystemSettingsResponse> adminSettings() {
        return ApiResponse.ok(service.getAdminSettings());
    }

    @PutMapping("/api/admin/settings")
    public ApiResponse<SystemSettingsResponse> updateSettings(
            Authentication authentication,
            @RequestBody SystemSettingsRequest request) {
        return ApiResponse.ok(service.update(request, authentication.getName()));
    }
}
