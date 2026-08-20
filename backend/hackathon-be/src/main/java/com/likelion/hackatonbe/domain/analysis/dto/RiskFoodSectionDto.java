package com.likelion.hackatonbe.domain.analysis.dto;

import java.util.List;

public record RiskFoodSectionDto(
        String title,
        List<RiskFoodDto> foods
) {}
