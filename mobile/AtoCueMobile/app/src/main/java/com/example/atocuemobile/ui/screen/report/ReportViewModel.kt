package com.example.atocuemobile.ui.screen.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atocuemobile.network.RetrofitClient
import com.example.atocuemobile.network.dto.DailyAnalysisReportResponse
import com.example.atocuemobile.network.dto.DailyAnalysisResponse
import com.example.atocuemobile.network.dto.WeeklyAnalysisResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

data class ReportBundle(
    val dailyAnalysis: DailyAnalysisResponse,
    val weeklyAnalysis: WeeklyAnalysisResponse? = null,
    val aiReport: DailyAnalysisReportResponse? = null
)

sealed interface ReportUiState {
    object Loading : ReportUiState

    data class Success(
        val report: ReportBundle
    ) : ReportUiState

    data class Error(
        val message: String
    ) : ReportUiState
}

class ReportViewModel(
    private val userId: Long
) : ViewModel() {

    private val koreaZone = ZoneId.of("Asia/Seoul")

    private var currentDate: LocalDate =
        LocalDate.now(koreaZone)

    private val _uiState =
        MutableStateFlow<ReportUiState>(
            ReportUiState.Loading
        )

    val uiState: StateFlow<ReportUiState> =
        _uiState.asStateFlow()

    init {
        fetchDailyReport()
    }

    fun fetchDailyReport(
        date: String = currentDate.toString()
    ) {

        viewModelScope.launch {

            _uiState.value =
                ReportUiState.Loading

            try {

                // 1. 일간 긁음 통계
                val dailyDeferred = async {

                    RetrofitClient.apiService
                        .getDailyAnalysis(
                            userId = userId,
                            date = date,
                            timezone = "Asia/Seoul"
                        )
                }

                // 2. 주간 긁음 통계
                val weeklyDeferred = async {

                    RetrofitClient.apiService
                        .getWeeklyAnalysis(
                            userId = userId,
                            date = date,
                            timezone = "Asia/Seoul"
                        )
                }

                // 3. OpenAI 최종 일간 리포트
                val aiDeferred = async {

                    RetrofitClient.apiService
                        .getDailyAiReport(
                            userId = userId,
                            date = date
                        )
                }

                // 세 요청을 병렬로 실행
                val dailyResponse =
                    dailyDeferred.await()

                val weeklyResponse =
                    weeklyDeferred.await()

                val aiResponse =
                    aiDeferred.await()

                if (
                    dailyResponse.isSuccessful &&
                    dailyResponse.body() != null
                ) {

                    val daily =
                        dailyResponse.body()!!

                    val weekly =
                        if (weeklyResponse.isSuccessful) {
                            weeklyResponse.body()
                        } else {
                            null
                        }

                    val aiReport =
                        if (aiResponse.isSuccessful) {
                            aiResponse.body()
                        } else {
                            null
                        }

                    _uiState.value =
                        ReportUiState.Success(
                            ReportBundle(
                                dailyAnalysis = daily,
                                weeklyAnalysis = weekly,
                                aiReport = aiReport
                            )
                        )

                } else {

                    _uiState.value =
                        ReportUiState.Error(
                            "리포트 조회 실패 " +
                                    "(코드: ${dailyResponse.code()})"
                        )
                }

            } catch (e: Exception) {

                e.printStackTrace()

                _uiState.value =
                    ReportUiState.Error(
                        "네트워크 오류: " +
                                (e.localizedMessage
                                    ?: "알 수 없는 오류")
                    )
            }
        }
    }

    fun goToPreviousDay() {

        currentDate =
            currentDate.minusDays(1)

        fetchDailyReport(
            date = currentDate.toString()
        )
    }

    fun goToNextDay() {

        val today =
            LocalDate.now(koreaZone)

        if (currentDate.isBefore(today)) {

            currentDate =
                currentDate.plusDays(1)

            fetchDailyReport(
                date = currentDate.toString()
            )
        }
    }
}