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
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Path

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
    ): Response<ChildRegistrationResponse>

    // 3. 워치 페어링 (백엔드 DevicePairingController에 맞춰 childId를 쿼리로 전달)
    @POST("devices/pairing-codes")
    suspend fun createPairingCode(
        @Header("Authorization") token: String,
        @Query("childId") childId: Long
    ): PairingCodeResponse

    @POST("devices/pair")
    suspend fun pairDevice(
        @Body request: PairDeviceRequest
    ): PairDeviceResponse

    // 모바일이 워치 페어링 완료 여부 확인
    @GET("devices/pairing-codes/{code}/status")
    suspend fun getPairingStatus(
        @Path("code") code: String
    ): PairingStatusResponse

    // 워치 감지 시작
    @POST("devices/{deviceId}/detection/start")
    suspend fun startDetection(
        @Path("deviceId") deviceId: Long
    ): Response<Unit>

    // 워치 감지 중지
    @POST("devices/{deviceId}/detection/stop")
    suspend fun stopDetection(
        @Path("deviceId") deviceId: Long
    ): Response<Unit>

    // 워치가 START / STOP 상태 확인
    @GET("devices/{deviceId}/detection/status")
    suspend fun getDetectionStatus(
        @Path("deviceId") deviceId: Long
    ): DetectionStatusResponse

    // 모바일이 현재 긁음 상태 확인
    @GET("devices/{deviceId}/detection/current")
    suspend fun getCurrentDetection(
        @Path("deviceId") deviceId: Long
    ): CurrentDetectionResponse


    // 4. 긁음 데이터 통계 & 이벤트
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

    // 5. 날씨 API
    @GET("weather")
    suspend fun getWeather(
        @Header("Authorization") token: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): WeatherResponse
}