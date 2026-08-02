package com.likelion.hackatonbe.domain.scratch.dto;

import java.time.LocalDate;
import java.util.List;

public record ScratchTimelineResponse(
        LocalDate date,
        List<ScratchTimelineItem> events
) {
}
