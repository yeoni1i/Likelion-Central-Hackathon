package com.likelion.hackatonbe.domain.dailylog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class DailyLogDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String mealType;     // BREAKFAST, LUNCH, DINNER, SNACK
        private List<String> foods;
        private Integer showerCount;
        private Integer moisturizerCount;
        private List<String> symptoms;
        private String memo;
        private LocalDate date;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String mealType;
        private List<String> foods;
        private String imageUrl;
        private Integer showerCount;
        private Integer moisturizerCount;
        private List<String> symptoms;
        private String memo;
        private LocalDate date;
    }
}
