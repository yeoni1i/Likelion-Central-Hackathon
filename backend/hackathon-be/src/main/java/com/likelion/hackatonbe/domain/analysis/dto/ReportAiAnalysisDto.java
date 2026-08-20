package com.likelion.hackatonbe.domain.analysis.dto;

import java.util.List;

public record ReportAiAnalysisDto(
        String summary,
        String pattern,
        String carePoint,
        String triggerFactor,
        List<TriggerFactorDto> triggerFactors
) {
}