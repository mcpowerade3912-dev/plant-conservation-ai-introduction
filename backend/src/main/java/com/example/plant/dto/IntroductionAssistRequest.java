package com.example.plant.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record IntroductionAssistRequest(
        @NotBlank(message = "植物名称不能为空")
        @Size(max = 100, message = "植物名称不能超过100个字符")
        String plantName,
        @Size(max = 100, message = "来源地不能超过100个字符")
        String origin,
        @Size(max = 500, message = "描述不能超过500个字符")
        String description
) {
}
