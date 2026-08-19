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
import com.likelion.hackatonbe.domain.analysis.dto.DailyAnalysisResponse;
import com.likelion.hackatonbe.domain.analysis.dto.WeeklyAnalysisResponse;
import com.likelion.hackatonbe.domain.dailylog.entity.DailyLog;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final DailyAnalysisService dailyAnalysisService;
    private final DailyLogService dailyLogService;
    private final DailyScratchService dailyScratchService;
    private final OpenAIService openAIService;
    private final ChildRepository childRepository;
    private final EnvironmentDataRepository environmentDataRepository;
    private final WeeklyDataService weeklyDataService;

    @Transactional(readOnly = true)
    public String generateDailyReport(
            Long userId,
            LocalDate date,
            ZoneId zoneId
    ) {

        // 해당 사용자의 아이 조회
        Child child = childRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // AI 주간 분석용 최근 7일 일지 조회
        List<DailyLog> weeklyLogs =
                weeklyDataService.getWeeklyDailyLogs(
                        child.getId(),
                        date
                );

        // 해당 날짜의 일상 기록 조회
        List<DailyLogDto.Response> dailyLogs =
                dailyLogService.getDailyLogsByDate(userId, date);

        // 해당 날짜의 긁음 기록 조회
        DailyScratchResponse scratch =
                dailyScratchService.getDaily(userId, date, zoneId);

        // 시간대별 긁음 분석
        DailyAnalysisResponse dailyAnalysis =
                dailyAnalysisService.getDailyAnalysis(
                        userId,
                        date,
                        zoneId
                );

        // 최근 7일 긁음 분석
        WeeklyAnalysisResponse weeklyAnalysis =
                dailyAnalysisService.getWeeklyAnalysis(
                        userId,
                        date,
                        zoneId
                );

        // 최근 환경 정보 조회
        EnvironmentData environment =
                environmentDataRepository
                        .findTopByChildIdOrderByRecordedAtDesc(child.getId())
                        .orElse(null);

        // 식단 정보 정리
        String meals = buildMealsText(dailyLogs);

        // 일상 특이사항 정리
        String dailyNotes = buildDailyNotesText(dailyLogs);

        // 7. 환경 정보
        Double temperature =
                environment != null ? environment.getTemperature() : null;

        Integer humidity =
                environment != null ? environment.getHumidity() : null;

        String airQuality =
                environment != null ? environment.getAirQuality() : null;

        String hourlyScratchText =
                buildHourlyScratchText(dailyAnalysis);

        String weeklyScratchText =
                buildWeeklyScratchText(weeklyAnalysis);

        // 최근 7일 식단 정보 정리
        String weeklyMealsText =
                buildWeeklyMealsText(weeklyLogs);

        // OpenAI 분석 요청
        return openAIService.analyze(
                Math.toIntExact(scratch.eventCount()),
                scratch.totalSeconds().doubleValue(),
                hourlyScratchText,
                weeklyScratchText,
                meals,
                weeklyMealsText,
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

    private String buildHourlyScratchText(
            DailyAnalysisResponse analysis
    ) {

        if (analysis == null || analysis.scratchCount() == 0) {
            return "긁음 기록 없음";
        }

        String hourlyText = analysis.hourly().stream()
                .filter(item -> item.count() > 0)
                .map(item ->
                        String.format(
                                "%02d시: %d회",
                                item.hour(),
                                item.count()
                        )
                )
                .collect(Collectors.joining("\n"));

        String peakText =
                analysis.peakHour() != null
                        ? String.format(
                        "가장 많이 긁은 시간대: %02d시",
                        analysis.peakHour()
                )
                        : "가장 많이 긁은 시간대: 없음";

        return peakText + "\n" + hourlyText;
    }

    private String buildWeeklyScratchText(
            WeeklyAnalysisResponse analysis
    ) {

        if (analysis == null) {
            return "주간 기록 없음";
        }

        String dailyText = analysis.daily().stream()
                .map(item ->
                        String.format(
                                "%s: %d회",
                                item.date(),
                                item.count()
                        )
                )
                .collect(Collectors.joining("\n"));

        return String.format(
                """
                분석 기간: %s ~ %s
                최근 7일 총 긁음: %d회
                최근 7일 일평균: %.2f회
    
                날짜별 기록:
                %s
                """,
                analysis.startDate(),
                analysis.endDate(),
                analysis.totalCount(),
                analysis.dailyAverage(),
                dailyText
        );
    }

    private String buildWeeklyMealsText(List<DailyLog> weeklyLogs) {

        if (weeklyLogs == null || weeklyLogs.isEmpty()) {
            return "주간 식단 기록 없음";
        }

        return weeklyLogs.stream()
                .filter(log ->
                        log.getFoods() != null &&
                                !log.getFoods().isEmpty()
                )
                .map(log ->
                        log.getDate()
                                + " | "
                                + log.getMealType()
                                + " | "
                                + log.getFoods()
                )
                .collect(Collectors.joining("\n"));
    }


}