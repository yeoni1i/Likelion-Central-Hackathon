package com.likelion.hackatonbe.domain.device.service;

import com.likelion.hackatonbe.domain.device.dto.PairDeviceRequest;
import com.likelion.hackatonbe.domain.device.dto.PairDeviceResponse;
import com.likelion.hackatonbe.domain.device.dto.PairingCodeResponse;
import com.likelion.hackatonbe.domain.device.entity.PairingCode;
import com.likelion.hackatonbe.domain.device.entity.WatchDevice;
import com.likelion.hackatonbe.domain.device.repository.PairingCodeRepository;
import com.likelion.hackatonbe.domain.device.repository.WatchDeviceRepository;
import com.likelion.hackatonbe.domain.user.entity.Child;
import com.likelion.hackatonbe.domain.user.repository.ChildRepository;
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
    private final ChildRepository childRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public DevicePairingService(
            PairingCodeRepository pairingCodeRepository,
            WatchDeviceRepository watchDeviceRepository,
            ChildRepository childRepository
    ) {
        this.pairingCodeRepository = pairingCodeRepository;
        this.watchDeviceRepository = watchDeviceRepository;
        this.childRepository = childRepository;
    }

    /**
     * 특정 아이의 워치를 등록하기 위한 6자리 페어링 코드 생성
     */
    public PairingCodeResponse createPairingCode(Long childId) {

        if (childId == null || childId <= 0) {
            throw new IllegalArgumentException(
                    "childId는 1 이상의 값이어야 합니다."
            );
        }

        // 실제 존재하는 아이인지 확인
        childRepository.findById(childId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "아이 정보를 찾을 수 없습니다."
                        )
                );

        String code = generateUniqueCode();

        LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plusMinutes(CODE_EXPIRE_MINUTES);

        PairingCode pairingCode = new PairingCode(
                code,
                childId,
                expiresAt
        );

        pairingCodeRepository.save(pairingCode);

        return new PairingCodeResponse(
                code,
                expiresAt
        );
    }

    /**
     * 워치에서 6자리 코드를 입력했을 때 실제 Child와 WatchDevice 연결
     */
    public PairDeviceResponse pairDevice(
            PairDeviceRequest request
    ) {

        validatePairRequest(request);

        PairingCode pairingCode =
                pairingCodeRepository
                        .findByCode(request.pairingCode())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "유효하지 않은 등록 코드입니다."
                                )
                        );

        LocalDateTime now =
                LocalDateTime.now();

        // 만료 여부 확인
        if (pairingCode.isExpired(now)) {

            pairingCode.expire();

            throw new IllegalStateException(
                    "등록 코드가 만료되었습니다."
            );
        }

        // 이미 사용한 코드인지 확인
        if (pairingCode.isUsed()) {

            throw new IllegalStateException(
                    "이미 사용된 등록 코드입니다."
            );
        }

        /*
         * PairingCode에 저장된 childId를 이용해
         * 실제 Child Entity 조회
         */
        Child child =
                childRepository
                        .findById(pairingCode.getChildId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "아이 정보를 찾을 수 없습니다."
                                )
                        );

        /*
         * 같은 deviceIdentifier가 이미 존재하면
         * 기존 워치를 해당 Child에게 다시 연결
         *
         * 없으면 새 WatchDevice 생성
         */
        WatchDevice watchDevice =
                watchDeviceRepository
                        .findByDeviceIdentifier(
                                request.deviceId()
                        )
                        .map(existing -> {

                            existing.reconnect(
                                    child,
                                    request.deviceName(),
                                    now
                            );

                            return existing;
                        })
                        .orElseGet(() ->
                                new WatchDevice(
                                        request.deviceId(),
                                        request.deviceName(),
                                        child,
                                        now
                                )
                        );

        watchDeviceRepository.save(watchDevice);

        // 페어링 코드 사용 처리
        pairingCode.use(now);

        return new PairDeviceResponse(
                true,
                watchDevice.getId(),
                "워치가 등록되었습니다."
        );
    }

    /**
     * 중복되지 않는 6자리 코드 생성
     */
    private String generateUniqueCode() {

        for (int attempt = 0; attempt < 20; attempt++) {

            String code =
                    String.format(
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

    /**
     * 워치 페어링 요청값 검증
     */
    private void validatePairRequest(
            PairDeviceRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "요청 본문이 필요합니다."
            );
        }

        if (request.pairingCode() == null
                || !request.pairingCode()
                .matches("\\d{6}")) {

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