package com.likelion.hackatonbe.domain.dailylog.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

public class DailyLogDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateRequest {
        private String mealType;     // BREAKFAST, LUNCH, DINNER, SNACK
        private List<String> foods;
        private LocalDate date;
    }

    @Getter
    public static class Response {
        private Long id;
        private String mealType;
        private List<String> foods;
        private String imageUrl;
        private LocalDate date;

        public Response(Long id, String mealType, List<String> foods, String imageUrl, LocalDate date) {
            this.id = id;
            this.mealType = mealType;
            this.foods = foods;
            this.imageUrl = imageUrl;
            this.date = date;
        }
    }
}
