package com.likelion.hackatonbe.domain.scratch.service;

import com.likelion.hackatonbe.domain.scratch.dto.DailyScratchResponse;
import com.likelion.hackatonbe.domain.scratch.dto.ScratchTimelineItem;
import com.likelion.hackatonbe.domain.scratch.dto.ScratchTimelineResponse;
import com.likelion.hackatonbe.domain.scratch.entity.ScratchEvent;
import com.likelion.hackatonbe.domain.scratch.repository.ScratchEventRepository;
import com.likelion.hackatonbe.domain.scratch.repository.IngestBatchRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DailyScratchService {

    private final ScratchEventRepository eventRepository;
    private final IngestBatchRepository batchRepository;

    public DailyScratchService(
            ScratchEventRepository eventRepository,
            IngestBatchRepository batchRepository


    ) {
        this.eventRepository = eventRepository;
        this.batchRepository = batchRepository;
    }


    @Transactional(readOnly = true)
    public DailyScratchResponse getDaily(Long userId, LocalDate date, ZoneId zoneId) {
        Instant from = date.atStartOfDay(zoneId).toInstant();
        Instant to = date.plusDays(1).atStartOfDay(zoneId).toInstant();
        List<ScratchEvent> events =
                eventRepository.findAllByUserIdAndStartTsGreaterThanEqualAndStartTsLessThan(userId, from, to);

        BigDecimal totalSeconds = events.stream()
                .map(ScratchEvent::getDurationSec)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageIntensity = events.isEmpty()
                ? BigDecimal.ZERO
                : events.stream()
                        .map(event -> BigDecimal.valueOf(event.getIntensity()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(events.size()), 2, RoundingMode.HALF_UP);

        long wearSeconds = batchRepository
                .findAllByUserIdAndWatermarkTsGreaterThanEqualAndWatermarkTsLessThan(userId, from, to)
                .stream()
                .mapToLong(batch -> batch.getWearSeconds())
                .sum();
        BigDecimal wearHours = BigDecimal.valueOf(wearSeconds)
                .divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_UP);
        BigDecimal normalized = wearHours.signum() == 0
                ? BigDecimal.ZERO
                : totalSeconds.divide(wearHours, 2, RoundingMode.HALF_UP);

        return new DailyScratchResponse(
                date,
                events.size(),
                totalSeconds,
                averageIntensity,
                wearHours,
                normalized
        );
    }


    @Transactional(readOnly = true)
    public ScratchTimelineResponse getTimeline(
            Long userId,
            LocalDate date,
            ZoneId zoneId
    ) {

        Instant from = date.atStartOfDay(zoneId).toInstant();
        Instant to = date.plusDays(1).atStartOfDay(zoneId).toInstant();

        List<ScratchTimelineItem> timelineItems =
                eventRepository
                        .findAllByUserIdAndStartTsGreaterThanEqualAndStartTsLessThan(
                                userId,
                                from,
                                to
                        )
                        .stream()
                        .map(event -> new ScratchTimelineItem(
                                event.getStartTs(),
                                event.getDurationSec(),
                                event.getIntensity()
                        ))
                        .toList();

        return new ScratchTimelineResponse(
                date,
                timelineItems
        );
    }

}
