package com.example.atocuemobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atocuemobile.network.RetrofitClient
import com.example.atocuemobile.network.dto.DailyScratchResponse
import com.example.atocuemobile.network.dto.ScratchTimelineItem
import com.example.atocuemobile.network.dto.ScratchTimelineResponse
import com.example.atocuemobile.network.dto.WeatherResponse
import com.example.atocuemobile.ui.model.ScratchStatus
import com.example.atocuemobile.ui.screen.GuideMessage
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
    val isDeviceConnected: Boolean = false,
    val isDetecting: Boolean = false,
    val pairingCode: String = "------",
    val currentStatus: ScratchStatus = ScratchStatus.STABLE,
    val totalScratchSecondsToday: Int = 0,
    val weatherData: WeatherResponse? = null,
    val guideList: List<GuideMessage> = emptyList(),
    val timelineItems: List<TimelineUiItem> = emptyList(),
    val errorMessage: String? = null
)

class HomeViewModel(
    private val userId: Long = 1L,       // 부모 유저 ID
    private val childId: Long = 1L,      // 자식 ID
    initialDeviceConnected: Boolean = false
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            isDeviceConnected = initialDeviceConnected,
            guideList = generateWeatherGuides(null)
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // 1. 워치 6자리 페어링 코드 발급
    fun fetchPairingCode() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val token = RetrofitClient.accessToken ?: ""
                val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

                val response = RetrofitClient.api.createPairingCode(
                    token = formattedToken,
                    childId = childId
                )
                _uiState.update {
                    it.copy(
                        pairingCode = response.pairingCode,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    // 2. 워치 연결 완료 처리
    fun onDeviceConnected() {
        _uiState.update { it.copy(isDeviceConnected = true, isDetecting = false) }
        loadToday()
    }

    // 3. 오늘 긁음 데이터 조회
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

                val currentStatus = ScratchStatus.fromIntensity(dailyReport.averageIntensity?.toString())
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

    // 4. 날씨 정보 조회 (토큰 헤더 추가 완료)
    fun fetchWeather(lat: Double = 37.5665, lon: Double = 126.9780) {
        viewModelScope.launch {
            try {
                val token = RetrofitClient.accessToken ?: ""
                val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

                val response = RetrofitClient.api.getWeather(
                    token = formattedToken,
                    lat = lat,
                    lon = lon
                )
                val guides = generateWeatherGuides(response)
                _uiState.update {
                    it.copy(
                        weatherData = response,
                        guideList = guides
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun generateWeatherGuides(weather: WeatherResponse?): List<GuideMessage> {
        if (weather == null) {
            return listOf(
                GuideMessage("실내습도 50%이상 유지", "적정한 실내 습도를 유지하여 피부 건조를 예방하세요."),
                GuideMessage("보습제 자주 덧바르기", "건조함을 느낄 때마다 보습제를 덧발라주세요."),
                GuideMessage("적정 실내온도 유지", "실내 온도를 20~22도로 쾌적하게 맞춰주세요.")
            )
        }

        val guides = mutableListOf<GuideMessage>()

        when {
            weather.humidity < 40 -> {
                guides.add(
                    GuideMessage(
                        title = "실내가 건조해 보습이 필요해요",
                        description = "현재 실외 습도는 ${weather.humidity}%입니다. 실내에서는 적정한 온/습도를 유지해주세요."
                    )
                )
            }
            weather.humidity > 65 -> {
                guides.add(
                    GuideMessage(
                        title = "습도가 높아 땀띠에 주의하세요",
                        description = "현재 습도가 ${weather.humidity}%로 높습니다. 통풍이 잘되는 면 소재 옷을 입히고 실내를 시원하게 유지해주세요."
                    )
                )
            }
            else -> {
                guides.add(
                    GuideMessage(
                        title = "실내습도 50%이상 유지",
                        description = "현재 습도는 ${weather.humidity}%로 적절합니다. 실내 적정 온도를 유지하며 보습을 관리해주세요."
                    )
                )
            }
        }

        when (weather.airQuality) {
            "나쁨", "매우 나쁨", "매우나쁨" -> {
                guides.add(
                    GuideMessage(
                        title = "미세먼지 농도가 높은 날이에요",
                        description = "공기질이 '${weather.airQuality}' 상태입니다. 외출을 자제하고 외출 후에는 즉시 미온수로 씻겨주세요."
                    )
                )
            }
            else -> {
                guides.add(
                    GuideMessage(
                        title = "미세먼지 [${weather.airQuality}] 대응",
                        description = "공기질이 양호합니다. 쾌적한 실내 환경을 유지하고 가벼운 환기를 권장합니다."
                    )
                )
            }
        }

        if (weather.temperature >= 26.0) {
            guides.add(
                GuideMessage(
                    title = "체온 상승으로 인한 가려움 주의",
                    description = "현재 기온 ${weather.temperature.toInt()}°C입니다. 아이가 땀을 흘리면 가려움이 심해질 수 있으니 수시로 땀을 닦아주세요."
                )
            )
        } else {
            guides.add(
                GuideMessage(
                    title = "보습 케어 가이드",
                    description = "현재 기온은 ${weather.temperature.toInt()}도입니다. 세안 및 목욕 후 3분 이내에 보습제를 충분히 발라주세요."
                )
            )
        }

        return guides
    }

    fun onStartDetection() {
        _uiState.update { it.copy(isDetecting = true) }
        loadToday()
    }

    fun onStopDetection() {
        _uiState.update { it.copy(isDetecting = false) }
    }
}