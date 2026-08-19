package com.example.atocuemobile.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// 💡 만약 빨간 줄이 남는다면 아래 import 문의 주석(//)을 해제하거나 Option+Enter로 임포트하세요.
// import com.example.atocuemobile.network.AtoCueApiService

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    var accessToken: String? = null

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        // OkHttp 4.x 프로퍼티 문법 적용 (.url() -> .url, .encodedPath() -> .encodedPath)
        val urlPath = originalRequest.url.encodedPath
        val builder = originalRequest.newBuilder()

        val isPublicApi = urlPath.contains("/accounts") || urlPath.contains("/login")

        accessToken?.let { token ->
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            if (originalRequest.header("Authorization") == null) {
                builder.header("Authorization", formattedToken)
            }
            Log.d("AtoCue_Network", "➡️ [요청] ${originalRequest.method} ${originalRequest.url} / Token: $formattedToken")
        } ?: run {
            if (!isPublicApi) {
                Log.w("AtoCue_Network", "⚠️ [경고] accessToken이 null인 상태로 인증 필요 요청 전송: ${originalRequest.url}")
            } else {
                Log.d("AtoCue_Network", "➡️ [공개 요청] ${originalRequest.method} ${originalRequest.url}")
            }
        }

        val response = chain.proceed(builder.build())

        val responseBodyString = runCatching {
            response.peekBody(4096).string()
        }.getOrDefault("응답 본문 없음")

        Log.d("AtoCue_Network", "⬅️ [응답] 코드: ${response.code} / URL: ${originalRequest.url}")
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

    // 1. 기존 팀원들이 사용하는 API 인터페이스
    val api: AtoCueApiService by lazy {
        retrofit.create(AtoCueApiService::class.java)
    }

    // 2. 일상/식단 기록 연동용 새 API 인터페이스
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}