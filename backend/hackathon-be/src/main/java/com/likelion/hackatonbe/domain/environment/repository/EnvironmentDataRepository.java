package com.likelion.hackatonbe.domain.environment.repository;

import com.likelion.hackatonbe.domain.environment.entity.EnvironmentData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface EnvironmentDataRepository extends JpaRepository<EnvironmentData, Long> {
    Optional<EnvironmentData> findByChildIdAndDate(Long childId, LocalDate date);
}
