package com.example.plant.service;

import com.example.plant.dto.PlantRequest;
import com.example.plant.model.Plant;
import com.example.plant.repository.PlantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlantService {
    private final PlantRepository repo;

    public PlantService(PlantRepository repo) {
        this.repo = repo;
    }

    public List<Plant> list() {
        return repo.findAll();
    }

    public Plant create(PlantRequest request) {
        Plant plant = new Plant();
        apply(plant, request);
        return repo.insert(plant);
    }

    public Plant update(Long id, PlantRequest request) {
        Plant plant = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("植物档案不存在"));
        apply(plant, request);
        repo.update(plant);
        return plant;
    }

    private void apply(Plant plant, PlantRequest request) {
        plant.setName(request.name());
        plant.setScientificName(request.scientificName());
        plant.setCategory(request.category());
        plant.setOrigin(request.origin());
        plant.setQuantity(request.quantity());
        plant.setStatus(request.status());
        plant.setImageUrl(request.imageUrl());
    }

    public void delete(Long id) {
        repo.delete(id);
    }
}
