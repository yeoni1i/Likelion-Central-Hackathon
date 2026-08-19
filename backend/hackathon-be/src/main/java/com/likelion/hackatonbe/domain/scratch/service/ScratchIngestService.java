package com.likelion.hackatonbe.domain.scratch.service;

import com.likelion.hackatonbe.core.time.TimeProvider;
import com.likelion.hackatonbe.domain.device.entity.WatchDevice;
import com.likelion.hackatonbe.domain.device.repository.WatchDeviceRepository;
import com.likelion.hackatonbe.domain.scratch.dto.ScratchEventRequest;
import com.likelion.hackatonbe.domain.scratch.dto.ScratchIngestRequest;
import com.likelion.hackatonbe.domain.scratch.dto.ScratchIngestResponse;
import com.likelion.hackatonbe.domain.scratch.entity.IngestBatch;
import com.likelion.hackatonbe.domain.scratch.entity.ScratchEvent;
import com.likelion.hackatonbe.domain.scratch.repository.IngestBatchRepository;
import com.likelion.hackatonbe.domain.scratch.repository.ScratchEventRepository;
import com.likelion.hackatonbe.global.error.BusinessException;
import com.likelion.hackatonbe.global.error.ErrorCode;
import com.likelion.hackatonbe.domain.device.service.DeviceDetectionService;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScratchIngestService {

    private final ScratchEventRepository eventRepository;
    private final IngestBatchRepository batchRepository;
    private final ScratchEventValidator validator;
    private final TimeProvider timeProvider;
    private final WatchDeviceRepository watchDeviceRepository;
    private final DeviceDetectionService deviceDetectionService;

    public ScratchIngestService(
            ScratchEventRepository eventRepository,
            IngestBatchRepository batchRepository,
            ScratchEventValidator validator,
            TimeProvider timeProvider,
            WatchDeviceRepository watchDeviceRepository,
            DeviceDetectionService deviceDetectionService
    ) {
        this.eventRepository = eventRepository;
        this.batchRepository = batchRepository;
        this.validator = validator;
        this.timeProvider = timeProvider;
        this.watchDeviceRepository = watchDeviceRepository;
        this.deviceDetectionService = deviceDetectionService;
    }

    @Transactional
    public ScratchIngestResponse ingest(
            String idempotencyKey,
            boolean backfill,
            ScratchIngestRequest request
    ) {
        /*
         * 워치가 전송한 deviceId는
         * watch_devices 테이블의 PK(id)이다.
         */
        WatchDevice watchDevice =
                watchDeviceRepository.findById(request.deviceId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "등록되지 않은 워치입니다. deviceId="
                                                + request.deviceId()
                                )
                        );

        /*
         * WatchDevice -> Child -> User 관계를 따라
         * 실제 부모 사용자의 userId를 가져온다.
         */
        Long userId =
                watchDevice
                        .getChild()
                        .getUser()
                        .getId();

        return batchRepository
                .findByUserIdAndIdempotencyKey(
                        userId,
                        idempotencyKey
                )
                .map(this::toPreviousResponse)
                .orElseGet(() ->
                        ingestNewBatch(
                                userId,
                                idempotencyKey,
                                backfill,
                                request
                        )
                );
    }

    private ScratchIngestResponse ingestNewBatch(
            Long userId,
            String idempotencyKey,
            boolean backfill,
            ScratchIngestRequest request
    ) {
        request.events()
                .forEach(validator::validate);

        Instant now =
                timeProvider.now();

        Instant oldestEventTs =
                request.events()
                        .stream()
                        .map(ScratchEventRequest::startTs)
                        .min(Instant::compareTo)
                        .orElseThrow();

        Instant newestEventTs =
                request.events()
                        .stream()
                        .map(ScratchEventRequest::endTs)
                        .max(Instant::compareTo)
                        .orElseThrow();

        long ageHours =
                Duration.between(
                        oldestEventTs,
                        now
                ).toHours();

        if (ageHours > 72) {
            throw new BusinessException(
                    ErrorCode.BACKFILL_TOO_OLD
            );
        }

        if (ageHours > 1 && !backfill) {
            throw new BusinessException(
                    ErrorCode.BACKFILL_HEADER_REQUIRED
            );
        }

        Set<String> seenInRequest =
                new HashSet<>();

        List<String> clientIds =
                request.events()
                        .stream()
                        .map(
                                ScratchEventRequest::clientEventId
                        )
                        .toList();

        Set<String> storedIds =
                new HashSet<>(
                        eventRepository
                                .findAllByUserIdAndClientEventIdIn(
                                        userId,
                                        clientIds
                                )
                                .stream()
                                .map(
                                        ScratchEvent::getClientEventId
                                )
                                .toList()
                );

        List<ScratchEvent> newEvents =
                request.events()
                        .stream()
                        .filter(event ->
                                seenInRequest.add(
                                        event.clientEventId()
                                )
                        )
                        .filter(event ->
                                !storedIds.contains(
                                        event.clientEventId()
                                )
                        )
                        .map(event ->
                                toEntity(
                                        userId,
                                        request,
                                        event,
                                        now
                                )
                        )
                        .toList();

        eventRepository.saveAll(
                newEvents
        );

        if (!newEvents.isEmpty()) {
            ScratchEvent latestEvent = newEvents.stream()
                    .max(Comparator.comparing(ScratchEvent::getEndTs))
                    .orElseThrow();

            deviceDetectionService.updateScratch(
                    request.deviceId(),
                    latestEvent.getIntensity(),
                    now
            );
        }

        int accepted =
                newEvents.size();

        int duplicated =
                request.events().size()
                        - accepted;

        batchRepository.save(
                new IngestBatch(
                        userId,
                        idempotencyKey,
                        accepted,
                        duplicated,
                        request.wearSecondsInBatch(),
                        now,
                        request.deviceId(),
                        oldestEventTs,
                        newestEventTs
                )
        );

        return new ScratchIngestResponse(
                accepted,
                duplicated,
                List.of(),
                now
        );
    }

    private ScratchEvent toEntity(
            Long userId,
            ScratchIngestRequest batch,
            ScratchEventRequest event,
            Instant now
    ) {
        return new ScratchEvent(
                userId,
                batch.deviceId(),
                event.clientEventId(),
                batch.modelVersion(),
                batch.calibrationVersion(),
                event.startTs(),
                event.endTs(),
                event.durationSec(),
                event.intensity(),
                event.confidence(),
                event.windowCount(),
                event.wearPosition(),
                now
        );
    }

    private ScratchIngestResponse toPreviousResponse(
            IngestBatch batch
    ) {
        return new ScratchIngestResponse(
                batch.getAccepted(),
                batch.getDuplicated(),
                List.of(),
                batch.getWatermarkTs()
        );
    }
}