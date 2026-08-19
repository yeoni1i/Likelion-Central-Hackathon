package com.likelion.hackatonbe.domain.device.dto;

import java.time.Instant;

public record CurrentDetectionResponse(
        Long deviceId,
        String detectionStatus,
        String scratchStatus,
        Integer intensity,
        Instant lastScratchAt
) {
}