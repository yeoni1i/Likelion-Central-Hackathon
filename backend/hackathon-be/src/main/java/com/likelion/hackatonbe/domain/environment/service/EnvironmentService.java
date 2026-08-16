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

import java.time.LocalDate;

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

        WeatherResponse weather = weatherService.getWeatherAndAirQuality(lat, lon);
        LocalDate today = LocalDate.now();

        environmentDataRepository.findByChildIdAndDate(child.getId(), today)
                .ifPresentOrElse(
                        existingData -> existingData.updateWeather(
                                weather.getTemperature(),
                                weather.getHumidity(),
                                weather.getAirQuality()
                        ),
                        () -> {
                            EnvironmentData newData = EnvironmentData.builder()
                                    .child(child)
                                    .temperature(weather.getTemperature())
                                    .humidity(weather.getHumidity())
                                    .airQuality(weather.getAirQuality())
                                    .date(today) // recordedAt -> date 변경
                                    .build();
                            environmentDataRepository.save(newData);
                        }
                );

        return weather;
    }
}