package com.likelion.hackatonbe.domain.scratch.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyScratchResponse(
        LocalDate date,
        long eventCount,
        BigDecimal totalSeconds,
        BigDecimal averageIntensity,
        BigDecimal wearHours,
        BigDecimal scratchSecondsPerWearHour
) {
}
