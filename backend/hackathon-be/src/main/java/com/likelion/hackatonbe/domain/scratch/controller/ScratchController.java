package com.likelion.hackatonbe.domain.scratch.controller;

import com.likelion.hackatonbe.domain.scratch.dto.*;
import com.likelion.hackatonbe.domain.scratch.service.DailyScratchService;
import com.likelion.hackatonbe.domain.scratch.service.ScratchIngestService;
import com.likelion.hackatonbe.domain.scratch.service.SyncStatusService;
import com.likelion.hackatonbe.global.error.BusinessException;
import com.likelion.hackatonbe.global.error.ErrorCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/scratch")
public class ScratchController {

    private final ScratchIngestService ingestService;
    private final DailyScratchService dailyScratchService;
    private final SyncStatusService syncStatusService;

    public ScratchController(
            ScratchIngestService ingestService,
            DailyScratchService dailyScratchService,
            SyncStatusService syncStatusService
    ) {
        this.ingestService = ingestService;
        this.dailyScratchService = dailyScratchService;
        this.syncStatusService = syncStatusService;
    }

    @PostMapping("/ingest/scratch-events")
    public ResponseEntity<ScratchIngestResponse> ingest(
            @RequestHeader("X-User-Id") @Positive Long userId,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestHeader(value = "X-Backfill", defaultValue = "false") boolean backfill,
            @Valid @RequestBody ScratchIngestRequest request
    ) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new BusinessException(ErrorCode.IDEMPOTENCY_KEY_REQUIRED);
        }
        return ResponseEntity.accepted().body(ingestService.ingest(userId, idempotencyKey, backfill, request));
    }

    @GetMapping("/reports/daily")
    public DailyScratchResponse daily(
            @RequestHeader("X-User-Id") @Positive Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "Asia/Seoul") String timezone
    ) {
        return dailyScratchService.getDaily(userId, date, ZoneId.of(timezone));
    }

    @GetMapping("/events")
    public ScratchTimelineResponse events(
            @RequestHeader("X-User-Id") @Positive Long userId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam(defaultValue = "Asia/Seoul")
            String timezone
    ) {
        return dailyScratchService.getTimeline(
                userId,
                date,
                ZoneId.of(timezone)
        );
    }

    @GetMapping("/sync/status")
    public SyncStatusResponse syncStatus(
            @RequestHeader("X-User-Id") @Positive Long userId,
            @RequestParam @Positive Long deviceId
    ) {
        return syncStatusService.get(userId, deviceId);
    }
}
