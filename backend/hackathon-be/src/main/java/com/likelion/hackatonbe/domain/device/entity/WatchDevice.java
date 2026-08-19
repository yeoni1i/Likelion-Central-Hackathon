package com.likelion.hackatonbe.domain.device.entity;

import com.likelion.hackatonbe.domain.user.entity.Child;
import jakarta.persistence.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "watch_devices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_watch_device_identifier",
                        columnNames = "device_identifier"
                )
        }
)
public class WatchDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_identifier", nullable = false)
    private String deviceIdentifier;

    @Column(nullable = false)
    private String deviceName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "child_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_watch_device_child")
    )
    private Child child;

    @Column(nullable = false)
    private LocalDateTime pairedAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "detection_status", nullable = false, length = 20)
    private DetectionStatus detectionStatus = DetectionStatus.STOP;

    @Column(name = "current_intensity")
    private Integer currentIntensity;

    @Column(name = "last_scratch_at")
    private Instant lastScratchAt;

    protected WatchDevice() {
    }

    public WatchDevice(
            String deviceIdentifier,
            String deviceName,
            Child child,
            LocalDateTime pairedAt
    ) {
        this.deviceIdentifier = deviceIdentifier;
        this.deviceName = deviceName;
        this.child = child;
        this.pairedAt = pairedAt;
    }

    public void reconnect(
            Child child,
            String deviceName,
            LocalDateTime pairedAt
    ) {
        this.child = child;
        this.deviceName = deviceName;
        this.pairedAt = pairedAt;
    }

    public void startDetection() {
        this.detectionStatus = DetectionStatus.START;
        this.currentIntensity = null;
        this.lastScratchAt = null;
    }

    public void stopDetection() {
        this.detectionStatus = DetectionStatus.STOP;
        this.currentIntensity = null;
    }

    public void updateScratchState(Integer intensity, Instant scratchAt) {
        this.currentIntensity = intensity;
        this.lastScratchAt = scratchAt;
    }

    public DetectionStatus getDetectionStatus() {
        return detectionStatus;
    }

    public Integer getCurrentIntensity() {
        return currentIntensity;
    }

    public Instant getLastScratchAt() {
        return lastScratchAt;
    }

    public Long getId() {
        return id;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public Child getChild() {
        return child;
    }

    public LocalDateTime getPairedAt() {
        return pairedAt;
    }
}