package com.likelion.hackatonbe.domain.scratch.dto;

import java.time.Instant;

public record SyncStatusResponse(
        Long deviceId,
        Instant serverWatermarkTs,
        Instant oldestAcceptedEventTs,
        Instant newestAcceptedEventTs,
        long retentionHours,
        String ackPolicy
) {
}
