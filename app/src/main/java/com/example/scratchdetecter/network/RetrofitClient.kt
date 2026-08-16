package com.example.scratchdetecter.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    /*
     * 실제 워치에서는 10.0.2.2가 아니라 PC/서버의 같은 Wi-Fi IPv4 주소를 사용한다.
     * 기존에 정상 동작하던 BASE_URL이 있다면 그 값을 그대로 유지한다.
     */
    private const val BASE_URL = "http://192.168.45.39:8080/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val scratchApi: ScratchApi = retrofit.create(ScratchApi::class.java)
    val devicePairingApi: DevicePairingApi = retrofit.create(DevicePairingApi::class.java)
}
