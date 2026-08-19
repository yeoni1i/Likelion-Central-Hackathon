package com.likelion.hackatonbe.domain.device.service;

import com.likelion.hackatonbe.domain.device.dto.CurrentDetectionResponse;
import com.likelion.hackatonbe.domain.device.dto.DetectionStatusResponse;
import com.likelion.hackatonbe.domain.device.entity.WatchDevice;
import com.likelion.hackatonbe.domain.device.repository.WatchDeviceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@Transactional
public class DeviceDetectionService {

    private static final long SCRATCH_ACTIVE_SECONDS = 10;

    private final WatchDeviceRepository watchDeviceRepository;

    public DeviceDetectionService(
            WatchDeviceRepository watchDeviceRepository
    ) {
        this.watchDeviceRepository = watchDeviceRepository;
    }

    public void start(Long deviceId) {
        WatchDevice device = getDevice(deviceId);
        device.startDetection();
    }

    public void stop(Long deviceId) {
        WatchDevice device = getDevice(deviceId);
        device.stopDetection();
    }

    public DetectionStatusResponse getStatus(Long deviceId) {
        WatchDevice device = getDevice(deviceId);

        return new DetectionStatusResponse(
                device.getId(),
                device.getDetectionStatus().name()
        );
    }

    public CurrentDetectionResponse getCurrent(Long deviceId) {
        WatchDevice device = getDevice(deviceId);

        Integer intensity = device.getCurrentIntensity();
        Instant lastScratchAt = device.getLastScratchAt();

        String scratchStatus = determineScratchStatus(
                intensity,
                lastScratchAt
        );

        return new CurrentDetectionResponse(
                device.getId(),
                device.getDetectionStatus().name(),
                scratchStatus,
                scratchStatus.equals("STABLE") ? null : intensity,
                lastScratchAt
        );
    }

    public void updateScratch(
            Long deviceId,
            Integer intensity,
            Instant scratchAt
    ) {
        WatchDevice device = getDevice(deviceId);

        if (device.getDetectionStatus()
                != com.likelion.hackatonbe.domain.device.entity.DetectionStatus.START) {
            return;
        }

        device.updateScratchState(intensity, scratchAt);
    }

    private String determineScratchStatus(
            Integer intensity,
            Instant lastScratchAt
    ) {
        if (intensity == null || lastScratchAt == null) {
            return "STABLE";
        }

        long seconds =
                Duration.between(lastScratchAt, Instant.now()).getSeconds();

        if (seconds > SCRATCH_ACTIVE_SECONDS) {
            return "STABLE";
        }

        return switch (intensity) {
            case 1 -> "NORMAL";
            case 2 -> "NORMAL";
            case 3 -> "WARNING";
            case 4 -> "DANGER";
            default -> "STABLE";
        };
    }

    private WatchDevice getDevice(Long deviceId) {
        return watchDeviceRepository.findById(deviceId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "등록되지 않은 워치입니다. deviceId=" + deviceId
                        )
                );
    }
}