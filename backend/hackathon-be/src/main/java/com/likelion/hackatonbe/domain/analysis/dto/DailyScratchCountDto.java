package com.likelion.hackatonbe.domain.analysis.dto;

import java.time.LocalDate;

public record DailyScratchCountDto(
        LocalDate date,
        long count
) {
}