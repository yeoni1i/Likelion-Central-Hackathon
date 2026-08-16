package com.likelion.hackatonbe.domain.analysis.service;

import com.likelion.hackatonbe.domain.dailylog.dto.DailyLogDto;
import com.likelion.hackatonbe.domain.dailylog.service.DailyLogService;
import com.likelion.hackatonbe.domain.scratch.dto.DailyScratchResponse;
import com.likelion.hackatonbe.domain.scratch.service.DailyScratchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.likelion.hackatonbe.domain.environment.entity.EnvironmentData;
import com.likelion.hackatonbe.domain.environment.repository.EnvironmentDataRepository;
import com.likelion.hackatonbe.domain.user.entity.Child;
import com.likelion.hackatonbe.domain.user.repository.ChildRepository;
import com.likelion.hackatonbe.global.error.BusinessException;
import com.likelion.hackatonbe.global.error.ErrorCode;

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
    private final ChildRepository childRepository;
    private final EnvironmentDataRepository environmentDataRepository;

    public String generateDailyReport(
            Long userId,
            LocalDate date,
            ZoneId zoneId
    ) {

        // 1. 해당 사용자의 아이 조회
        Child child = childRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 해당 날짜의 일상 기록 조회
        List<DailyLogDto.Response> dailyLogs =
                dailyLogService.getDailyLogsByDate(userId, date);

        // 3. 해당 날짜의 긁음 기록 조회
        DailyScratchResponse scratch =
                dailyScratchService.getDaily(userId, date, zoneId);

        // 4. 해당 날짜의 환경 정보 조회
        EnvironmentData environment =
                environmentDataRepository
                        .findByChildIdAndDate(child.getId(), date)
                        .orElse(null);

        // 5. 식단 정보 정리
        String meals = buildMealsText(dailyLogs);

        // 6. 일상 특이사항 정리
        String dailyNotes = buildDailyNotesText(dailyLogs);

        // 7. 환경 정보
        Double temperature =
                environment != null ? environment.getTemperature() : null;

        Integer humidity =
                environment != null ? environment.getHumidity() : null;

        String airQuality =
                environment != null ? environment.getAirQuality() : null;

        // 8. OpenAI 분석 요청
        return openAIService.analyze(
                Math.toIntExact(scratch.eventCount()),
                scratch.totalSeconds().doubleValue(),
                meals,
                dailyNotes,
                temperature,
                humidity,
                airQuality
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