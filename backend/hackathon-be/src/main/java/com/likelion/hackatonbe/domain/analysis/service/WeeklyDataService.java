package com.likelion.hackatonbe.domain.analysis.service;

import com.likelion.hackatonbe.domain.dailylog.entity.DailyLog;
import com.likelion.hackatonbe.domain.dailylog.repository.DailyLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeeklyDataService {

    private final DailyLogRepository dailyLogRepository;

    @Transactional(readOnly = true)
    public List<DailyLog> getWeeklyDailyLogs(
            Long childId,
            LocalDate endDate
    ) {
        LocalDate startDate = endDate.minusDays(6);

        return dailyLogRepository
                .findByChildIdAndDateBetweenOrderByDateAsc(
                        childId,
                        startDate,
                        endDate
                );
    }
}