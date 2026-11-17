package com.example.plant.service;

import com.example.plant.dto.IntroductionAssistRequest;
import com.example.plant.dto.IntroductionAssistResponse;

public interface PlantAiClient {
    IntroductionAssistResponse assist(IntroductionAssistRequest request);
}
