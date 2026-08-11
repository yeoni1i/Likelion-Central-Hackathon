package com.example.atocuemobile.network.dto

data class WeatherResponse(
    val temperature: Double,
    val humidity: Int,
    val airQuality: String
)