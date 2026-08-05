package com.likelion.hackatonbe.domain.scratch.repository;

import com.likelion.hackatonbe.domain.scratch.entity.IngestBatch;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngestBatchRepository extends JpaRepository<IngestBatch, Long> {

    Optional<IngestBatch> findByUserIdAndIdempotencyKey(Long userId, String idempotencyKey);

    List<IngestBatch> findAllByUserIdAndDeviceIdOrderByWatermarkTsDesc(Long userId, Long deviceId);

    List<IngestBatch> findAllByUserIdAndWatermarkTsGreaterThanEqualAndWatermarkTsLessThan(
            Long userId,
            java.time.Instant from,
            java.time.Instant to
    );
}
