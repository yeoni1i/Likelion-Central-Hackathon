package com.example.scratchdetecter.network

import com.example.scratchdetecter.network.dto.PairDeviceRequest
import com.example.scratchdetecter.network.dto.PairDeviceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DevicePairingApi {
    @POST("devices/pair")
    suspend fun pairDevice(
        @Body request: PairDeviceRequest
    ): Response<PairDeviceResponse>
}
