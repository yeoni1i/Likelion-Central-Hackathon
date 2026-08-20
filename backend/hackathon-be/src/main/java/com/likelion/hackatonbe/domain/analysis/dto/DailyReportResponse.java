package com.likelion.hackatonbe.domain.analysis.dto;

import java.time.LocalDate;
import java.util.List;


//프론트 백 사이 최종 계약

public record DailyReportResponse(
        LocalDate date,

        ScratchSummaryDto scratchSummary,

        EnvironmentSummaryDto environment,

        List<HourlyScratchDto> hourlyScratch,

        List<DailyScratchCountDto> weeklyTrend,

        ReportAiAnalysisDto analysis,

        RiskFoodSectionDto riskFoodSection
) {
}