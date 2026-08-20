package com.likelion.hackatonbe.domain.analysis.controller;

import com.likelion.hackatonbe.domain.analysis.dto.DailyReportResponse;
import com.likelion.hackatonbe.domain.analysis.service.AnalysisService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/analysis")
public class OpenAIController {

    private final AnalysisService analysisService;

    public OpenAIController(
            AnalysisService analysisService
    ) {
        this.analysisService = analysisService;
    }

    @GetMapping("/ping")
    public String ping() {
        return "OK";
    }

    @GetMapping("/daily")
    public DailyReportResponse analyzeDaily(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam LocalDate date
    ) {
        return analysisService.generateDailyReport(
                userId,
                date,
                ZoneId.of("Asia/Seoul")
        );
    }
}