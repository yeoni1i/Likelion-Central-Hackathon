package com.likelion.hackatonbe.domain.model.dto;
import jakarta.validation.constraints.NotBlank;
public record EventFeedbackRequest(
        @NotBlank String clientEventId,
        @NotBlank String label,
        String context,
        boolean rawWindowIncluded
) {}
