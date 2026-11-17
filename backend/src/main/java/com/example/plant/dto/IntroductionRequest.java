package com.example.plant.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public record IntroductionRequest(
        @NotBlank @Size(max = 100) String plantName,
        @Size(max = 100) String scientificName,
        @NotBlank @Size(max = 100) String origin,
        @NotNull LocalDate introductionDate,
        @NotNull Integer quantity,
        @Size(max = 500) String description,
        @Size(max = 500) String imageUrl
) {
}
