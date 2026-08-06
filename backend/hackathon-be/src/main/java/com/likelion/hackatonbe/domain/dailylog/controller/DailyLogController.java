package com.likelion.hackatonbe.domain.dailylog.controller;

import com.likelion.hackatonbe.domain.dailylog.dto.DailyLogDto;
import com.likelion.hackatonbe.domain.dailylog.service.DailyLogService;
import com.likelion.hackatonbe.global.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/daily-logs")
@RequiredArgsConstructor
public class DailyLogController {

    private final DailyLogService dailyLogService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<DailyLogDto.Response> createDailyLog(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "request") DailyLogDto.CreateRequest request
    ) {
        DailyLogDto.Response response = dailyLogService.createDailyLog(userDetails.getUserId(), image, request);
        return ResponseEntity.ok(response);
    }
}
