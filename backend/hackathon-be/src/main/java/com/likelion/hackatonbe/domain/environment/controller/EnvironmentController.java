package com.likelion.hackatonbe.domain.environment.controller;

import com.likelion.hackatonbe.domain.environment.service.EnvironmentService;
import com.likelion.hackatonbe.domain.weather.dto.WeatherResponse;
import com.likelion.hackatonbe.global.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class EnvironmentController {

    private final EnvironmentService environmentService;

    @GetMapping
    public ResponseEntity<WeatherResponse> getWeatherAndSave(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam double lat,
            @RequestParam double lon) {

        Long userId = userDetails.getUserId();
        WeatherResponse result = environmentService.getAndSaveTodayEnvironment(userId, lat, lon);
        return ResponseEntity.ok(result);
    }
}
