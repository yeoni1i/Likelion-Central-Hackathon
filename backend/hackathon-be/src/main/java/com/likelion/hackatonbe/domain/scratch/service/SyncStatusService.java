package com.likelion.hackatonbe.domain.scratch.service;

import com.likelion.hackatonbe.domain.scratch.dto.SyncStatusResponse;
import com.likelion.hackatonbe.domain.scratch.entity.IngestBatch;
import com.likelion.hackatonbe.domain.scratch.repository.IngestBatchRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SyncStatusService {

    private final IngestBatchRepository batchRepository;

    public SyncStatusService(IngestBatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    @Transactional(readOnly = true)
    public SyncStatusResponse get(Long userId, Long deviceId) {
        List<IngestBatch> batches =
                batchRepository.findAllByUserIdAndDeviceIdOrderByWatermarkTsDesc(userId, deviceId);
        if (batches.isEmpty()) {
            return new SyncStatusResponse(
                    deviceId,
                    null,
                    null,
                    null,
                    72,
                    "202 응답 수신 후 해당 배치만 로컬 삭제"
            );
        }

        IngestBatch latest = batches.get(0);
        return new SyncStatusResponse(
                deviceId,
                latest.getWatermarkTs(),
                batches.stream().map(IngestBatch::getOldestEventTs).min(java.time.Instant::compareTo).orElse(null),
                batches.stream().map(IngestBatch::getNewestEventTs).max(java.time.Instant::compareTo).orElse(null),
                72,
                "202 응답 수신 후 해당 배치만 로컬 삭제"
        );
    }
}
