package com.likelion.hackatonbe.domain.scratch.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ScratchIngestRequest(
        @NotNull Long deviceId,
        @NotBlank String modelVersion,
        @NotNull @Min(1) Integer calibrationVersion,
        @NotNull @Min(1) @Max(1) Integer schemaVersion,
        @NotEmpty @Size(max = 500) List<@Valid ScratchEventRequest> events,
        @NotNull @Min(0) @Max(86400) Long wearSecondsInBatch
) {
}
