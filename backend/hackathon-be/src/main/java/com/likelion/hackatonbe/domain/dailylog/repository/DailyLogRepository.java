package com.likelion.hackatonbe.domain.dailylog.repository;

import com.likelion.hackatonbe.domain.dailylog.entity.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {

    // 하루 일지 조회
    List<DailyLog> findByChildIdAndDate(
            Long childId,
            LocalDate date
    );

    // 기간 내 일지 조회 (AI 주간 분석용)
    List<DailyLog> findByChildIdAndDateBetweenOrderByDateAsc(
            Long childId,
            LocalDate startDate,
            LocalDate endDate
    );
}