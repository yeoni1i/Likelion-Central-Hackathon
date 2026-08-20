package com.likelion.hackatonbe.domain.analysis.dto;

public record EnvironmentSummaryDto(
        Double temperature,
        Integer humidity,
        String airQuality
) {
}