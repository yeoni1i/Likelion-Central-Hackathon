package com.likelion.hackatonbe.domain.analysis.dto;

import java.time.LocalDate;
import java.util.List;

public record WeeklyAnalysisResponse(
        LocalDate startDate,
        LocalDate endDate,
        long totalCount,
        double dailyAverage,
        List<DailyScratchCountDto> daily
) {
}