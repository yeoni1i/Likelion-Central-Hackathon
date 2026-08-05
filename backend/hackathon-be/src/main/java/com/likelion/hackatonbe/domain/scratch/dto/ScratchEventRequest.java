package com.likelion.hackatonbe.domain.scratch.dto;

import com.likelion.hackatonbe.domain.scratch.entity.WearPosition;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

public record ScratchEventRequest(
        @NotBlank String clientEventId,
        @NotNull Instant startTs,
        @NotNull Instant endTs,
        @NotNull @DecimalMin("0.1") @DecimalMax("3600.0") BigDecimal durationSec,
        @NotNull @Min(1) @Max(5) Integer intensity,
        @NotNull @DecimalMin("0.0") @DecimalMax("1.0") BigDecimal confidence,
        @NotNull @Min(1) Integer windowCount,
        @NotNull WearPosition wearPosition
) {
}
