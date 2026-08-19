package com.likelion.hackatonbe.domain.device.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_pairing_codes")
public class PairingCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 6)
    private String code;

    @Column(name = "child_id", nullable = false)
    private Long childId;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PairingStatus status;

    @Column(name = "device_id")
    private Long deviceId;

    protected PairingCode() {
    }

    public PairingCode(
            String code,
            Long childId,
            LocalDateTime expiresAt
    ) {
        this.code = code;
        this.childId = childId;
        this.expiresAt = expiresAt;
        this.status = PairingStatus.WAITING;
    }

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(expiresAt);
    }

    public boolean isUsed() {
        return status == PairingStatus.USED;
    }

    public void expire() {
        this.status = PairingStatus.EXPIRED;
    }

    public void use(Long deviceId) {
        this.status = PairingStatus.USED;
        this.deviceId = deviceId;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Long getChildId() {
        return childId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public PairingStatus getStatus() {
        return status;
    }

    public Long getDeviceId() { return deviceId; }
}
