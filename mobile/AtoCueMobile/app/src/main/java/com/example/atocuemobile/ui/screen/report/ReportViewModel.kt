package com.example.atocuemobile.ui.screen.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atocuemobile.network.RetrofitClient
import com.example.atocuemobile.network.dto.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ReportBundle(
    val daily: DailyScratchResponse,
    val weekly: WeeklyScratchResponse?,      // null이면 컴포넌트가 더미 유지
    val aiReport: DailyAiReportResponse?,    // null이면 컴포넌트가 더미 유지
    val riskFoods: RiskFoodListResponse?     // null이면 컴포넌트가 더미 유지
)

sealed interface ReportUiState {
    object Loading : ReportUiState
    data class Success(val report: ReportBundle) : ReportUiState
    data class Error(val message: String) : ReportUiState
}

class ReportViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Loading)
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    private var currentDate : LocalDate = LocalDate.now()

    init { fetchDailyReport() }

    fun fetchDailyReport(
        userId: Long = 1L,
        date: String = currentDate.toString()
    ) {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading
            try {
                // 필수: daily는 실패하면 화면 전체 에러 처리
                val dailyDeferred = async {
                    RetrofitClient.apiService.getDailyScratchReport(userId, date)
                }

                // 선택: weekly/ai/riskFoods는 실패해도 null로 넘어감
                val weeklyDeferred = async {
                    runCatching { RetrofitClient.apiService.getWeeklyScratchReport(userId, date) }
                        .getOrNull()?.takeIf { it.isSuccessful }?.body()
                }
                val aiReportDeferred = async {
                    runCatching { RetrofitClient.apiService.getDailyAiReport(userId, date) }
                        .getOrNull()?.takeIf { it.isSuccessful }?.body()
                }
                val riskFoodsDeferred = async {
                    runCatching { RetrofitClient.apiService.getRiskFoodList(userId, date) }
                        .getOrNull()?.takeIf { it.isSuccessful }?.body()
                }

                val dailyResponse = dailyDeferred.await()
                if (dailyResponse.isSuccessful && dailyResponse.body() != null) {
                    _uiState.value = ReportUiState.Success(
                        ReportBundle(
                            daily = dailyResponse.body()!!,
                            weekly = weeklyDeferred.await(),
                            aiReport = aiReportDeferred.await(),
                            riskFoods = riskFoodsDeferred.await()
                        )
                    )
                } else {
                    _uiState.value = ReportUiState.Error("리포트 조회 실패 (코드: ${dailyResponse.code()})")
                }
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error("네트워크 오류: ${e.localizedMessage ?: "알 수 없는 오류"}")
            }
        }
    }
    fun goToPreviousDay() {
        currentDate = currentDate.minusDays(1)
        fetchDailyReport(date = currentDate.toString())
    }

    fun goToNextDay() {
        val today = LocalDate.now()
        if (currentDate.isBefore(today)) {   // 미래 날짜로는 못 가게 방어
            currentDate = currentDate.plusDays(1)
            fetchDailyReport(date = currentDate.toString())
        }
    }
}