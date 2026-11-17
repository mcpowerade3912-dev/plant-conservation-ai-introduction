package com.example.plant.controller;

import com.example.plant.common.ApiResponse;
import com.example.plant.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class PlantImageController {
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp");

    private final Path imageDir;
    private final SystemSettingsService settingsService;

    public PlantImageController(
            @Value("${app.upload-dir:./uploads}") String uploadDir,
            SystemSettingsService settingsService) throws IOException {
        this.imageDir = Path.of(uploadDir, "plant-images")
                .toAbsolutePath().normalize();
        this.settingsService = settingsService;
        Files.createDirectories(imageDir);
    }

    @PostMapping("/plant-images")
    public ApiResponse<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("请选择图片");
        }
        int maxSizeMb = settingsService.getDefaultUploadMaxSizeMb();
        if (file.getSize() > maxSizeMb * 1024L * 1024L) {
            throw new IllegalArgumentException("图片不能超过" + maxSizeMb + "MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("仅支持 JPG、PNG、WebP 图片");
        }
        String extension = switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
        String filename = UUID.randomUUID().toString().replace("-", "") + extension;
        Path target = imageDir.resolve(filename).normalize();
        if (!target.startsWith(imageDir)) {
            throw new IllegalArgumentException("非法文件路径");
        }
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return ApiResponse.ok(Map.of("url", "/uploads/plant-images/" + filename));
    }
}
