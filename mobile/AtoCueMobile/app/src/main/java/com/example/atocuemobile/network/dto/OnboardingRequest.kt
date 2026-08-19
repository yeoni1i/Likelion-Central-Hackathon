package com.example.atocuemobile.network.dto

data class OnboardingRequest(
    val parentName: String,
    val childName: String,
    val birthDate: String,
    val height: Double,
    val weight: Double,
    val skinConditions: List<String>,
    val specialNote: String
)