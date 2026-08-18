package com.example.atocuemobile.network

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
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AtoCueApiService {

    // 1. 회원가입 & 로그인
    @POST("accounts/signup_account")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): Response<ResponseBody>

    @POST("accounts/")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

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
    ): Response<ResponseBody>

    // 3. 워치 페어링 (토큰은 RetrofitClient 인터셉터가 자동 주입)
    @POST("devices/pairing-codes")
    suspend fun createPairingCode(
        @Header("Authorization") token: String,
        @Query("childId") childId: Long
    ): PairingCodeResponse

    @POST("devices/pair")
    suspend fun pairDevice(
        @Body request: PairDeviceRequest
    ): PairDeviceResponse

    // 4. 긁음 데이터 통계 & 이벤트 (기존 테스트 파일 호환용 getScratchEvents 포함)
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

    @GET("scratch/events")
    suspend fun getScratchEvents(
        @Header("X-User-Id") userId: Long,
        @Query("date") date: String,
        @Query("timezone") timezone: String = "Asia/Seoul"
    ): ScratchTimelineResponse

    // 5. 날씨 API
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): WeatherResponse
}