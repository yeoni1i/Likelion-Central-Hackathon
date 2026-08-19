package com.likelion.hackatonbe.domain.analysis.controller;

import com.likelion.hackatonbe.domain.analysis.dto.DailyAnalysisResponse;
import com.likelion.hackatonbe.domain.analysis.service.DailyAnalysisService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import com.likelion.hackatonbe.domain.analysis.dto.WeeklyAnalysisResponse;
import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    private final DailyAnalysisService dailyAnalysisService;

    public AnalysisController(
            DailyAnalysisService dailyAnalysisService
    ) {
        this.dailyAnalysisService = dailyAnalysisService;
    }

    @GetMapping("/reports/daily")
    public DailyAnalysisResponse getDailyAnalysis(
            @RequestHeader("X-User-Id") Long userId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(defaultValue = "Asia/Seoul")
            String timezone
    ) {

        return dailyAnalysisService.getDailyAnalysis(
                userId,
                date,
                ZoneId.of(timezone)
        );
    }

    @GetMapping("/reports/weekly")
    public WeeklyAnalysisResponse getWeeklyAnalysis(
            @RequestHeader("X-User-Id") Long userId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(defaultValue = "Asia/Seoul")
            String timezone
    ) {

        return dailyAnalysisService.getWeeklyAnalysis(
                userId,
                date,
                ZoneId.of(timezone)
        );
    }
}