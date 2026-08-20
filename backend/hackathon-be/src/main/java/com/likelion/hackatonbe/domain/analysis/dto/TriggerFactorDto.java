package com.likelion.hackatonbe.domain.analysis.dto;

public record TriggerFactorDto(
        int rank,
        String type,
        String factor,
        String reason
) {
}