package com.likelion.hackatonbe.domain.device.dto;

public record PairingStatusResponse(
        boolean paired,
        Long deviceId
) {
}