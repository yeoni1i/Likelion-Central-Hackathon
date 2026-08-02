package com.likelion.hackatonbe.domain.scratch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "scratch_event",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_scratch_event_user_client",
                columnNames = {"user_id", "client_event_id"}
        ),
        indexes = @Index(name = "idx_scratch_event_user_start", columnList = "user_id,start_ts")
)
public class ScratchEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Column(name = "client_event_id", nullable = false, length = 64)
    private String clientEventId;

    @Column(name = "model_version", nullable = false, length = 32)
    private String modelVersion;

    @Column(name = "calibration_version", nullable = false)
    private Integer calibrationVersion;

    @Column(name = "start_ts", nullable = false)
    private Instant startTs;

    @Column(name = "end_ts", nullable = false)
    private Instant endTs;

    @Column(name = "duration_sec", nullable = false, precision = 10, scale = 3)
    private BigDecimal durationSec;

    @Column(nullable = false)
    private Integer intensity;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal confidence;

    @Column(name = "window_count", nullable = false)
    private Integer windowCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "wear_position", nullable = false, length = 8)
    private WearPosition wearPosition;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ScratchEvent() {
    }

    public ScratchEvent(
            Long userId,
            Long deviceId,
            String clientEventId,
            String modelVersion,
            Integer calibrationVersion,
            Instant startTs,
            Instant endTs,
            BigDecimal durationSec,
            Integer intensity,
            BigDecimal confidence,
            Integer windowCount,
            WearPosition wearPosition,
            Instant createdAt
    ) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.clientEventId = clientEventId;
        this.modelVersion = modelVersion;
        this.calibrationVersion = calibrationVersion;
        this.startTs = startTs;
        this.endTs = endTs;
        this.durationSec = durationSec;
        this.intensity = intensity;
        this.confidence = confidence;
        this.windowCount = windowCount;
        this.wearPosition = wearPosition;
        this.createdAt = createdAt;
    }

    public String getClientEventId() {
        return clientEventId;
    }

    public BigDecimal getDurationSec() {
        return durationSec;
    }

    public Integer getIntensity() {
        return intensity;
    }

    public Instant getStartTs() {
        return startTs;
    }
}
