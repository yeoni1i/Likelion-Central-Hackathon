package com.likelion.hackatonbe.domain.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.hackatonbe.domain.weather.dto.WeatherResponse;
import com.likelion.hackatonbe.global.error.BusinessException;
import com.likelion.hackatonbe.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${openweathermap.api.url}")
    private String baseUrl;

    public WeatherResponse getWeatherAndAirQuality(Double lat, Double lon) {
        if (lat == null || lon == null || lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            throw new BusinessException(ErrorCode.INVALID_LOCATION_PARAMETER);
        }

        try {
            String weatherUrl = UriComponentsBuilder.fromUriString(baseUrl + "/weather")
                    .queryParam("lat", lat)
                    .queryParam("lon", lon)
                    .queryParam("appid", apiKey)
                    .queryParam("units", "metric")
                    .toUriString();

            String weatherJsonString = restTemplate.getForObject(weatherUrl, String.class);
            JsonNode weatherJson = objectMapper.readTree(weatherJsonString);

            if (weatherJson == null || !weatherJson.has("main")) {
                throw new BusinessException(ErrorCode.WEATHER_API_ERROR);
            }

            double temp = weatherJson.path("main").path("temp").asDouble();
            int humidity = weatherJson.path("main").path("humidity").asInt();

            String airUrl = UriComponentsBuilder.fromUriString(baseUrl + "/air_pollution")
                    .queryParam("lat", lat)
                    .queryParam("lon", lon)
                    .queryParam("appid", apiKey)
                    .toUriString();

            String airJsonString = restTemplate.getForObject(airUrl, String.class);
            JsonNode airJson = objectMapper.readTree(airJsonString);

            int aqi = 3;
            if (airJson != null && airJson.has("list") && !airJson.path("list").isEmpty()) {
                aqi = airJson.path("list").get(0).path("main").path("aqi").asInt(3);
            }

            double roundedTemp = Math.round(temp * 10) / 10.0;
            String airQualityText = convertAqiToText(aqi);

            return WeatherResponse.builder()
                    .temperature(roundedTemp)
                    .humidity(humidity)
                    .airQuality(airQualityText)
                    .build();

        } catch (RestClientException | JsonProcessingException e) {
            log.error("OpenWeatherMap API 통신/파싱 에러: {}", e.getMessage());
            throw new BusinessException(ErrorCode.WEATHER_API_ERROR);
        }
    }

    private String convertAqiToText(int aqi) {
        return switch (aqi) {
            case 1 -> "매우 좋음";
            case 2 -> "좋음";
            case 3 -> "보통";
            case 4 -> "나쁨";
            case 5 -> "매우 나쁨";
            default -> "보통";
        };
    }
}