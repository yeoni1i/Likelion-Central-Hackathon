package com.example.scratchdetecter.network

import com.example.scratchdetecter.network.dto.DetectionStatusResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DeviceDetectionApi {

    @GET("devices/{deviceId}/detection/status")
    suspend fun getDetectionStatus(
        @Path("deviceId") deviceId: Long
    ): Response<DetectionStatusResponse>
}