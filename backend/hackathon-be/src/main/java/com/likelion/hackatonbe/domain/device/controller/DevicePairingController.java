package com.likelion.hackatonbe.domain.device.controller;

import com.likelion.hackatonbe.domain.device.dto.PairDeviceRequest;
import com.likelion.hackatonbe.domain.device.dto.PairDeviceResponse;
import com.likelion.hackatonbe.domain.device.dto.PairingCodeResponse;
import com.likelion.hackatonbe.domain.device.service.DevicePairingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.likelion.hackatonbe.domain.device.dto.PairingStatusResponse;


@RestController
@RequestMapping("/devices")
public class DevicePairingController {

    private final DevicePairingService devicePairingService;

    public DevicePairingController(
            DevicePairingService devicePairingService
    ) {
        this.devicePairingService = devicePairingService;
    }

    @PostMapping("/pairing-codes")
    public ResponseEntity<PairingCodeResponse> createPairingCode(
            @RequestParam Long childId
    ) {
        return ResponseEntity.ok(
                devicePairingService.createPairingCode(childId)
        );
    }

    @PostMapping("/pair")
    public ResponseEntity<PairDeviceResponse> pairDevice(
            @RequestBody PairDeviceRequest request
    ) {
        return ResponseEntity.ok(
                devicePairingService.pairDevice(request)
        );
    }

    @GetMapping("/pairing-codes/{code}/status")
    public ResponseEntity<PairingStatusResponse> getPairingStatus(
            @PathVariable String code
    ) {
        return ResponseEntity.ok(
                devicePairingService.getPairingStatus(code)
        );
    }
}