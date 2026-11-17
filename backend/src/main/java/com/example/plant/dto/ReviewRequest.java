package com.example.plant.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record ReviewRequest(@NotBlank String result, @Size(max = 500) String comment) {
}
