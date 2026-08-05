package com.likelion.hackatonbe.domain.scratch.dto;

import java.time.Instant;
import java.util.List;

public record ScratchIngestResponse(
        int accepted,
        int duplicated,
        List<RejectedEvent> rejected,
        Instant serverWatermarkTs
) {
    public record RejectedEvent(String clientEventId, String reason) {
    }
}
