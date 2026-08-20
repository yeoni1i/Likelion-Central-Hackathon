package com.likelion.hackatonbe.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

// LoginResponse.java
@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long userId;

    @JsonProperty("isOnboarded") // 명시적 지정
    private boolean isOnboarded;

    private Long childId;
}