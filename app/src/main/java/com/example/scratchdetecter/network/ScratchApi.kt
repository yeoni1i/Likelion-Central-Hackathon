package com.example.scratchdetecter.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ScratchApi {

    @POST("v1/ingest/scratch-events")
    suspend fun sendScratchEvents(
        @Header("X-User-Id")
        userId: Long,

        @Header("Idempotency-Key")
        idempotencyKey: String,

        @Header("X-Backfill")
        backfill: Boolean = false,

        @Body
        request: ScratchIngestRequest
    ): Response<ScratchIngestResponse>

    @POST("devices/pair")
    suspend fun pairDevice(
        @Body request: PairDeviceRequest
    ): Response<PairDeviceResponse>
}