package com.example.plant.service;

import com.example.plant.common.AiServiceUnavailableException;
import com.example.plant.dto.IntroductionAssistRequest;
import com.example.plant.dto.IntroductionAssistResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AiIntroductionService {
    private static final Logger log = LoggerFactory.getLogger(AiIntroductionService.class);
    private final PlantAiClient client;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final int rateLimitPerMinute;
    private final String model;
    private final SystemSettingsService systemSettingsService;
    private final Map<String, Deque<Long>> requestWindows = new ConcurrentHashMap<>();

    public AiIntroductionService(
            PlantAiClient client,
            JdbcTemplate jdbcTemplate,
            ObjectMapper objectMapper,
            @Value("${app.ai.rate-limit-per-minute:20}") int rateLimitPerMinute,
            @Value("${app.ai.openai.model:gpt-4.1-mini}") String model,
            SystemSettingsService systemSettingsService) {
        this.client = client;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.rateLimitPerMinute = rateLimitPerMinute;
        this.model = model;
        this.systemSettingsService = systemSettingsService;
    }

    public IntroductionAssistResponse assist(IntroductionAssistRequest request, String username) {
        enforceRateLimit(username);
        long start = System.currentTimeMillis();
        try {
            IntroductionAssistResponse response = client.assist(request);
            writeAudit(username, request, true, response, null, start);
            return response;
        } catch (AiServiceUnavailableException e) {
            writeAudit(username, request, false, null, e.getMessage(), start);
            throw e;
        } catch (Exception e) {
            writeAudit(username, request, false, null, e.getMessage(), start);
            throw new AiServiceUnavailableException("AI 引种助手暂时不可用", e);
        }
    }

    private void enforceRateLimit(String username) {
        long now = System.currentTimeMillis();
        Deque<Long> window = requestWindows.computeIfAbsent(
                username, ignored -> new ArrayDeque<>());
        synchronized (window) {
            while (!window.isEmpty() && window.peekFirst() < now - 60_000) {
                window.pollFirst();
            }
            if (window.size() >= systemSettingsService.getAiRateLimitPerMinute(rateLimitPerMinute)) {
                throw new AiServiceUnavailableException("AI 调用过于频繁，请稍后再试");
            }
            window.addLast(now);
        }
    }

    private void writeAudit(String username, IntroductionAssistRequest request,
                            boolean success, IntroductionAssistResponse response,
                            String error, long start) {
        try {
            jdbcTemplate.update("""
                    INSERT INTO ai_introduction_log
                    (username, plant_name, origin, model, success, response_json,
                     error_message, duration_ms, create_time)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """, username, request.plantName(), request.origin(),
                    systemSettingsService.getAiModel(model),
                    success ? 1 : 0,
                    response == null ? null : objectMapper.writeValueAsString(response),
                    error, System.currentTimeMillis() - start, LocalDateTime.now());
        } catch (Exception e) {
            log.warn("AI 引种助手审计日志写入失败: {}", e.getMessage());
        }
    }
}
