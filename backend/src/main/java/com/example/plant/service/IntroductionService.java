package com.example.plant.service;

import com.example.plant.dto.IntroductionRequest;
import com.example.plant.model.IntroductionRecord;
import com.example.plant.model.Plant;
import com.example.plant.model.User;
import com.example.plant.repository.IntroductionRepository;
import com.example.plant.repository.PlantRepository;
import com.example.plant.repository.ReviewRepository;
import com.example.plant.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class IntroductionService {
    private final IntroductionRepository repo;
    private final UserRepository users;
    private final ReviewRepository reviews;
    private final PlantRepository plants;
    private final SystemSettingsService settingsService;

    public IntroductionService(IntroductionRepository repo, UserRepository users,
                               ReviewRepository reviews, PlantRepository plants,
                               SystemSettingsService settingsService) {
        this.repo = repo;
        this.users = users;
        this.reviews = reviews;
        this.plants = plants;
        this.settingsService = settingsService;
    }

    public List<IntroductionRecord> list(String username, String status, String keyword) {
        User user = users.findByUsername(username).orElseThrow();
        return "USER".equals(user.getRole())
                ? repo.findByUserId(user.getId())
                : repo.findAll(status, keyword);
    }

    public List<IntroductionRecord> pending() {
        return repo.findByStatus("PENDING");
    }

    public IntroductionRecord create(String username, IntroductionRequest request) {
        User user = users.findByUsername(username).orElseThrow();
        IntroductionRecord record = new IntroductionRecord();
        record.setUserId(user.getId());
        record.setCreatorName(user.getNickname());
        record.setPlantName(request.plantName());
        record.setScientificName(request.scientificName());
        record.setOrigin(request.origin());
        record.setIntroductionDate(request.introductionDate());
        record.setQuantity(request.quantity());
        record.setDescription(request.description());
        record.setImageUrl(request.imageUrl());
        record.setStatus("PENDING");
        return repo.insert(record);
    }

    public IntroductionRecord update(Long id, String username, IntroductionRequest request) {
        User user = users.findByUsername(username).orElseThrow();
        IntroductionRecord record = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("引种记录不存在"));
        if ("USER".equals(user.getRole()) && !record.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("无权修改他人记录");
        }
        record.setPlantName(request.plantName());
        record.setScientificName(request.scientificName());
        record.setOrigin(request.origin());
        record.setIntroductionDate(request.introductionDate());
        record.setQuantity(request.quantity());
        record.setDescription(request.description());
        record.setImageUrl(request.imageUrl());
        repo.update(record);
        return record;
    }

    public void delete(Long id, String username) {
        User user = users.findByUsername(username).orElseThrow();
        IntroductionRecord record = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("引种记录不存在"));
        if ("USER".equals(user.getRole()) && !record.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("无权删除他人记录");
        }
        repo.delete(id);
    }

    public Map<String, Object> review(Long id, Long reviewerId, String result, String comment) {
        IntroductionRecord record = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("引种记录不存在"));
        if (!record.getStatus().equals("PENDING")) {
            throw new IllegalArgumentException("该记录已经审核");
        }
        record.setStatus("APPROVED".equals(result) ? "APPROVED" : "REJECTED");
        repo.update(record);
        reviews.insert(id, reviewerId, result, comment);
        if ("APPROVED".equals(record.getStatus())) {
            Plant plant = new Plant();
            plant.setName(record.getPlantName());
            plant.setScientificName(record.getScientificName());
            plant.setCategory(settingsService.getDefaultPlantCategory());
            plant.setOrigin(record.getOrigin());
            plant.setQuantity(record.getQuantity());
            plant.setStatus("ACTIVE");
            plant.setImageUrl(record.getImageUrl());
            plant.setIntroRecordId(record.getId());
            plants.insert(plant);
        }
        return Map.of("id", id, "status", record.getStatus());
    }
}
