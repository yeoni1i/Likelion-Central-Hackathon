package com.likelion.hackatonbe.domain.scratch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(
        name = "ingest_batch",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_ingest_batch_user_key",
                columnNames = {"user_id", "idempotency_key"}
        )
)
public class IngestBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;

    @Column(nullable = false)
    private Integer accepted;

    @Column(nullable = false)
    private Integer duplicated;

    @Column(name = "wear_seconds", nullable = false)
    private Long wearSeconds;

    @Column(name = "watermark_ts", nullable = false)
    private Instant watermarkTs;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Column(name = "oldest_event_ts", nullable = false)
    private Instant oldestEventTs;

    @Column(name = "newest_event_ts", nullable = false)
    private Instant newestEventTs;

    protected IngestBatch() {
    }

    public IngestBatch(
            Long userId,
            String idempotencyKey,
            int accepted,
            int duplicated,
            long wearSeconds,
            Instant watermarkTs,
            Long deviceId,
            Instant oldestEventTs,
            Instant newestEventTs
    ) {
        this.userId = userId;
        this.idempotencyKey = idempotencyKey;
        this.accepted = accepted;
        this.duplicated = duplicated;
        this.wearSeconds = wearSeconds;
        this.watermarkTs = watermarkTs;
        this.deviceId = deviceId;
        this.oldestEventTs = oldestEventTs;
        this.newestEventTs = newestEventTs;
    }

    public int getAccepted() {
        return accepted;
    }

    public int getDuplicated() {
        return duplicated;
    }

    public Instant getWatermarkTs() {
        return watermarkTs;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public Long getWearSeconds() {
        return wearSeconds;
    }

    public Instant getOldestEventTs() {
        return oldestEventTs;
    }

    public Instant getNewestEventTs() {
        return newestEventTs;
    }
}
