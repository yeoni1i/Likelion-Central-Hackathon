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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

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
    val deviceId: Long? = null,
    val pairingCode: String = "------",
    val currentStatus: ScratchStatus = ScratchStatus.STABLE,
    val totalScratchSecondsToday: Int = 0,
    val weatherData: WeatherResponse? = null,
    val guideList: List<GuideMessage> = emptyList(),
    val timelineItems: List<TimelineUiItem> = emptyList(),
    val errorMessage: String? = null
)

class HomeViewModel(
    val userId: Long,
    private val childId: Long,
    initialDeviceConnected: Boolean = false,
    initialDeviceId: Long? = null
) : ViewModel() {

    private var pairingPollingJob: Job? = null
    private var detectionPollingJob: Job? = null

    private val _uiState = MutableStateFlow(
        HomeUiState(
            isDeviceConnected = initialDeviceConnected || initialDeviceId != null,
            deviceId = initialDeviceId,
            guideList = generateWeatherGuides(null)
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun skipPairingForTest() {
        pairingPollingJob?.cancel()
        _uiState.update {
            it.copy(
                isDeviceConnected = true,
                isDetecting = false,
                deviceId = 1L,
                errorMessage = null
            )
        }
        loadToday()
    }

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

                startPairingStatusPolling()
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onDeviceConnected() {
        _uiState.update { it.copy(isDeviceConnected = true, isDetecting = false) }
        loadToday()
    }

    fun startPairingStatusPolling() {
        pairingPollingJob?.cancel()

        pairingPollingJob = viewModelScope.launch {
            while (true) {
                val code = _uiState.value.pairingCode

                if (code == "------") {
                    delay(2000)
                    continue
                }

                try {
                    val response = RetrofitClient.api.getPairingStatus(code)

                    if (response.paired && response.deviceId != null) {
                        _uiState.update {
                            it.copy(
                                isDeviceConnected = true,
                                isDetecting = false,
                                deviceId = response.deviceId,
                                errorMessage = null
                            )
                        }

                        loadToday()
                        break
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                delay(2000)
            }
        }
    }

    fun loadToday() {
        loadDate(LocalDate.now())
    }

    fun loadDate(date: LocalDate) {

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val selectedDate =
                    date.format(DateTimeFormatter.ISO_LOCAL_DATE)

                val dailyReport = RetrofitClient.api.getDailyScratchReport(
                    userId = userId,
                    date = selectedDate
                )

                val timeline = RetrofitClient.api.getScratchTimeline(
                    userId = userId,
                    date = selectedDate
                )

                val currentStatus = ScratchStatus.fromIntensity(dailyReport.averageIntensity?.toString())
                val totalSeconds = dailyReport.totalSeconds.toString().toDoubleOrNull()?.toInt() ?: 0

                val items = timeline.events.map { event: ScratchTimelineItem ->
                    val zoned = Instant.parse(event.startTs.toString()).atZone(ZoneId.of("Asia/Seoul"))
                    val hour = zoned.hour
                    val endZoned = zoned.plusSeconds(
                        event.durationSec.toLong()
                    )
                    val ampm = if (hour < 12) "AM" else "PM"
                    val hour12 = when {
                        hour == 0 -> 12
                        hour > 12 -> hour - 12
                        else -> hour
                    }

                    val durationSeconds = event.durationSec.toInt()

                    TimelineUiItem(
                        hourLabel = "${"%02d".format(hour12)}:00\n$ampm",
                        status = ScratchStatus.fromIntensity(event.intensity),
                        durationLabel = "${durationSeconds}초",
                        timeRangeLabel = "%02d:%02d~%02d:%02d".format(
                            zoned.hour,
                            zoned.minute,
                            endZoned.hour,
                            endZoned.minute
                        )
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

    // 4. 날씨 정보 조회 (인터셉터가 토큰을 자동 처리하므로 위도/경도만 전달)
    fun fetchWeather(lat: Double = 37.5665, lon: Double = 126.9780) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getWeather(
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

    private fun startCurrentDetectionPolling() {
        detectionPollingJob?.cancel()

        detectionPollingJob = viewModelScope.launch {
            while (_uiState.value.isDetecting) {
                val deviceId = _uiState.value.deviceId ?: break

                try {
                    val response = RetrofitClient.api.getCurrentDetection(deviceId)

                    val scratchStatus = when (response.scratchStatus) {
                        "NORMAL" -> ScratchStatus.NORMAL
                        "WARNING" -> ScratchStatus.WARNING
                        "DANGER" -> ScratchStatus.DANGER
                        "VERY_DANGER" -> ScratchStatus.VERY_DANGER
                        else -> ScratchStatus.STABLE
                    }

                    _uiState.update {
                        it.copy(currentStatus = scratchStatus)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                delay(2000)
            }
        }
    }

    fun onStartDetection() {
        val deviceId = _uiState.value.deviceId ?: run {
            println("DETECTION_MOBILE: deviceId=NULL")
            _uiState.update {
                it.copy(errorMessage = "연결된 워치가 없습니다.")
            }
            return
        }

        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.startDetection(deviceId)
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isDetecting = true,
                            currentStatus = ScratchStatus.STABLE,
                            errorMessage = null
                        )
                    }
                    startCurrentDetectionPolling()
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "감지 시작 실패 (${response.code()})")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "감지 시작 요청에 실패했습니다.")
                }
            }
        }
    }

    fun onStopDetection() {
        val deviceId = _uiState.value.deviceId ?: return

        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.stopDetection(deviceId)
                if (response.isSuccessful) {
                    detectionPollingJob?.cancel()
                    _uiState.update {
                        it.copy(
                            isDetecting = false,
                            currentStatus = ScratchStatus.STABLE,
                            errorMessage = null
                        )
                    }
                    loadToday()
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "감지 종료 실패 (${response.code()})")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "감지 종료 요청 실패")
                }
            }
        }
    }
}