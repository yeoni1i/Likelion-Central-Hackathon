package com.likelion.hackatonbe.domain.analysis.service;

import com.likelion.hackatonbe.domain.dailylog.dto.DailyLogDto;
import com.likelion.hackatonbe.domain.dailylog.service.DailyLogService;
import com.likelion.hackatonbe.domain.scratch.dto.DailyScratchResponse;
import com.likelion.hackatonbe.domain.scratch.service.DailyScratchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final DailyLogService dailyLogService;
    private final DailyScratchService dailyScratchService;
    private final OpenAIService openAIService;

    public String generateDailyReport(
            Long userId,
            LocalDate date,
            ZoneId zoneId
    ) {

        // 1. 해당 날짜의 일상 기록 조회
        List<DailyLogDto.Response> dailyLogs =
                dailyLogService.getDailyLogsByDate(userId, date);

        // 2. 해당 날짜의 긁음 기록 조회
        DailyScratchResponse scratch =
                dailyScratchService.getDaily(userId, date, zoneId);

        // 3. 식단 정보 문자열로 정리
        String meals = buildMealsText(dailyLogs);

        // 4. 일상 특이사항 문자열로 정리
        String dailyNotes = buildDailyNotesText(dailyLogs);

        // 5. OpenAI 분석 요청
        return openAIService.analyze(
                Math.toIntExact(scratch.eventCount()),
                scratch.totalSeconds().doubleValue(),
                meals,
                dailyNotes
        );
    }

    private String buildMealsText(List<DailyLogDto.Response> dailyLogs) {

        if (dailyLogs.isEmpty()) {
            return "기록 없음";
        }

        String result = dailyLogs.stream()
                .filter(log -> log.getFoods() != null)
                .filter(log -> !log.getFoods().isEmpty())
                .map(log -> log.getMealType() + ": " + log.getFoods())
                .collect(Collectors.joining("\n"));

        return result.isBlank()
                ? "기록 없음"
                : result;
    }

    private String buildDailyNotesText(List<DailyLogDto.Response> dailyLogs) {

        if (dailyLogs.isEmpty()) {
            return "기록 없음";
        }

        String result = dailyLogs.stream()
                .map(log -> {
                    StringBuilder builder = new StringBuilder();

                    if (log.getSymptoms() != null
                            && !log.getSymptoms().isEmpty()) {
                        builder.append("증상: ")
                                .append(log.getSymptoms())
                                .append(", ");
                    }

                    if (log.getMemo() != null
                            && !log.getMemo().isBlank()) {
                        builder.append("메모: ")
                                .append(log.getMemo())
                                .append(", ");
                    }

                    if (log.getShowerCount() != null) {
                        builder.append("샤워: ")
                                .append(log.getShowerCount())
                                .append("회, ");
                    }

                    if (log.getMoisturizerCount() != null) {
                        builder.append("보습제 사용: ")
                                .append(log.getMoisturizerCount())
                                .append("회");
                    }

                    return builder.toString()
                            .replaceAll(", $", "");
                })
                .filter(text -> !text.isBlank())
                .collect(Collectors.joining("\n"));

        return result.isBlank()
                ? "기록 없음"
                : result;
    }
}