package com.example.scratchdetecter.network

import com.example.scratchdetecter.network.dto.PairDeviceRequest
import com.example.scratchdetecter.network.dto.PairDeviceResponse
import com.example.scratchdetecter.network.dto.ScratchIngestRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ScratchApi {
    // 백엔드 ScratchController의 실제 @PostMapping 경로와 다르면 이 문자열만 수정한다.
    @POST("scratch/events")
    suspend fun sendScratchEvents(
        @Header("X-User-Id") userId: Long,
        @Header("Idempotency-Key") idempotencyKey: String,
        @Header("X-Backfill") backfill: Boolean,
        @Body request: ScratchIngestRequest
    ): Response<Unit>

    @POST("devices/pair")
    suspend fun pairDevice(
        @Body request: PairDeviceRequest
    ): Response<PairDeviceResponse>
}
