package com.example.atocuemobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atocuemobile.network.RetrofitClient
import com.example.atocuemobile.network.dto.DailyScratchResponse
import com.example.atocuemobile.network.dto.ScratchTimelineItem
import com.example.atocuemobile.network.dto.ScratchTimelineResponse
import com.example.atocuemobile.network.dto.WeatherResponse
import com.example.atocuemobile.ui.model.ScratchStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class TimelineUiItem(
    val hourLabel: String,
    val status: ScratchStatus,
    val durationLabel: String,
    val timeRangeLabel: String
)

data class HomeUiState(
    val isLoading: Boolean = false,
    val isDeviceConnected: Boolean = true,
    val isDetecting: Boolean = true,
    val currentStatus: ScratchStatus = ScratchStatus.STABLE,
    val totalScratchSecondsToday: Int = 0,
    val weatherData: WeatherResponse? = null,
    val timelineItems: List<TimelineUiItem> = emptyList(),
    val errorMessage: String? = null
)

class HomeViewModel(
    private val userId: Long = 1L,
    initialDeviceConnected: Boolean = true
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isDeviceConnected = initialDeviceConnected))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadToday() {
        if (!_uiState.value.isDeviceConnected) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

                val dailyReport = RetrofitClient.api.getDailyScratchReport(
                    userId = userId,
                    date = today
                )
                val timeline = RetrofitClient.api.getScratchTimeline(
                    userId = userId,
                    date = today
                )

                // averageIntensity를 문자열/숫자 모두 대응
                val currentStatus = ScratchStatus.fromIntensity(dailyReport.averageIntensity?.toString())

                // totalSeconds를 안전하게 Int로 변환
                val totalSeconds = dailyReport.totalSeconds.toString().toDoubleOrNull()?.toInt() ?: 0

                val items = timeline.events.map { event: ScratchTimelineItem ->
                    val zoned = Instant.parse(event.startTs.toString()).atZone(ZoneId.of("Asia/Seoul"))
                    val hour = zoned.hour
                    val ampm = if (hour < 12) "AM" else "PM"
                    val hour12 = when {
                        hour == 0 -> 12
                        hour > 12 -> hour - 12
                        else -> hour
                    }

                    // durationSec를 안전하게 분 단위로 변환
                    val minutes = ((event.durationSec.toString().toDoubleOrNull() ?: 0.0) / 60).toInt()

                    TimelineUiItem(
                        hourLabel = "${"%02d".format(hour12)}:00\n$ampm",
                        status = ScratchStatus.fromIntensity(event.intensity),
                        durationLabel = "${minutes}분",
                        timeRangeLabel = "%02d:00~%02d:00".format(hour, hour)
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentStatus = currentStatus,
                        totalScratchSecondsToday = totalSeconds,
                        timelineItems = items
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "데이터를 불러오지 못했습니다.")
                }
            }
        }
    }

    fun fetchWeather(lat: Double = 37.5665, lon: Double = 126.9780, token: String? = null) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getWeather(
                    lat = lat,
                    lon = lon,
                    token = token
                )
                _uiState.update { it.copy(weatherData = response) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onStartDetection() {
        _uiState.update { it.copy(isDetecting = true) }
        loadToday()
    }

    fun onStopDetection() {
        _uiState.update { it.copy(isDetecting = false) }
    }
}