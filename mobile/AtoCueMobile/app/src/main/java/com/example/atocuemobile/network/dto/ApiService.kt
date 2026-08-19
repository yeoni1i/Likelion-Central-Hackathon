package com.example.atocuemobile.network

import com.example.atocuemobile.network.dto.DailyLogCreateRequest
import com.example.atocuemobile.network.dto.DailyLogResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // 1. 일반 텍스트 데이터 기록 (사진 없음)
    @POST("api/v1/daily-logs")
    suspend fun createDailyLog(
        @Body request: DailyLogCreateRequest
    ): Response<DailyLogResponse>

    // 2. 사진이 포함된 기록 (Multipart)
    @Multipart
    @POST("api/v1/daily-logs/image")
    suspend fun createDailyLogWithImage(
        @Part image: MultipartBody.Part?,
        @Part("request") request: RequestBody
    ): Response<DailyLogResponse>

    // 3. 기록 조회 (필요 시 활용)
    @GET("api/v1/daily-logs/{date}")
    suspend fun getDailyLogByDate(
        @Path("date") date: String
    ): Response<DailyLogResponse>
}