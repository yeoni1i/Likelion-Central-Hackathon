package com.example.atocuemobile.network

import com.example.atocuemobile.network.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/v1/daily-logs")
    suspend fun createDailyLog(@Body request: DailyLogCreateRequest): Response<DailyLogResponse>

    @Multipart
    @POST("api/v1/daily-logs/image")
    suspend fun createDailyLogWithImage(
        @Part image: MultipartBody.Part?,
        @Part("request") request: RequestBody
    ): Response<DailyLogResponse>

    @GET("api/v1/daily-logs/{date}")
    suspend fun getDailyLogByDate(@Path("date") date: String): Response<DailyLogResponse>

    // ✅ 경로 수정: scratch/ → analysis/
    @GET("analysis/reports/daily")
    suspend fun getDailyScratchReport(
        @Header("X-User-Id") userId: Long = 1L,
        @Query("date") date: String,
        @Query("timezone") timezone: String = "Asia/Seoul"
    ): Response<DailyScratchResponse>

    // ✅ 신규: 주간 추이
    @GET("analysis/reports/weekly")
    suspend fun getWeeklyScratchReport(
        @Header("X-User-Id") userId: Long = 1L,
        @Query("date") date: String,
        @Query("timezone") timezone: String = "Asia/Seoul"
    ): Response<WeeklyScratchResponse>

    // ✅ 신규: AI 원인 분석 리포트 (백엔드 미구현 — 요청해야 함)
    @GET("analysis/daily")
    suspend fun getDailyAiReport(
        @Header("X-User-Id") userId: Long = 1L,
        @Query("date") date: String
    ): Response<DailyAiReportResponse>

    // ✅ 신규: 위험 식단 리스트 (백엔드 미구현 — 요청해야 함, 경로는 제안)
    @GET("analysis/reports/risk-foods")
    suspend fun getRiskFoodList(
        @Header("X-User-Id") userId: Long = 1L,
        @Query("baseDate") baseDate: String,
        @Query("days") days: Int = 30
    ): Response<RiskFoodListResponse>
}