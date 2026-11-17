package com.example.plant.service;

import com.example.plant.model.SystemSetting;
import com.example.plant.repository.SystemSettingRepository;
import com.example.plant.repository.UserRepository;
import com.example.plant.dto.SystemSettingsRequest;
import com.example.plant.dto.SystemSettingsResponse;
import com.example.plant.security.SettingsCrypto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SystemSettingsService {
    private static final String SYSTEM_NAME = "system.name";
    private static final String SUPPORT_CONTACT = "system.supportContact";
    private static final String DEFAULT_CATEGORY = "plant.defaultCategory";
    private static final String UPLOAD_MAX_SIZE = "upload.maxSizeMb";
    private static final String AI_ENABLED = "ai.enabled";
    private static final String AI_BASE_URL = "ai.baseUrl";
    private static final String AI_MODEL = "ai.model";
    private static final String AI_TIMEOUT = "ai.timeoutSeconds";
    private static final String AI_RATE_LIMIT = "ai.rateLimitPerMinute";
    private static final String AI_API_KEY = "ai.apiKey";

    private final SystemSettingRepository repository;
    private final SettingsCrypto crypto;
    private final UserRepository userRepository;

    public SystemSettingsService(
            SystemSettingRepository repository,
            SettingsCrypto crypto,
            UserRepository userRepository) {
        this.repository = repository;
        this.crypto = crypto;
        this.userRepository = userRepository;
    }

    public SystemSettingsResponse getAdminSettings() {
        String encryptedKey = rawValue(AI_API_KEY);
        boolean keyConfigured = encryptedKey != null && !encryptedKey.isBlank();
        if (keyConfigured) {
            try {
                crypto.decrypt(encryptedKey);
            } catch (Exception e) {
                keyConfigured = false;
            }
        }
        return new SystemSettingsResponse(
                getValue(SYSTEM_NAME, "数智保育系统"),
                getValue(SUPPORT_CONTACT, ""),
                getValue(DEFAULT_CATEGORY, "观赏植物"),
                getInt(UPLOAD_MAX_SIZE, 5),
                getBoolean(AI_ENABLED, true),
                getValue(AI_BASE_URL, "https://api.openai.com/v1"),
                getValue(AI_MODEL, "gpt-4.1-mini"),
                getInt(AI_TIMEOUT, 60),
                getInt(AI_RATE_LIMIT, 20),
                keyConfigured,
                repository.findAll().stream()
                        .map(SystemSetting::getUpdateTime)
                        .filter(java.util.Objects::nonNull)
                        .max(java.util.Comparator.naturalOrder())
                        .orElse(null));
    }

    @Transactional
    public SystemSettingsResponse update(SystemSettingsRequest request, String username) {
        validate(request);
        Long userId = userRepository.findByUsername(username)
                .map(user -> user.getId())
                .orElse(null);
        repository.upsert(SYSTEM_NAME, request.systemName().trim(), false, userId);
        repository.upsert(SUPPORT_CONTACT, trim(request.supportContact()), false, userId);
        repository.upsert(DEFAULT_CATEGORY, trim(request.defaultPlantCategory()), false, userId);
        repository.upsert(UPLOAD_MAX_SIZE, String.valueOf(request.uploadMaxSizeMb()), false, userId);
        repository.upsert(AI_ENABLED, String.valueOf(request.aiEnabled()), false, userId);
        repository.upsert(AI_BASE_URL, request.aiBaseUrl().trim(), false, userId);
        repository.upsert(AI_MODEL, request.aiModel().trim(), false, userId);
        repository.upsert(AI_TIMEOUT, String.valueOf(request.aiTimeoutSeconds()), false, userId);
        repository.upsert(AI_RATE_LIMIT, String.valueOf(request.aiRateLimitPerMinute()), false, userId);
        if (Boolean.TRUE.equals(request.clearAiApiKey())) {
            repository.delete(AI_API_KEY);
        } else if (request.aiApiKey() != null && !request.aiApiKey().isBlank()) {
            repository.upsert(AI_API_KEY, crypto.encrypt(request.aiApiKey().trim()), true, userId);
        }
        return getAdminSettings();
    }

    public Map<String, Object> getPublicSettings() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("systemName", getValue(SYSTEM_NAME, "数智保育系统"));
        result.put("supportContact", getValue(SUPPORT_CONTACT, ""));
        return result;
    }

    public String getValue(String key, String fallback) {
        String value = rawValue(key);
        return value == null || value.isBlank() ? fallback : value;
    }

    public int getInt(String key, int fallback) {
        try {
            return Integer.parseInt(getValue(key, String.valueOf(fallback)));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public boolean getBoolean(String key, boolean fallback) {
        String value = rawValue(key);
        return value == null || value.isBlank() ? fallback : Boolean.parseBoolean(value);
    }

    public String getAiApiKey(String fallback) {
        String value = rawValue(AI_API_KEY);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return crypto.decrypt(value);
    }

    public String getAiBaseUrl(String fallback) {
        return getValue(AI_BASE_URL, fallback);
    }

    public String getAiModel(String fallback) {
        return getValue(AI_MODEL, fallback);
    }

    public int getAiTimeoutSeconds(int fallback) {
        return getInt(AI_TIMEOUT, fallback);
    }

    public int getAiRateLimitPerMinute(int fallback) {
        return getInt(AI_RATE_LIMIT, fallback);
    }

    public boolean isAiEnabled(boolean fallback) {
        return getBoolean(AI_ENABLED, fallback);
    }

    public int getDefaultUploadMaxSizeMb() {
        return getInt(UPLOAD_MAX_SIZE, 5);
    }

    public String getDefaultPlantCategory() {
        return getValue(DEFAULT_CATEGORY, "观赏植物");
    }

    private String rawValue(String key) {
        return repository.findByKey(key)
                .map(SystemSetting::getSettingValue)
                .orElse(null);
    }

    private void validate(SystemSettingsRequest request) {
        if (request == null || request.systemName() == null
                || request.systemName().trim().isEmpty()
                || request.systemName().trim().length() > 30) {
            throw new IllegalArgumentException("系统名称长度应在1-30个字符之间");
        }
        if (request.defaultPlantCategory() == null
                || request.defaultPlantCategory().trim().isEmpty()
                || request.defaultPlantCategory().trim().length() > 100) {
            throw new IllegalArgumentException("默认植物分类不能为空，且不能超过100个字符");
        }
        if (request.uploadMaxSizeMb() == null
                || request.uploadMaxSizeMb() < 1 || request.uploadMaxSizeMb() > 20) {
            throw new IllegalArgumentException("图片大小限制应在1-20MB之间");
        }
        if (request.aiEnabled() == null || request.aiBaseUrl() == null
                || request.aiBaseUrl().isBlank() || request.aiModel() == null
                || request.aiModel().isBlank() || request.aiTimeoutSeconds() == null
                || request.aiRateLimitPerMinute() == null) {
            throw new IllegalArgumentException("AI 配置不完整");
        }
        try {
            URI uri = URI.create(request.aiBaseUrl().trim());
            if (!"http".equalsIgnoreCase(uri.getScheme())
                    && !"https".equalsIgnoreCase(uri.getScheme())) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("AI 接口地址必须是有效的 HTTP/HTTPS URL");
        }
        if (request.aiTimeoutSeconds() < 5 || request.aiTimeoutSeconds() > 180) {
            throw new IllegalArgumentException("AI 超时时间应在5-180秒之间");
        }
        if (request.aiRateLimitPerMinute() < 1 || request.aiRateLimitPerMinute() > 200) {
            throw new IllegalArgumentException("AI 每分钟调用上限应在1-200之间");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
