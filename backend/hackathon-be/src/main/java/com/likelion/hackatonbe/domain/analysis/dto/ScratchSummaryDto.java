package com.likelion.hackatonbe.domain.analysis.dto;


public record ScratchSummaryDto(
        long count,
        double totalSeconds,
        double weeklyAverage,
        double changePercent
) {
}