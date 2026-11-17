package com.example.plant.dto;

import javax.validation.constraints.Size;

public record ProfileRequest(
        @Size(max = 50) String nickname,
        @Size(max = 20) String phone,
        @Size(max = 100) String email,
        @Size(max = 100) String password
) {
}
