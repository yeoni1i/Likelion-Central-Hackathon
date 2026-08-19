package com.likelion.hackatonbe.domain.weather.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    private Double temperature;   // 기온
    private Integer humidity;     // 습도
    private String airQuality;    // 미세먼지 상태
}