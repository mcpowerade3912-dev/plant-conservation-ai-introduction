package com.example.plant.service;

import com.example.plant.common.AiServiceUnavailableException;
import com.example.plant.dto.IntroductionAssistRequest;
import com.example.plant.dto.IntroductionAssistResponse;
import com.example.plant.dto.PlantSpeciesCandidate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiIntroductionServiceTest {
    private PlantAiClient client;
    private JdbcTemplate jdbcTemplate;
    private SystemSettingsService settingsService;
    private AiIntroductionService service;

    @BeforeEach
    void setUp() {
        client = mock(PlantAiClient.class);
        jdbcTemplate = mock(JdbcTemplate.class);
        settingsService = mock(SystemSettingsService.class);
        when(settingsService.getAiRateLimitPerMinute(anyInt())).thenReturn(20);
        when(settingsService.getAiModel(anyString())).thenReturn("test-model");
        service = new AiIntroductionService(
                client, jdbcTemplate, new ObjectMapper(), 20, "test-model", settingsService);
    }

    @Test
    void returnsCandidatesAndWritesAuditLog() {
        IntroductionAssistResponse expected = new IntroductionAssistResponse(
                List.of(new PlantSpeciesCandidate(
                        "Prunus serrulata", "蔷薇科", "李属", "日本",
                        List.of("日本晚樱"), 0.92, "建议人工核对")),
                List.of("喜光，注意排水"),
                "结果仅供参考");
        when(client.assist(any())).thenReturn(expected);

        IntroductionAssistResponse result = service.assist(
                new IntroductionAssistRequest("日本晚樱", "日本", "校园景观"),
                "user1");

        assertEquals(expected, result);
        verify(jdbcTemplate).update(
                anyString(), eq("user1"), eq("日本晚樱"), eq("日本"),
                eq("test-model"), eq(1), anyString(), isNull(),
                anyLong(), any());
    }

    @Test
    void propagatesAiUnavailableError() {
        when(client.assist(any()))
                .thenThrow(new AiServiceUnavailableException("未配置 API Key"));

        assertThrows(AiServiceUnavailableException.class, () ->
                service.assist(
                        new IntroductionAssistRequest("银杏", "中国", "校园树木"),
                        "user1"));
    }
}
