package com.example.atocuemobile.network

import com.example.atocuemobile.network.dto.DailyScratchResponse
import com.example.atocuemobile.network.dto.LoginRequest
import com.example.atocuemobile.network.dto.LoginResponse
import com.example.atocuemobile.network.dto.PairDeviceRequest
import com.example.atocuemobile.network.dto.PairDeviceResponse
import com.example.atocuemobile.network.dto.PairingCodeResponse
import com.example.atocuemobile.network.dto.ScratchTimelineResponse
import com.example.atocuemobile.network.dto.SignUpRequest
import com.example.atocuemobile.network.dto.WeatherResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AtoCueApiService {

    // 1. 회원가입 & 로그인
    @POST("api/users/signup")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): Any

    @POST("api/users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    // 2. 워치 페어링
    @POST("devices/pairing-codes")
    suspend fun createPairingCode(
        @Query("parentUserId") parentUserId: Long
    ): PairingCodeResponse

    @POST("devices/pair")
    suspend fun pairDevice(
        @Body request: PairDeviceRequest
    ): PairDeviceResponse

    // 3. 긁음(Scratch) 데이터 조회
    @GET("scratch/reports/daily")
    suspend fun getDailyScratchReport(
        @Header("X-User-Id") userId: Long,
        @Query("date") date: String,
        @Query("timezone") timezone: String = "Asia/Seoul"
    ): DailyScratchResponse

    @GET("scratch/events")
    suspend fun getScratchTimeline(
        @Header("X-User-Id") userId: Long,
        @Query("date") date: String,
        @Query("timezone") timezone: String = "Asia/Seoul"
    ): ScratchTimelineResponse

    // ScratchTestScreen용 이벤트 조회 함수 (반환형을 ScratchTimelineResponse로 지정)
    @GET("scratch/events")
    suspend fun getScratchEvents(
        @Header("X-User-Id") userId: Long,
        @Query("date") date: String,
        @Query("timezone") timezone: String = "Asia/Seoul"
    ): ScratchTimelineResponse

    // 4. 날씨
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Header("Authorization") token: String? = null
    ): WeatherResponse
}