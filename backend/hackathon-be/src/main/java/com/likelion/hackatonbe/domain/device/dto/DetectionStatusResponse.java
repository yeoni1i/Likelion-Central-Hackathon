package com.likelion.hackatonbe.domain.device.dto;

public record DetectionStatusResponse(
        Long deviceId,
        String status
) {
}