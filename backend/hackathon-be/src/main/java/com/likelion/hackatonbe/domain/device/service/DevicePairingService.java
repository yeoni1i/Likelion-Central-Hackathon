package com.likelion.hackatonbe.domain.device.service;

import com.likelion.hackatonbe.domain.device.dto.PairDeviceRequest;
import com.likelion.hackatonbe.domain.device.dto.PairDeviceResponse;
import com.likelion.hackatonbe.domain.device.dto.PairingCodeResponse;
import com.likelion.hackatonbe.domain.device.entity.PairingCode;
import com.likelion.hackatonbe.domain.device.entity.WatchDevice;
import com.likelion.hackatonbe.domain.device.repository.PairingCodeRepository;
import com.likelion.hackatonbe.domain.device.repository.WatchDeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@Transactional
public class DevicePairingService {

    private static final int CODE_BOUND = 1_000_000;
    private static final int CODE_EXPIRE_MINUTES = 5;

    private final PairingCodeRepository pairingCodeRepository;
    private final WatchDeviceRepository watchDeviceRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public DevicePairingService(
            PairingCodeRepository pairingCodeRepository,
            WatchDeviceRepository watchDeviceRepository
    ) {
        this.pairingCodeRepository = pairingCodeRepository;
        this.watchDeviceRepository = watchDeviceRepository;
    }

    public PairingCodeResponse createPairingCode(Long parentUserId) {
        if (parentUserId == null || parentUserId <= 0) {
            throw new IllegalArgumentException(
                    "parentUserId는 1 이상의 값이어야 합니다."
            );
        }

        String code = generateUniqueCode();
        LocalDateTime expiresAt =
                LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES);

        PairingCode pairingCode = new PairingCode(
                code,
                parentUserId,
                expiresAt
        );

        pairingCodeRepository.save(pairingCode);

        return new PairingCodeResponse(code, expiresAt);
    }

    public PairDeviceResponse pairDevice(PairDeviceRequest request) {
        validatePairRequest(request);

        PairingCode pairingCode = pairingCodeRepository
                .findByCode(request.pairingCode())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "유효하지 않은 등록 코드입니다."
                        )
                );

        LocalDateTime now = LocalDateTime.now();

        if (pairingCode.isExpired(now)) {
            pairingCode.expire();

            throw new IllegalStateException(
                    "등록 코드가 만료되었습니다."
            );
        }

        if (pairingCode.isUsed()) {
            throw new IllegalStateException(
                    "이미 사용된 등록 코드입니다."
            );
        }

        WatchDevice watchDevice = watchDeviceRepository
                .findByDeviceIdentifier(request.deviceId())
                .map(existing -> {
                    existing.reconnect(
                            pairingCode.getParentUserId(),
                            request.deviceName(),
                            now
                    );
                    return existing;
                })
                .orElseGet(() -> new WatchDevice(
                        request.deviceId(),
                        request.deviceName(),
                        pairingCode.getParentUserId(),
                        now
                ));

        watchDeviceRepository.save(watchDevice);
        pairingCode.use(now);

        return new PairDeviceResponse(
                true,
                watchDevice.getDeviceIdentifier(),
                "워치가 등록되었습니다."
        );
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < 20; attempt++) {
            String code = String.format(
                    "%06d",
                    secureRandom.nextInt(CODE_BOUND)
            );

            if (!pairingCodeRepository.existsByCode(code)) {
                return code;
            }
        }

        throw new IllegalStateException(
                "등록 코드 생성에 실패했습니다. 다시 시도해주세요."
        );
    }

    private void validatePairRequest(PairDeviceRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "요청 본문이 필요합니다."
            );
        }

        if (request.pairingCode() == null
                || !request.pairingCode().matches("\\d{6}")) {
            throw new IllegalArgumentException(
                    "등록 코드는 숫자 6자리여야 합니다."
            );
        }

        if (request.deviceId() == null
                || request.deviceId().isBlank()) {
            throw new IllegalArgumentException(
                    "deviceId는 필수입니다."
            );
        }

        if (request.deviceName() == null
                || request.deviceName().isBlank()) {
            throw new IllegalArgumentException(
                    "deviceName은 필수입니다."
            );
        }
    }
}