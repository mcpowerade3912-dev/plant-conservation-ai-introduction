package com.example.plant.dto;

import java.time.LocalDateTime;

public record SystemSettingsResponse(
        String systemName,
        String supportContact,
        String defaultPlantCategory,
        Integer uploadMaxSizeMb,
        Boolean aiEnabled,
        String aiBaseUrl,
        String aiModel,
        Integer aiTimeoutSeconds,
        Integer aiRateLimitPerMinute,
        Boolean aiApiKeyConfigured,
        LocalDateTime updateTime
) {
}
