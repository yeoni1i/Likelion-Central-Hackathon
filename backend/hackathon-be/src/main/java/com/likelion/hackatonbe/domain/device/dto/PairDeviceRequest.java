package com.likelion.hackatonbe.domain.device.dto;

public record PairDeviceRequest(
        String pairingCode,
        String deviceId,
        String deviceName
) {
}