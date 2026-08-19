package com.example.atocuemobile.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import com.example.atocuemobile.network.dto.DailyScratchResponse
import com.example.atocuemobile.network.dto.ScratchTimelineResponse

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
    @GET("api/weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): WeatherResponse
}

// 💡 현재 브랜치에 없는 DTO 클래스들의 임시 더미(Dummy) 선언
// 내 브랜치의 프로젝트 컴파일을 위한 용도이며, 추후 메인에 합칠 때 진짜 DTO 파일로 자동 대체됩니다.
class SignUpRequest
class LoginRequest
class LoginResponse
class ParentInfoRequest
class OnboardingRequest
class ChildRegistrationResponse
class PairingCodeResponse
class PairDeviceRequest
class PairDeviceResponse
class PairingStatusResponse
class DetectionStatusResponse
class CurrentDetectionResponse

class WeatherResponse