package com.example.atocuemobile.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://192.168.45.39:8080/"

    var accessToken: String? = null

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()

        accessToken?.let { token ->
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            builder.header("Authorization", formattedToken)
            Log.d("AtoCue_Network", "➡️ [요청] ${originalRequest.method()} ${originalRequest.url()}")
        } ?: run {
            Log.w("AtoCue_Network", "⚠️ [경고] accessToken이 null인 상태로 요청 전송: ${originalRequest.url()}")
        }

        val response = chain.proceed(builder.build())

        // 👈 서버가 내려준 실제 응답 본문(에러 메시지 등) 출력
        val responseBodyString = runCatching {
            response.peekBody(4096).string()
        }.getOrDefault("응답 본문 없음")

        Log.d("AtoCue_Network", "⬅️ [응답] 코드: ${response.code()} / URL: ${originalRequest.url()}")
        Log.d("AtoCue_Network", "📄 [응답 본문]: $responseBodyString")

        response
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: AtoCueApiService by lazy {
        retrofit.create(AtoCueApiService::class.java)
    }
}