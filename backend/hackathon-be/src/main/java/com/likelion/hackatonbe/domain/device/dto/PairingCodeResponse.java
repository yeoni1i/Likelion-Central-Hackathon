package com.likelion.hackatonbe.domain.device.dto;

import java.time.LocalDateTime;

public record PairingCodeResponse(
        String pairingCode,
        LocalDateTime expiresAt
) {
}