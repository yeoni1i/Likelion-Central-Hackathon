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

    @Column(nullable = false)
    private Long parentUserId;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PairingStatus status;

    protected PairingCode() {
    }

    public PairingCode(
            String code,
            Long parentUserId,
            LocalDateTime expiresAt
    ) {
        this.code = code;
        this.parentUserId = parentUserId;
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

    public void use(LocalDateTime usedAt) {
        this.status = PairingStatus.USED;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Long getParentUserId() {
        return parentUserId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public PairingStatus getStatus() {
        return status;
    }
}

