package com.likelion.hackatonbe.domain.user.dto;

import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

@Getter
public class OnboardingRequest {
    private String parentName;
    private String childName;
    private LocalDate birthDate;
    private Double height;
    private Double weight;
    private List<String> skinConditions;
    private String specialNote;
}