package com.likelion.hackatonbe.domain.analysis.service;

import com.likelion.hackatonbe.domain.analysis.dto.DailyAnalysisResponse;
import com.likelion.hackatonbe.domain.analysis.dto.DailyReportResponse;
import com.likelion.hackatonbe.domain.analysis.dto.EnvironmentSummaryDto;
import com.likelion.hackatonbe.domain.analysis.dto.ReportAiAnalysisDto;
import com.likelion.hackatonbe.domain.analysis.dto.ScratchSummaryDto;
import com.likelion.hackatonbe.domain.analysis.dto.WeeklyAnalysisResponse;
import com.likelion.hackatonbe.domain.dailylog.dto.DailyLogDto;
import com.likelion.hackatonbe.domain.dailylog.entity.DailyLog;
import com.likelion.hackatonbe.domain.dailylog.service.DailyLogService;
import com.likelion.hackatonbe.domain.environment.entity.EnvironmentData;
import com.likelion.hackatonbe.domain.environment.repository.EnvironmentDataRepository;
import com.likelion.hackatonbe.domain.scratch.dto.DailyScratchResponse;
import com.likelion.hackatonbe.domain.scratch.service.DailyScratchService;
import com.likelion.hackatonbe.domain.user.entity.Child;
import com.likelion.hackatonbe.domain.user.repository.ChildRepository;
import com.likelion.hackatonbe.global.error.BusinessException;
import com.likelion.hackatonbe.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
    public DailyReportResponse generateDailyReport(
            Long userId,
            LocalDate date,
            ZoneId zoneId
    ) {

        // 1. 해당 사용자의 아이 조회
        Child child = childRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND)
                );


        // 2. 최근 7일 생활 기록 조회
        List<DailyLog> weeklyLogs =
                weeklyDataService.getWeeklyDailyLogs(
                        child.getId(),
                        date
                );


        // 3. 오늘 생활 기록 조회
        List<DailyLogDto.Response> dailyLogs =
                dailyLogService.getDailyLogsByDate(
                        userId,
                        date
                );


        // 4. 오늘 긁음 기록 조회
        DailyScratchResponse scratch =
                dailyScratchService.getDaily(
                        userId,
                        date,
                        zoneId
                );


        // 5. 오늘 시간대별 긁음 분석
        DailyAnalysisResponse dailyAnalysis =
                dailyAnalysisService.getDailyAnalysis(
                        userId,
                        date,
                        zoneId
                );


        // 6. 최근 7일 긁음 분석
        WeeklyAnalysisResponse weeklyAnalysis =
                dailyAnalysisService.getWeeklyAnalysis(
                        userId,
                        date,
                        zoneId
                );


        // 7. 최근 환경 정보 조회
        EnvironmentData environment =
                environmentDataRepository
                        .findTopByChildIdOrderByRecordedAtDesc(
                                child.getId()
                        )
                        .orElse(null);


        // 8. 오늘 식단 텍스트 생성
        String meals =
                buildMealsText(dailyLogs);


        // 9. 오늘 생활 기록 텍스트 생성
        String dailyNotes =
                buildDailyNotesText(dailyLogs);


        // 10. 최근 7일 식단 텍스트 생성
        String weeklyMealsText =
                buildWeeklyMealsText(weeklyLogs);


        // 11. 환경 정보 추출
        Double temperature =
                environment != null
                        ? environment.getTemperature()
                        : null;

        Integer humidity =
                environment != null
                        ? environment.getHumidity()
                        : null;

        String airQuality =
                environment != null
                        ? environment.getAirQuality()
                        : null;


        // 12. AI에게 전달할 시간대별 긁음 텍스트
        String hourlyScratchText =
                buildHourlyScratchText(
                        dailyAnalysis
                );


        // 13. AI에게 전달할 최근 7일 긁음 텍스트
        String weeklyScratchText =
                buildWeeklyScratchText(
                        weeklyAnalysis
                );

        // 13-1. 음식 후보 점수 계산
        ScoredTriggerCandidate foodCandidate =
                findFoodTriggerCandidate(
                        weeklyLogs,
                        weeklyAnalysis
                );

// 13-2. 생활 후보 점수 계산
        ScoredTriggerCandidate moisturizerCandidate =
                findMoisturizerTriggerCandidate(
                        weeklyLogs,
                        weeklyAnalysis
                );


// 13-3. 유효 후보 수집
        List<ScoredTriggerCandidate> scoredCandidates =
                new java.util.ArrayList<>();

        if (foodCandidate != null) {
            scoredCandidates.add(foodCandidate);
        }

        if (moisturizerCandidate != null) {
            scoredCandidates.add(moisturizerCandidate);
        }


// 13-4. 점수 높은 순으로 정렬
        scoredCandidates.sort(
                java.util.Comparator
                        .comparingDouble(
                                ScoredTriggerCandidate::score
                        )
                        .reversed()
        );


// 13-5. 최대 2개만 AI에게 전달
        List<String> triggerCandidates =
                scoredCandidates
                        .stream()
                        .limit(2)
                        .map(candidate ->
                                candidate.type()
                                        + "|"
                                        + candidate.factor()
                                        + "|"
                                        + candidate.score()
                        )
                        .toList();

        //로그확인용
        System.out.println("===== AI INPUT DEBUG =====");
        System.out.println("[TODAY MEALS]");
        System.out.println(meals);

        System.out.println("[WEEKLY MEALS]");
        System.out.println(weeklyMealsText);

        System.out.println("[WEEKLY SCRATCH]");
        System.out.println(weeklyScratchText);

        System.out.println("[HOURLY SCRATCH]");
        System.out.println(hourlyScratchText);

        System.out.println("[TRIGGER CANDIDATES]");
        System.out.println(triggerCandidates);

        System.out.println("===== TRIGGER SCORE DEBUG =====");

        for (int i = 0; i < scoredCandidates.size(); i++) {

            ScoredTriggerCandidate candidate =
                    scoredCandidates.get(i);

            System.out.println(
                    (i + 1)
                            + "순위"
                            + " | type="
                            + candidate.type()
                            + " | factor="
                            + candidate.factor()
                            + " | score="
                            + candidate.score()
            );
        }

        System.out.println("===============================");


        // 14. OpenAI 분석 요청
        ReportAiAnalysisDto aiAnalysis =
                openAIService.analyze(
                        Math.toIntExact(
                                scratch.eventCount()
                        ),
                        scratch.totalSeconds().doubleValue(),
                        hourlyScratchText,
                        weeklyScratchText,
                        meals,
                        weeklyMealsText,
                        dailyNotes,
                        temperature,
                        humidity,
                        airQuality,
                        triggerCandidates
                );


        // 15. 최근 7일 일평균
        double weeklyAverage =
                weeklyAnalysis != null
                        ? weeklyAnalysis.dailyAverage()
                        : 0.0;


        // 16. 오늘 긁음 횟수
        long todayCount =
                scratch.eventCount();


        // 17. 최근 평균 대비 증감률 계산
        double changePercent =
                calculateChangePercent(
                        todayCount,
                        weeklyAverage
                );


        // 18. 긁음 요약 DTO
        ScratchSummaryDto scratchSummary =
                new ScratchSummaryDto(
                        todayCount,
                        scratch.totalSeconds().doubleValue(),
                        weeklyAverage,
                        changePercent
                );


        // 19. 환경 요약 DTO
        EnvironmentSummaryDto environmentSummary =
                new EnvironmentSummaryDto(
                        temperature,
                        humidity,
                        airQuality
                );


        // 20. 최종 일간 리포트 반환
        return new DailyReportResponse(
                date,
                scratchSummary,
                environmentSummary,
                dailyAnalysis.hourly(),
                weeklyAnalysis.daily(),
                aiAnalysis
        );
    }


    /**
     * 최근 평균 대비 오늘 긁음 횟수 증감률
     *
     * 예:
     * 최근 평균 = 5
     * 오늘 = 10
     *
     * → +100%
     */
    private double calculateChangePercent(
            long todayCount,
            double weeklyAverage
    ) {

        // 둘 다 0이면 변화 없음
        if (weeklyAverage == 0.0 && todayCount == 0) {
            return 0.0;
        }

        /*
         * 평균이 0인데 오늘 긁음이 발생한 경우에는
         * 백분율 증가를 정상적으로 정의할 수 없음.
         *
         * 현재 DTO가 double이므로 0으로 반환한다.
         * 추후 필요하면 Double nullable로 변경 가능.
         */
        if (weeklyAverage == 0.0) {
            return 0.0;
        }

        double change =
                ((todayCount - weeklyAverage)
                        / weeklyAverage)
                        * 100.0;

        // 소수점 첫째 자리
        return Math.round(
                change * 10.0
        ) / 10.0;
    }


    /**
     * 오늘 식단을 AI 입력용 문자열로 변환
     */
    private String buildMealsText(
            List<DailyLogDto.Response> dailyLogs
    ) {

        if (dailyLogs == null || dailyLogs.isEmpty()) {
            return "기록 없음";
        }

        String result =
                dailyLogs.stream()
                        .filter(log ->
                                log.getFoods() != null
                        )
                        .filter(log ->
                                !log.getFoods().isEmpty()
                        )
                        .map(log ->
                                log.getMealType()
                                        + ": "
                                        + log.getFoods()
                        )
                        .collect(
                                Collectors.joining("\n")
                        );

        return result.isBlank()
                ? "기록 없음"
                : result;
    }


    /**
     * 오늘 생활 기록을 AI 입력용 문자열로 변환
     */
    private String buildDailyNotesText(
            List<DailyLogDto.Response> dailyLogs
    ) {

        if (dailyLogs == null || dailyLogs.isEmpty()) {
            return "기록 없음";
        }

        String result =
                dailyLogs.stream()
                        .map(log -> {

                            StringBuilder builder =
                                    new StringBuilder();


                            // 증상
                            if (log.getSymptoms() != null
                                    && !log.getSymptoms().isEmpty()) {

                                builder.append("증상: ")
                                        .append(log.getSymptoms())
                                        .append(", ");
                            }


                            // 메모
                            if (log.getMemo() != null
                                    && !log.getMemo().isBlank()) {

                                builder.append("메모: ")
                                        .append(log.getMemo())
                                        .append(", ");
                            }


                            // 샤워
                            if (log.getShowerCount() != null) {

                                builder.append("샤워: ")
                                        .append(
                                                log.getShowerCount()
                                        )
                                        .append("회, ");
                            }


                            // 보습제
                            if (log.getMoisturizerCount() != null) {

                                builder.append("보습제 사용: ")
                                        .append(
                                                log.getMoisturizerCount()
                                        )
                                        .append("회");
                            }


                            return builder.toString()
                                    .replaceAll(
                                            ", $",
                                            ""
                                    );
                        })
                        .filter(text ->
                                !text.isBlank()
                        )
                        .collect(
                                Collectors.joining("\n")
                        );


        return result.isBlank()
                ? "기록 없음"
                : result;
    }


    /**
     * 시간대별 긁음 분석을
     * OpenAI 입력용 문자열로 변환
     */
    private String buildHourlyScratchText(
            DailyAnalysisResponse analysis
    ) {

        if (analysis == null
                || analysis.scratchCount() == 0) {

            return "긁음 기록 없음";
        }


        String hourlyText =
                analysis.hourly()
                        .stream()
                        .filter(item ->
                                item.count() > 0
                        )
                        .map(item ->
                                String.format(
                                        "%02d시: %d회",
                                        item.hour(),
                                        item.count()
                                )
                        )
                        .collect(
                                Collectors.joining("\n")
                        );


        String peakText =
                analysis.peakHour() != null
                        ? String.format(
                        "가장 많이 긁은 시간대: %02d시",
                        analysis.peakHour()
                )
                        : "가장 많이 긁은 시간대: 없음";


        return peakText
                + "\n"
                + hourlyText;
    }


    /**
     * 최근 7일 긁음 분석을
     * OpenAI 입력용 문자열로 변환
     */
    private String buildWeeklyScratchText(
            WeeklyAnalysisResponse analysis
    ) {

        if (analysis == null) {
            return "주간 기록 없음";
        }


        String dailyText =
                analysis.daily()
                        .stream()
                        .map(item ->
                                String.format(
                                        "%s: %d회",
                                        item.date(),
                                        item.count()
                                )
                        )
                        .collect(
                                Collectors.joining("\n")
                        );


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


    /**
     * 최근 7일 식단 기록을
     * OpenAI 입력용 문자열로 변환
     */
    private String buildWeeklyMealsText(
            List<DailyLog> weeklyLogs
    ) {

        if (weeklyLogs == null
                || weeklyLogs.isEmpty()) {

            return "주간 식단 기록 없음";
        }


        String result =
                weeklyLogs.stream()
                        .filter(log ->
                                log.getFoods() != null
                                        && !log.getFoods().isEmpty()
                        )
                        .map(log ->
                                log.getDate()
                                        + " | "
                                        + log.getMealType()
                                        + " | "
                                        + log.getFoods()
                        )
                        .collect(
                                Collectors.joining("\n")
                        );


        return result.isBlank()
                ? "주간 식단 기록 없음"
                : result;
    }

    //음식 요인 추측 계산 로직
    // 음식 요인 후보 + 점수 계산
    private ScoredTriggerCandidate findFoodTriggerCandidate(
            List<DailyLog> weeklyLogs,
            WeeklyAnalysisResponse weeklyAnalysis
    ) {

        if (weeklyLogs == null
                || weeklyLogs.isEmpty()
                || weeklyAnalysis == null) {

            return null;
        }

        // 날짜 → 해당 날짜 긁음 횟수
        var scratchByDate =
                weeklyAnalysis.daily()
                        .stream()
                        .collect(Collectors.toMap(
                                item -> item.date(),
                                item -> item.count()
                        ));

        /*
         * 음식별로:
         * 어떤 날짜에 등장했고,
         * 그 날짜에 몇 번 긁었는지 저장
         */
        var foodScratchStats =
                new java.util.HashMap<
                        String,
                        java.util.List<Long>
                        >();

        for (DailyLog log : weeklyLogs) {

            if (log.getFoods() == null
                    || log.getFoods().isEmpty()) {
                continue;
            }

            long scratchCount =
                    scratchByDate.getOrDefault(
                            log.getDate(),
                            0L
                    );

            // 같은 날짜에 같은 음식이 중복 저장돼도 1회만 계산
            log.getFoods()
                    .stream()
                    .distinct()
                    .forEach(food ->
                            foodScratchStats
                                    .computeIfAbsent(
                                            food,
                                            key -> new java.util.ArrayList<>()
                                    )
                                    .add(scratchCount)
                    );
        }

        double weeklyAverage =
                weeklyAnalysis.dailyAverage();

        int totalDays =
                weeklyAnalysis.daily().size();

        ScoredTriggerCandidate bestCandidate = null;

        for (var entry : foodScratchStats.entrySet()) {

            String food = entry.getKey();
            List<Long> scratchCounts = entry.getValue();

            int appearedDays =
                    scratchCounts.size();

            // 최소 3일 이상 반복되어야 후보 인정
            if (appearedDays < 3) {
                continue;
            }

            double averageScratchOnFoodDays =
                    scratchCounts
                            .stream()
                            .mapToLong(Long::longValue)
                            .average()
                            .orElse(0.0);

            // 음식 등장일 긁음 평균이 전체 평균보다 높지 않으면 제외
            if (averageScratchOnFoodDays <= weeklyAverage) {
                continue;
            }

            /*
             * ① 반복 등장 빈도 점수
             *
             * 7일 내 7일 등장 → 1.0
             * 7일 내 4일 등장 → 약 0.57
             */
            double frequencyScore =
                    totalDays > 0
                            ? (double) appearedDays / totalDays
                            : 0.0;

            /*
             * ② 해당 음식 등장일의 긁음 증가 정도
             *
             * 예)
             * 주간 평균 = 4
             * 음식 등장일 평균 = 6
             *
             * 증가율 = (6 - 4) / 4 = 0.5
             */
            double scratchIncreaseScore;

            if (weeklyAverage > 0) {
                scratchIncreaseScore =
                        (averageScratchOnFoodDays - weeklyAverage)
                                / weeklyAverage;
            } else {
                scratchIncreaseScore =
                        averageScratchOnFoodDays > 0
                                ? 1.0
                                : 0.0;
            }

            // 특정 값이 지나치게 점수를 압도하지 않도록 0~1 범위 제한
            scratchIncreaseScore =
                    Math.max(
                            0.0,
                            Math.min(
                                    1.0,
                                    scratchIncreaseScore
                            )
                    );

            /*
             * 최종 점수
             *
             * 반복 빈도 50%
             * 긁음 증가 정도 50%
             *
             * 결과 범위: 0 ~ 100
             */
            double score =
                    (frequencyScore * 50.0)
                            + (scratchIncreaseScore * 50.0);

            score =
                    Math.round(score * 10.0) / 10.0;

            ScoredTriggerCandidate candidate =
                    new ScoredTriggerCandidate(
                            "FOOD",
                            food,
                            score
                    );

            if (bestCandidate == null
                    || candidate.score() > bestCandidate.score()) {

                bestCandidate = candidate;
            }
        }

        return bestCandidate;
    }

    //생활요인 추측 계산 로직
    // 생활 요인(보습 기록 감소) 후보 + 점수 계산
    // 생활 요인(보습 기록 감소) 후보 + 점수 계산
// 이전 3일 평균과 최근 3일 평균을 비교하여 하루치 이상값의 영향을 줄임
    private ScoredTriggerCandidate findMoisturizerTriggerCandidate(
            List<DailyLog> weeklyLogs,
            WeeklyAnalysisResponse weeklyAnalysis
    ) {

        if (weeklyLogs == null
                || weeklyAnalysis == null
                || weeklyAnalysis.daily() == null
                || weeklyAnalysis.daily().isEmpty()) {
            return null;
        }

        // 보습 기록이 있는 날짜만 사용
        List<DailyLog> logs = weeklyLogs.stream()
                .filter(log -> log.getMoisturizerCount() != null)
                .sorted(java.util.Comparator.comparing(DailyLog::getDate))
                .toList();

        // 이전 3일 + 최근 3일 비교를 위해 최소 6일 필요
        if (logs.size() < 6) {
            return null;
        }

        /*
         * 최근 기록 6일만 사용
         *
         * [이전 3일] [최근 3일]
         */
        List<DailyLog> recentSixLogs =
                logs.subList(logs.size() - 6, logs.size());

        List<DailyLog> previousLogs =
                recentSixLogs.subList(0, 3);

        List<DailyLog> recentLogs =
                recentSixLogs.subList(3, 6);


        // 1. 이전 3일 평균 보습 횟수
        double previousMoisturizerAverage =
                previousLogs.stream()
                        .mapToInt(DailyLog::getMoisturizerCount)
                        .average()
                        .orElse(0.0);

        // 2. 최근 3일 평균 보습 횟수
        double recentMoisturizerAverage =
                recentLogs.stream()
                        .mapToInt(DailyLog::getMoisturizerCount)
                        .average()
                        .orElse(0.0);


        /*
         * 날짜 → 긁음 횟수
         */
        var scratchByDate =
                weeklyAnalysis.daily()
                        .stream()
                        .collect(Collectors.toMap(
                                item -> item.date(),
                                item -> item.count()
                        ));


        // 3. 이전 3일 평균 긁음 횟수
        double previousScratchAverage =
                previousLogs.stream()
                        .mapToLong(log ->
                                scratchByDate.getOrDefault(
                                        log.getDate(),
                                        0L
                                )
                        )
                        .average()
                        .orElse(0.0);


        // 4. 최근 3일 평균 긁음 횟수
        double recentScratchAverage =
                recentLogs.stream()
                        .mapToLong(log ->
                                scratchByDate.getOrDefault(
                                        log.getDate(),
                                        0L
                                )
                        )
                        .average()
                        .orElse(0.0);


        /*
         * 보습은 감소하고,
         * 긁음은 증가했을 때만 후보 인정
         */
        if (recentMoisturizerAverage >= previousMoisturizerAverage
                || recentScratchAverage <= previousScratchAverage) {
            return null;
        }


        /*
         * ① 생활습관 변화 점수
         *
         * 예:
         * 이전 3일 평균 보습 = 3회
         * 최근 3일 평균 보습 = 1회
         *
         * 감소율 = (3 - 1) / 3 = 0.667
         */
        double lifestyleChangeScore;

        if (previousMoisturizerAverage > 0) {

            lifestyleChangeScore =
                    (previousMoisturizerAverage
                            - recentMoisturizerAverage)
                            / previousMoisturizerAverage;

        } else {
            lifestyleChangeScore = 0.0;
        }

        lifestyleChangeScore =
                Math.max(
                        0.0,
                        Math.min(1.0, lifestyleChangeScore)
                );


        /*
         * ② 긁음 증가 점수
         *
         * 이전 3일 평균 vs 최근 3일 평균
         */
        double scratchGrowthScore;

        if (previousScratchAverage > 0) {

            scratchGrowthScore =
                    (recentScratchAverage
                            - previousScratchAverage)
                            / previousScratchAverage;

        } else {

            scratchGrowthScore =
                    recentScratchAverage > 0
                            ? 1.0
                            : 0.0;
        }

        scratchGrowthScore =
                Math.max(
                        0.0,
                        Math.min(1.0, scratchGrowthScore)
                );


        /*
         * 최종 점수
         *
         * 보습 변화 정도 50%
         * 긁음 증가 정도 50%
         */
        double score =
                (lifestyleChangeScore * 50.0)
                        + (scratchGrowthScore * 50.0);

        score =
                Math.round(score * 10.0) / 10.0;


        // 디버그
        System.out.println("===== MOISTURIZER SCORE DEBUG =====");
        System.out.println(
                "이전 3일 평균 보습 = "
                        + previousMoisturizerAverage
        );
        System.out.println(
                "최근 3일 평균 보습 = "
                        + recentMoisturizerAverage
        );
        System.out.println(
                "이전 3일 평균 긁음 = "
                        + previousScratchAverage
        );
        System.out.println(
                "최근 3일 평균 긁음 = "
                        + recentScratchAverage
        );
        System.out.println(
                "생활 변화 점수 = "
                        + lifestyleChangeScore
        );
        System.out.println(
                "긁음 증가 점수 = "
                        + scratchGrowthScore
        );
        System.out.println(
                "최종 LIFESTYLE 점수 = "
                        + score
        );
        System.out.println("===================================");


        return new ScoredTriggerCandidate(
                "LIFESTYLE",
                "보습 기록 감소",
                score
        );
    }

    /**
     * 자극 요인 후보 + 백엔드 계산 점수
     *
     * score가 높을수록 최근 7일 데이터에서
     * 긁음 증가와 더 강하게 함께 관찰된 후보
     */
    private record ScoredTriggerCandidate(
            String type,
            String factor,
            double score
    ) {
    }

}