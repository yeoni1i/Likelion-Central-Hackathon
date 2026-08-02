package com.example.atocuemobile.network

import com.example.atocuemobile.network.dto.DailyScratchResponse
import com.example.atocuemobile.network.dto.PairingCodeResponse
import com.example.atocuemobile.network.dto.ScratchTimelineResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    //하루 데이터 조회
    @GET("scratch/events")
    suspend fun getScratchEvents(
        @Header("X-User-Id") userId: Long,
        @Query("date") date: String,
        @Query("timezone") timezone: String = "Asia/Seoul"
    ): ScratchTimelineResponse

    //워치 등록 api
    @POST("devices/pairing-codes")
    suspend fun createPairingCode(
        @Query("parentUserId") parentUserId: Long
    ): PairingCodeResponse

    // 긁음 일일 통계 조회 API
    @GET("scratch/reports/daily")
    suspend fun getDailyScratch(
        @Header("X-User-Id") userId: Long,
        @Query("date") date: String,
        @Query("timezone") timezone: String = "Asia/Seoul"
    ): DailyScratchResponse
}