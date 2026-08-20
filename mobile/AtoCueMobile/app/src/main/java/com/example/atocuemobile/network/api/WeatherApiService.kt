package com.example.atocuemobile.network.api

import com.example.atocuemobile.network.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface WeatherApiService {
    @GET("api/weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Header("Authorization") token: String? = null
    ): WeatherResponse
}