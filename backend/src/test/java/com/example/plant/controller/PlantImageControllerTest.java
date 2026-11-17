package com.example.plant.controller;

import com.example.plant.service.SystemSettingsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlantImageControllerTest {
    @TempDir
    Path tempDir;

    @Test
    void storesValidPngImage() throws Exception {
        SystemSettingsService settings = mock(SystemSettingsService.class);
        when(settings.getDefaultUploadMaxSizeMb()).thenReturn(5);
        PlantImageController controller = new PlantImageController(tempDir.toString(), settings);
        MockMultipartFile file = new MockMultipartFile(
                "file", "plant.png", "image/png", new byte[]{(byte) 0x89, 0x50, 0x4e, 0x47});

        Map<String, String> result = controller.upload(file).data();

        assertTrue(result.get("url").startsWith("/uploads/plant-images/"));
        String filename = result.get("url").substring(result.get("url").lastIndexOf('/') + 1);
        assertTrue(Files.exists(tempDir.resolve("plant-images").resolve(filename)));
    }

    @Test
    void rejectsNonImageFile() throws Exception {
        SystemSettingsService settings = mock(SystemSettingsService.class);
        when(settings.getDefaultUploadMaxSizeMb()).thenReturn(5);
        PlantImageController controller = new PlantImageController(tempDir.toString(), settings);
        MockMultipartFile file = new MockMultipartFile(
                "file", "notes.txt", "text/plain", "not an image".getBytes());

        assertThrows(IllegalArgumentException.class, () -> controller.upload(file));
    }
}
