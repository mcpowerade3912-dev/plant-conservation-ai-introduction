package com.example.plant.dto;

import java.util.List;

public record IntroductionAssistResponse(
        List<PlantSpeciesCandidate> candidates,
        List<String> careTips,
        String note
) {
}
