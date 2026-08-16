package com.likelion.hackatonbe.domain.dailylog.entity;

import com.likelion.hackatonbe.domain.user.entity.Child;
import com.likelion.hackatonbe.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "daily_logs")
public class DailyLog extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @Column(nullable = false)
    private LocalDate date;

    private String mealType;

    @ElementCollection
    @CollectionTable(name = "daily_log_foods", joinColumns = @JoinColumn(name = "daily_log_id"))
    @Column(name = "food_name")
    @Builder.Default
    private List<String> foods = new ArrayList<>();

    private String imageUrl;

    private Integer showerCount;

    private Integer moisturizerCount;

    @ElementCollection
    @CollectionTable(name = "daily_log_symptoms", joinColumns = @JoinColumn(name = "daily_log_id"))
    @Column(name = "symptom")
    @Builder.Default
    private List<String> symptoms = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String memo;

    public void update(
            String mealType,
            List<String> foods,
            String imageUrl,
            Integer showerCount,
            Integer moisturizerCount,
            List<String> symptoms,
            String memo
    ) {
        this.mealType = mealType;
        this.foods = foods;
        this.imageUrl = imageUrl;
        this.showerCount = showerCount;
        this.moisturizerCount = moisturizerCount;
        this.symptoms = symptoms;
        this.memo = memo;
    }
}