package com.likelion.hackatonbe.domain.analysis.service;

import com.likelion.hackatonbe.domain.analysis.dto.DailyAnalysisResponse;
import com.likelion.hackatonbe.domain.analysis.dto.HourlyScratchDto;
import com.likelion.hackatonbe.domain.scratch.entity.ScratchEvent;
import com.likelion.hackatonbe.domain.scratch.repository.ScratchEventRepository;
import org.springframework.stereotype.Service;
import com.likelion.hackatonbe.domain.analysis.dto.DailyScratchCountDto;
import com.likelion.hackatonbe.domain.analysis.dto.WeeklyAnalysisResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


//주간 긁음 데이터 생성-시간대별 막대 그래프
@Service
public class DailyAnalysisService {

    private final ScratchEventRepository scratchEventRepository;

    public DailyAnalysisService(
            ScratchEventRepository scratchEventRepository
    ) {
        this.scratchEventRepository = scratchEventRepository;
    }

    public DailyAnalysisResponse getDailyAnalysis(
            Long userId,
            LocalDate date,
            ZoneId zoneId
    ) {

        // 해당 날짜의 시작 시각
        Instant from = date
                .atStartOfDay(zoneId)
                .toInstant();

        // 다음 날 00:00
        Instant to = date
                .plusDays(1)
                .atStartOfDay(zoneId)
                .toInstant();

        // 해당 사용자의 하루 긁음 기록 조회
        List<ScratchEvent> events =
                scratchEventRepository
                        .findAllByUserIdAndStartTsGreaterThanEqualAndStartTsLessThan(
                                userId,
                                from,
                                to
                        );

        // 0시 ~ 23시
        long[] hourlyCounts = new long[24];

        for (ScratchEvent event : events) {

            int hour = event
                    .getStartTs()
                    .atZone(zoneId)
                    .getHour();

            hourlyCounts[hour]++;
        }

        List<HourlyScratchDto> hourly = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {

            String label = String.format("%02d:00", hour);

            hourly.add(
                    new HourlyScratchDto(
                            hour,
                            hourlyCounts[hour]
                    )
            );
        }

        Integer peakHour = null;

        if (!events.isEmpty()) {

            int maxHour = 0;

            for (int hour = 1; hour < 24; hour++) {

                if (hourlyCounts[hour] > hourlyCounts[maxHour]) {
                    maxHour = hour;
                }
            }

            peakHour = maxHour;
        }

        return new DailyAnalysisResponse(
                date,
                events.size(),
                peakHour,
                hourly

        );
    }

    public WeeklyAnalysisResponse getWeeklyAnalysis(
            Long userId,
            LocalDate endDate,
            ZoneId zoneId
    ) {

        LocalDate startDate = endDate.minusDays(6);

        Instant from = startDate
                .atStartOfDay(zoneId)
                .toInstant();

        Instant to = endDate
                .plusDays(1)
                .atStartOfDay(zoneId)
                .toInstant();

        List<ScratchEvent> events =
                scratchEventRepository
                        .findAllByUserIdAndStartTsGreaterThanEqualAndStartTsLessThan(
                                userId,
                                from,
                                to
                        );

        long[] dailyCounts = new long[7];

        for (ScratchEvent event : events) {

            LocalDate eventDate = event
                    .getStartTs()
                    .atZone(zoneId)
                    .toLocalDate();

            int dayIndex = (int) java.time.temporal.ChronoUnit.DAYS
                    .between(startDate, eventDate);

            if (dayIndex >= 0 && dayIndex < 7) {
                dailyCounts[dayIndex]++;
            }
        }

        List<DailyScratchCountDto> daily = new ArrayList<>();

        for (int i = 0; i < 7; i++) {

            LocalDate currentDate = startDate.plusDays(i);

            daily.add(
                    new DailyScratchCountDto(
                            currentDate,
                            dailyCounts[i]
                    )
            );
        }

        long totalCount = events.size();

        double dailyAverage = Math.round(
                (totalCount / 7.0) * 100.0
        ) / 100.0;

        return new WeeklyAnalysisResponse(
                startDate,
                endDate,
                totalCount,
                dailyAverage,
                daily
        );
    }
}

