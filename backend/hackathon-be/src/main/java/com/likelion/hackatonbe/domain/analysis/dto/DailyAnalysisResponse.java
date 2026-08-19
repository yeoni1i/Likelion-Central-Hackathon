package com.likelion.hackatonbe.domain.analysis.dto;

import java.time.LocalDate;
import java.util.List;

public record DailyAnalysisResponse(
        LocalDate date,
        long scratchCount,
        Integer peakHour,
        List<HourlyScratchDto> hourly
) {
}