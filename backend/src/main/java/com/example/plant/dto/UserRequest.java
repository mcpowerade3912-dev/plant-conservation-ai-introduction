package com.example.plant.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record UserRequest(
        @NotBlank @Size(max = 50) String username,
        @Size(max = 100) String password,
        @Size(max = 50) String nickname,
        @NotBlank String role,
        @NotNull Integer status
) {
}
