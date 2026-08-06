package com.likelion.hackatonbe.domain.dailylog.entity;

import com.likelion.hackatonbe.domain.user.entity.Child;
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
public class DailyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    private String mealType;

    @ElementCollection
    @CollectionTable(name = "daily_log_foods", joinColumns = @JoinColumn(name = "daily_log_id"))
    @Column(name = "food_name")
    @Builder.Default
    private List<String> foods = new ArrayList<>();

    private String imageUrl;

    private LocalDate date;
}