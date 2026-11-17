package com.example.plant.dto;

import java.util.List;

public record PlantSpeciesCandidate(
        String scientificName,
        String family,
        String genus,
        String origin,
        List<String> commonNames,
        Double confidence,
        String note
) {
}
