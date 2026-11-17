package com.example.plant.dto;

public record SystemSettingsRequest(
        String systemName,
        String supportContact,
        String defaultPlantCategory,
        Integer uploadMaxSizeMb,
        Boolean aiEnabled,
        String aiBaseUrl,
        String aiModel,
        Integer aiTimeoutSeconds,
        Integer aiRateLimitPerMinute,
        String aiApiKey,
        Boolean clearAiApiKey
) {
}
