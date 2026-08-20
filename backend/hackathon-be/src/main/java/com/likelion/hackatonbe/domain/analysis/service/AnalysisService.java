package com.likelion.hackatonbe.domain.analysis.service;

import com.likelion.hackatonbe.domain.analysis.dto.*;
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


        // 20. 위험 식단 섹션 DTO 조립
        RiskFoodSectionDto riskFoodSection = new RiskFoodSectionDto(
                aiAnalysis.riskFoodTitle() != null ? aiAnalysis.riskFoodTitle() : "자주 섭취한 식단을 주의 깊게 관찰해 보세요.",
                aiAnalysis.riskFoods() != null ? aiAnalysis.riskFoods() : List.of()
        );


        // 21. 최종 일간 리포트 반환
        return new DailyReportResponse(
                date,
                scratchSummary,
                environmentSummary,
                dailyAnalysis.hourly(),
                weeklyAnalysis.daily(),
                aiAnalysis,
                riskFoodSection
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
                || weeklyAnalysis == null
                || weeklyAnalysis.daily() == null
                || weeklyAnalysis.daily().isEmpty()) {

            return null;
        }

        // 날짜 → 긁음 횟수
        var scratchByDate =
                weeklyAnalysis.daily()
                        .stream()
                        .collect(Collectors.toMap(
                                item -> item.date(),
                                item -> item.count()
                        ));

        /*
         * 음식군 → 등장 날짜
         *
         * 예:
         * 코코아 시리얼 -> 초콜릿/코코아
         * 초코우유     -> 초콜릿/코코아
         */
        var foodDates =
                new java.util.HashMap<
                        String,
                        java.util.Set<LocalDate>
                        >();

        for (DailyLog log : weeklyLogs) {

            if (log.getFoods() == null
                    || log.getFoods().isEmpty()) {
                continue;
            }

            for (String rawFood : log.getFoods()) {

                String food = normalizeFoodTrigger(rawFood);

                if (food == null || food.isBlank()) {
                    continue;
                }

                foodDates
                        .computeIfAbsent(
                                food,
                                key -> new java.util.HashSet<>()
                        )
                        .add(log.getDate());
            }
        }

        ScoredTriggerCandidate bestCandidate = null;

        for (var entry : foodDates.entrySet()) {

            String food = entry.getKey();
            java.util.Set<LocalDate> exposedDates =
                    entry.getValue();

            int appearedDays = exposedDates.size();

            /*
             * 최소 2일 이상 반복 노출된 음식군만 사용
             */
            if (appearedDays < 2) {
                continue;
            }

            /*
             * 해당 음식을 먹은 날의 긁음
             */
            double exposedAverage =
                    weeklyAnalysis.daily()
                            .stream()
                            .filter(item ->
                                    exposedDates.contains(item.date())
                            )
                            .mapToLong(item -> item.count())
                            .average()
                            .orElse(0.0);

            /*
             * 해당 음식을 먹지 않은 날의 긁음
             */
            double nonExposedAverage =
                    weeklyAnalysis.daily()
                            .stream()
                            .filter(item ->
                                    !exposedDates.contains(item.date())
                            )
                            .mapToLong(item -> item.count())
                            .average()
                            .orElse(0.0);

            /*
             * 모든 날 등장한 음식은 비교군이 없으므로 제외
             *
             * 예: 매일 쌀밥을 먹었다면
             * 쌀밥 때문인지 판단할 수 없음
             */
            if (appearedDays >= weeklyAnalysis.daily().size()) {
                continue;
            }

            /*
             * 먹은 날 긁음이 더 많지 않다면 후보 제외
             */
            if (exposedAverage <= nonExposedAverage) {
                continue;
            }

            /*
             * 노출일 vs 비노출일 증가 정도
             */
            double scratchIncreaseScore;

            if (nonExposedAverage > 0) {

                scratchIncreaseScore =
                        (exposedAverage - nonExposedAverage)
                                / nonExposedAverage;

            } else {

                scratchIncreaseScore =
                        exposedAverage > 0
                                ? 1.0
                                : 0.0;
            }

            scratchIncreaseScore =
                    Math.max(
                            0.0,
                            Math.min(
                                    1.0,
                                    scratchIncreaseScore
                            )
                    );

            /*
             * 반복성
             *
             * 2일 등장 → 기본 신뢰도
             * 3일 이상 → 조금 더 높은 신뢰도
             *
             * 하지만 단순 빈도가 점수를 지배하지 않게 함
             */
            double repetitionScore =
                    Math.min(
                            1.0,
                            appearedDays / 3.0
                    );

            /*
             * 최종 점수
             *
             * 긁음 증가 연관성 80%
             * 반복성 20%
             */
            double score =
                    (scratchIncreaseScore * 80.0)
                            + (repetitionScore * 20.0);

            score =
                    Math.round(score * 10.0) / 10.0;


            System.out.println(
                    "[FOOD SCORE] "
                            + food
                            + " | 등장="
                            + appearedDays
                            + "일"
                            + " | 섭취일 평균="
                            + exposedAverage
                            + " | 비섭취일 평균="
                            + nonExposedAverage
                            + " | score="
                            + score
            );


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


    private String normalizeFoodTrigger(String food) {

        if (food == null) {
            return null;
        }

        String normalized = food.trim();

        /*
         * 초콜릿 / 코코아 계열
         */
        if (normalized.contains("초코")
                || normalized.contains("초콜릿")
                || normalized.contains("코코아")) {

            return "초콜릿/코코아";
        }

        return normalized;
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