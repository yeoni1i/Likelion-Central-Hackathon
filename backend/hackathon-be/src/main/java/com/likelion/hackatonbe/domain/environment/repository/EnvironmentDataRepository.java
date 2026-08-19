package com.likelion.hackatonbe.domain.environment.repository;

import com.likelion.hackatonbe.domain.environment.entity.EnvironmentData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EnvironmentDataRepository extends JpaRepository<EnvironmentData, Long> {
    Optional<EnvironmentData> findTopByChildIdOrderByRecordedAtDesc(Long childId);

    void deleteByRecordedAtBefore(LocalDateTime threshold);
}
