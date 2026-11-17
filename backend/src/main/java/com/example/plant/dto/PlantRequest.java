package com.example.plant.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record PlantRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 100) String scientificName,
        @Size(max = 100) String category,
        @Size(max = 100) String origin,
        @NotNull Integer quantity,
        @NotBlank String status,
        @Size(max = 500) String imageUrl
) {
}
