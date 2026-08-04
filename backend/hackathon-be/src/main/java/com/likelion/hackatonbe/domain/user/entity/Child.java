package com.likelion.hackatonbe.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "children")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 30)
    private String name;

    private LocalDate birthDate;
    private Double height;
    private Double weight;

    @ElementCollection
    @CollectionTable(name = "child_skin_conditions", joinColumns = @JoinColumn(name = "child_id"))
    @Column(name = "condition_name")
    private List<String> skinConditions = new ArrayList<>();

    @Column(length = 500)
    private String specialNote;

    @Builder
    public Child(User user, String name, LocalDate birthDate, Double height, Double weight, List<String> skinConditions, String specialNote) {
        this.user = user;
        this.name = name;
        this.birthDate = birthDate;
        this.height = height;
        this.weight = weight;
        this.skinConditions = skinConditions != null ? skinConditions : new ArrayList<>();
        this.specialNote = specialNote;
    }
}