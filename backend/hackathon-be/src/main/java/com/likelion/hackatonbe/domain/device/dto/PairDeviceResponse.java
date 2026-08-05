package com.likelion.hackatonbe.domain.device.dto;

public record PairDeviceResponse(
        boolean success,
        String deviceId,
        String message
) {
}