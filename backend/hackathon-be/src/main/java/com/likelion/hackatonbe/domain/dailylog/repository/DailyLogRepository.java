package com.likelion.hackatonbe.domain.dailylog.repository;

import com.likelion.hackatonbe.domain.dailylog.entity.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    List<DailyLog> findByChildIdAndDate(Long childId, LocalDate date);
}