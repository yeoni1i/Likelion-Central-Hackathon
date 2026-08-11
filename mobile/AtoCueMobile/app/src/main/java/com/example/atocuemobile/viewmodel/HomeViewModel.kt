package com.example.atocuemobile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atocuemobile.network.RetrofitClient
import com.example.atocuemobile.network.dto.WeatherResponse
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    var weatherData by mutableStateOf<WeatherResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchWeather(lat: Double, lon: Double, token: String? = null) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.weatherApiService.getWeather(
                    lat = lat,
                    lon = lon,
                    token = token
                )
                weatherData = response
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "날씨 정보를 불러오지 못했습니다."
            } finally {
                isLoading = false
            }
        }
    }
}