package com.likelion.hackatonbe.domain.environment.entity;

import com.likelion.hackatonbe.domain.user.entity.Child;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        name = "environment_data",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"child_id", "date"})
        }
)
public class EnvironmentData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Integer humidity;

    @Column(nullable = false)
    private String airQuality;

    @Column(nullable = false)
    private LocalDate date;

    public void updateWeather(Double temperature, Integer humidity, String airQuality) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.airQuality = airQuality;
    }
}