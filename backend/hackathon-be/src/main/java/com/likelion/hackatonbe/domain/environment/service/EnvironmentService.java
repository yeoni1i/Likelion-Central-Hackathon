package com.likelion.hackatonbe.domain.environment.service;

import com.likelion.hackatonbe.domain.environment.entity.EnvironmentData;
import com.likelion.hackatonbe.domain.environment.repository.EnvironmentDataRepository;
import com.likelion.hackatonbe.domain.user.entity.Child;
import com.likelion.hackatonbe.domain.user.repository.ChildRepository;
import com.likelion.hackatonbe.domain.weather.dto.WeatherResponse;
import com.likelion.hackatonbe.domain.weather.service.WeatherService;
import com.likelion.hackatonbe.global.error.BusinessException;
import com.likelion.hackatonbe.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnvironmentService {

    private final WeatherService weatherService;
    private final EnvironmentDataRepository environmentDataRepository;
    private final ChildRepository childRepository;

    @Transactional
    public WeatherResponse getAndSaveTodayEnvironment(Long userId, Double lat, Double lon) {
        Child child = childRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_FOUND));

        Optional<EnvironmentData> latestData = environmentDataRepository
                .findTopByChildIdOrderByRecordedAtDesc(child.getId());

        if (latestData.isPresent() &&
                latestData.get().getRecordedAt().isAfter(LocalDateTime.now().minusHours(1))) {

            EnvironmentData recent = latestData.get();
            return WeatherResponse.builder()
                    .temperature(recent.getTemperature())
                    .humidity(recent.getHumidity())
                    .airQuality(recent.getAirQuality())
                    .build();
        }

        WeatherResponse weather = weatherService.getWeatherAndAirQuality(lat, lon);

        EnvironmentData newData = EnvironmentData.builder()
                .child(child)
                .temperature(weather.getTemperature())
                .humidity(weather.getHumidity())
                .airQuality(weather.getAirQuality())
                .recordedAt(LocalDateTime.now())
                .build();

        environmentDataRepository.save(newData);

        return weather;
    }
}