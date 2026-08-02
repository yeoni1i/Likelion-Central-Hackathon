package com.likelion.hackatonbe.domain.scratch.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ScratchTimelineItem(
        Instant startTs,
        BigDecimal durationSec,
        Integer intensity
) {
}