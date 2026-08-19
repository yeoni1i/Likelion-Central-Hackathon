package com.likelion.hackatonbe.domain.device.controller;

import com.likelion.hackatonbe.domain.device.dto.CurrentDetectionResponse;
import com.likelion.hackatonbe.domain.device.dto.DetectionStatusResponse;
import com.likelion.hackatonbe.domain.device.service.DeviceDetectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/devices")
public class DeviceDetectionController {

    private final DeviceDetectionService deviceDetectionService;

    public DeviceDetectionController(
            DeviceDetectionService deviceDetectionService
    ) {
        this.deviceDetectionService = deviceDetectionService;
    }

    @PostMapping("/{deviceId}/detection/start")
    public ResponseEntity<Void> start(
            @PathVariable Long deviceId
    ) {
        deviceDetectionService.start(deviceId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{deviceId}/detection/stop")
    public ResponseEntity<Void> stop(
            @PathVariable Long deviceId
    ) {
        deviceDetectionService.stop(deviceId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{deviceId}/detection/status")
    public DetectionStatusResponse getStatus(
            @PathVariable Long deviceId
    ) {
        return deviceDetectionService.getStatus(deviceId);
    }

    @GetMapping("/{deviceId}/detection/current")
    public CurrentDetectionResponse getCurrent(
            @PathVariable Long deviceId
    ) {
        return deviceDetectionService.getCurrent(deviceId);
    }
}