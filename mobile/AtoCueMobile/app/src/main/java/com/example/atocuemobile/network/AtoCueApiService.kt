package com.example.atocuemobile.network

import com.example.atocuemobile.network.dto.ChildRegistrationResponse
import com.example.atocuemobile.network.dto.DailyScratchResponse
import com.example.atocuemobile.network.dto.LoginRequest
import com.example.atocuemobile.network.dto.LoginResponse
import com.example.atocuemobile.network.dto.OnboardingRequest
import com.example.atocuemobile.network.dto.PairDeviceRequest
import com.example.atocuemobile.network.dto.PairDeviceResponse
import com.example.atocuemobile.network.dto.PairingCodeResponse
import com.example.atocuemobile.network.dto.ParentInfoRequest
import com.example.atocuemobile.network.dto.ScratchTimelineResponse
import com.example.atocuemobile.network.dto.SignUpRequest
import com.example.atocuemobile.network.dto.WeatherResponse
import com.example.atocuemobile.network.dto.DetectionStatusResponse
import com.example.atocuemobile.network.dto.CurrentDetectionResponse
import com.example.atocuemobile.network.dto.PairingStatusResponse
import com.example.atocuemobile.network.dto.DailyLogResponse
import com.example.atocuemobile.network.dto.RiskFoodListResponse
import com.example.atocuemobile.network.dto.WeeklyScratchResponse
import com.example.atocuemobile.network.dto.DailyAnalysisReportResponse
import com.example.atocuemobile.network.dto.DailyAnalysisResponse
import com.example.atocuemobile.network.dto.WeeklyAnalysisResponse
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*



interface AtoCueApiService {

    // 1. 회원가입 & 로그인
    @POST("accounts/signup_account")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): Response<ResponseBody>

    @POST("accounts/")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // 2. 온보딩
    @POST("accounts/parent_info")
    suspend fun saveParentInfo(
        @Header("Authorization") token: String,
        @Body request: ParentInfoRequest
    ): Response<ResponseBody>

    @POST("accounts/signup_child")
    suspend fun saveChildInfo(
        @Header("Authorization") token: String,
        @Body request: OnboardingRequest
    ): Response<ChildRegistrationResponse>

    // 3. 워치 페어링
    @POST("devices/pairing-codes")
    suspend fun createPairingCode(
        @Header("Authorization") token: String,
        @Query("childId") childId: Long
    ): PairingCodeResponse

    @POST("devices/pair")
    suspend fun pairDevice(
        @Body request: PairDeviceRequest
    ): PairDeviceResponse

    @GET("devices/pairing-codes/{code}/status")
    suspend fun getPairingStatus(
        @Path("code") code: String
    ): PairingStatusResponse

    @POST("devices/{deviceId}/detection/start")
    suspend fun startDetection(
        @Path("deviceId") deviceId: Long
    ): Response<Unit>

    @POST("devices/{deviceId}/detection/stop")
    suspend fun stopDetection(
        @Path("deviceId") deviceId: Long
    ): Response<Unit>

    @GET("devices/{deviceId}/detection/status")
    suspend fun getDetectionStatus(
        @Path("deviceId") deviceId: Long
    ): DetectionStatusResponse

    @GET("devices/{deviceId}/detection/current")
    suspend fun getCurrentDetection(
        @Path("deviceId") deviceId: Long
    ): CurrentDetectionResponse

    @GET("scratch/events")
    suspend fun getScratchTimeline(
        @Header("X-User-Id") userId: Long,
        @Query("date") date: String,
        @Query("timezone") timezone: String = "Asia/Seoul"
    ): ScratchTimelineResponse

    // 4. 긁음 데이터 통계 & 이벤트
    @GET("scratch/reports/daily")
    suspend fun getDailyScratchReport(
        @Header("X-User-Id") userId: Long,
        @Query("date") date: String,
        @Query("timezone") timezone: String = "Asia/Seoul"
    ): DailyScratchResponse

    // 5. 날씨 API
    @GET("api/weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): WeatherResponse


    //6.일상데이터
    @Multipart
    @POST("daily-logs")
    suspend fun createDailyLog(
        @Part("request") request: RequestBody
    ): DailyLogResponse

    @GET("daily-logs")
    suspend fun getDailyLogs(
        @Query("date") date: String
    ): List<DailyLogResponse>


    // 일간 긁음 통계 조회
    @GET("analysis/reports/daily")
    suspend fun getDailyAnalysis(
        @Header("X-User-Id") userId: Long,
        @Query("date") date: String,
        @Query("timezone") timezone: String = "Asia/Seoul"
    ): Response<DailyAnalysisResponse>

    // 2. 주간 긁음 통계  조회
    @GET("analysis/reports/weekly")
    suspend fun getWeeklyAnalysis(
        @Header("X-User-Id") userId: Long,
        @Query("date") date: String,
        @Query("timezone") timezone: String = "Asia/Seoul"
    ): Response<WeeklyAnalysisResponse>

    // 3. OpenAI 최종 일간 AI 리포트 조회
    @GET("analysis/daily")
    suspend fun getDailyAiReport(
        @Header("X-User-Id") userId: Long,
        @Query("date") date: String
    ): Response<DailyAnalysisReportResponse>

    // ✅ 신규: 위험 식단 리스트 (백엔드 미구현 — 요청해야 함, 경로는 제안)
    @GET("analysis/reports/risk-foods")
    suspend fun getRiskFoodList(
        @Header("X-User-Id") userId: Long = 1L,
        @Query("baseDate") baseDate: String,
        @Query("days") days: Int = 30
    ): Response<RiskFoodListResponse>
}

