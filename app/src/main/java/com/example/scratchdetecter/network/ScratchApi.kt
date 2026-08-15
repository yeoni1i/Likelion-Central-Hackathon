package com.example.scratchdetecter.network

import com.example.scratchdetecter.network.dto.PairDeviceRequest
import com.example.scratchdetecter.network.dto.PairDeviceResponse
import com.example.scratchdetecter.network.dto.ScratchIngestRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ScratchApi {

    @POST("scratch/ingest/scratch-events")
    suspend fun sendScratchEvents(
        @Header("Idempotency-Key") idempotencyKey: String,
        @Header("X-Backfill") backfill: Boolean,
        @Body request: ScratchIngestRequest
    ): Response<Unit>

    @POST("devices/pair")
    suspend fun pairDevice(
        @Body request: PairDeviceRequest
    ): Response<PairDeviceResponse>
}