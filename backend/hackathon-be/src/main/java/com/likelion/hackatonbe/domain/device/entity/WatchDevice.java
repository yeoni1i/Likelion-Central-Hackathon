package com.likelion.hackatonbe.domain.device.entity;

import jakarta.persistence.*;

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

    @Column(nullable = false)
    private Long parentUserId;

    @Column(nullable = false)
    private LocalDateTime pairedAt;

    @Column(nullable = false)
    private boolean active;

    protected WatchDevice() {
    }

    public WatchDevice(
            String deviceIdentifier,
            String deviceName,
            Long parentUserId,
            LocalDateTime pairedAt
    ) {
        this.deviceIdentifier = deviceIdentifier;
        this.deviceName = deviceName;
        this.parentUserId = parentUserId;
        this.pairedAt = pairedAt;
        this.active = true;
    }

    public void reconnect(
            Long parentUserId,
            String deviceName,
            LocalDateTime pairedAt
    ) {
        this.parentUserId = parentUserId;
        this.deviceName = deviceName;
        this.pairedAt = pairedAt;
        this.active = true;
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

    public Long getParentUserId() {
        return parentUserId;
    }

    public LocalDateTime getPairedAt() {
        return pairedAt;
    }

    public boolean isActive() {
        return active;
    }
}
