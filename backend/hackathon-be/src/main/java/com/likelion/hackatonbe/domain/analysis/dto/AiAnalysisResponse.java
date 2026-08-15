package com.likelion.hackatonbe.domain.analysis.dto;

public record AiAnalysisResponse(
        String summary,
        String possibleCause,
        String advice
) {
}