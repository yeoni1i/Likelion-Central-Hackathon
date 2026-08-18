package com.likelion.hackatonbe.domain.device.entity;

import com.likelion.hackatonbe.domain.user.entity.Child;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "child_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_watch_device_child")
    )
    private Child child;

    @Column(nullable = false)
    private LocalDateTime pairedAt;

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