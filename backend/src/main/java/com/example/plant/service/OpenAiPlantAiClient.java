package com.example.plant.service;

import com.example.plant.common.AiServiceUnavailableException;
import com.example.plant.dto.IntroductionAssistRequest;
import com.example.plant.dto.IntroductionAssistResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class OpenAiPlantAiClient implements PlantAiClient {
    private static final String INSTRUCTIONS = """
            你是植物引种信息录入助手。请根据用户提供的信息给出可能的植物分类候选，
            只返回有根据的信息，不确定时降低 confidence 并在 note 中说明，不要编造。
            候选最多3个；confidence 为0到1的小数；养护要点只给出通用且保守的建议。
            所有文本使用简体中文，学名使用拉丁学名。
            """;

    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String baseUrl;
    private final String model;
    private final int timeoutSeconds;
    private final SystemSettingsService systemSettingsService;

    public OpenAiPlantAiClient(
            ObjectMapper objectMapper,
            @Value("${app.ai.openai.api-key:}") String apiKey,
            @Value("${app.ai.openai.base-url:https://api.openai.com/v1}") String baseUrl,
            @Value("${app.ai.openai.model:gpt-4.1-mini}") String model,
            @Value("${app.ai.openai.timeout-seconds:60}") int timeoutSeconds,
            SystemSettingsService systemSettingsService) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.model = model;
        this.timeoutSeconds = timeoutSeconds;
        this.systemSettingsService = systemSettingsService;
    }

    @Override
    public IntroductionAssistResponse assist(IntroductionAssistRequest request) {
        AiRuntimeConfig config = currentConfig();
        if (!config.enabled() || config.apiKey().isBlank()) {
            throw new AiServiceUnavailableException("AI 引种助手尚未配置 API Key");
        }
        try {
            Map<String, Object> input = new LinkedHashMap<>();
            input.put("植物名称", request.plantName());
            input.put("来源地", request.origin() == null ? "" : request.origin());
            input.put("描述", request.description() == null ? "" : request.description());

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", config.model());
            body.put("instructions", INSTRUCTIONS);
            body.put("input", List.of(Map.of(
                    "role", "user",
                    "content", objectMapper.writeValueAsString(input))));
            body.put("store", false);
            body.put("max_output_tokens", 900);
            body.put("text", Map.of("format", Map.of(
                    "type", "json_schema",
                    "name", "plant_introduction_assist",
                    "strict", true,
                    "schema", responseSchema())));

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(Math.min(config.timeoutSeconds(), 15)))
                    .build();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(trimSlash(config.baseUrl()) + "/responses"))
                    .timeout(Duration.ofSeconds(config.timeoutSeconds()))
                    .header("Authorization", "Bearer " + config.apiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            objectMapper.writeValueAsString(body), StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new AiServiceUnavailableException(
                        "AI 服务请求失败，HTTP " + response.statusCode());
            }
            String outputText = extractOutputText(objectMapper.readTree(response.body()));
            IntroductionAssistResponse result = objectMapper.readValue(
                    outputText, IntroductionAssistResponse.class);
            if (result.candidates() == null || result.candidates().isEmpty()) {
                throw new AiServiceUnavailableException("AI 未返回可用的植物候选");
            }
            return result;
        } catch (AiServiceUnavailableException e) {
            throw e;
        } catch (Exception e) {
            throw new AiServiceUnavailableException("AI 引种助手调用失败", e);
        }
    }

    private String extractOutputText(JsonNode response) {
        JsonNode outputText = response.path("output_text");
        if (outputText.isTextual() && !outputText.asText().isBlank()) {
            return outputText.asText();
        }
        JsonNode output = response.path("output");
        if (output.isArray()) {
            for (JsonNode item : output) {
                if (!"message".equals(item.path("type").asText())) {
                    continue;
                }
                JsonNode content = item.path("content");
                if (!content.isArray()) {
                    continue;
                }
                for (JsonNode part : content) {
                    if ("output_text".equals(part.path("type").asText())
                            && part.path("text").isTextual()) {
                        return part.path("text").asText();
                    }
                }
            }
        }
        throw new AiServiceUnavailableException("AI 返回结果中没有可解析的文本");
    }

    private Map<String, Object> responseSchema() {
        Map<String, Object> candidateProperties = new LinkedHashMap<>();
        candidateProperties.put("scientificName", Map.of("type", "string"));
        candidateProperties.put("family", Map.of("type", "string"));
        candidateProperties.put("genus", Map.of("type", "string"));
        candidateProperties.put("origin", Map.of("type", "string"));
        candidateProperties.put("commonNames", Map.of(
                "type", "array",
                "items", Map.of("type", "string")));
        candidateProperties.put("confidence", Map.of(
                "type", "number",
                "minimum", 0,
                "maximum", 1));
        candidateProperties.put("note", Map.of("type", "string"));

        Map<String, Object> candidateSchema = new LinkedHashMap<>();
        candidateSchema.put("type", "object");
        candidateSchema.put("properties", candidateProperties);
        candidateSchema.put("required", List.of(
                "scientificName", "family", "genus", "origin",
                "commonNames", "confidence", "note"));
        candidateSchema.put("additionalProperties", false);

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("candidates", Map.of(
                "type", "array",
                "items", candidateSchema,
                "minItems", 1,
                "maxItems", 3));
        properties.put("careTips", Map.of(
                "type", "array",
                "items", Map.of("type", "string")));
        properties.put("note", Map.of("type", "string"));

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", properties);
        schema.put("required", List.of("candidates", "careTips", "note"));
        schema.put("additionalProperties", false);
        return schema;
    }

    private String trimSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private AiRuntimeConfig currentConfig() {
        String environmentKey = apiKey == null ? "" : apiKey.trim();
        String effectiveKey = systemSettingsService.getAiApiKey(environmentKey);
        return new AiRuntimeConfig(
                systemSettingsService.isAiEnabled(true),
                effectiveKey == null ? "" : effectiveKey.trim(),
                systemSettingsService.getAiBaseUrl(baseUrl),
                systemSettingsService.getAiModel(model),
                systemSettingsService.getAiTimeoutSeconds(timeoutSeconds));
    }

    private record AiRuntimeConfig(
            boolean enabled,
            String apiKey,
            String baseUrl,
            String model,
            int timeoutSeconds) {
    }
}
