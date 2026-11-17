package com.example.plant;

import com.example.plant.dto.SystemSettingsRequest;
import com.example.plant.repository.SystemSettingRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.ai.openai.api-key=")
@AutoConfigureMockMvc
@Transactional
class SystemSettingsIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SystemSettingRepository settingRepository;

    @Test
    void publicSettingsCanBeReadWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/settings/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.systemName").isNotEmpty());
    }

    @Test
    void adminCanEncryptAndUpdateAiSettings() throws Exception {
        String token = login("admin", "123456");
        SystemSettingsRequest request = request();

        mockMvc.perform(put("/api/admin/settings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.systemName").value("保育设置测试"))
                .andExpect(jsonPath("$.data.aiApiKeyConfigured").value(true))
                .andExpect(jsonPath("$.data.aiApiKey").doesNotExist());

        String encrypted = settingRepository.findByKey("ai.apiKey").orElseThrow().getSettingValue();
        assertNotEquals("sk-plant-test", encrypted);
        assertFalse(encrypted.contains("sk-plant-test"));
        assertTrue(encrypted.startsWith("enc:"));
    }

    @Test
    void reviewerCannotReadAdminSettings() throws Exception {
        String token = login("reviewer", "123456");
        mockMvc.perform(get("/api/admin/settings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    private String login(String username, String password) throws Exception {
        String body = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username, "password", password))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(body);
        return json.path("data").path("token").asText();
    }

    private SystemSettingsRequest request() {
        return new SystemSettingsRequest(
                "保育设置测试",
                "400-800-1234",
                "珍稀植物",
                8,
                true,
                "https://api.openai.com/v1",
                "gpt-4.1-mini-test",
                45,
                15,
                "sk-plant-test",
                false);
    }
}
