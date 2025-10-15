package com.example.academiaapp.data.remote

import com.example.academiaapp.core.EnvConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface TokenProvider {
    suspend fun getAccessToken(): String?
}

class AuthInterceptor(private val tokenProvider: TokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val original = chain.request()
        val builder = original.newBuilder()
        // We can't call suspend here; for simplicity, skip adding header automatically.
        // We'll add Authorization per-request when needed.
        return chain.proceed(builder.build())
    }
}

object NetworkModule {
    fun createOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .callTimeout(90, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

    fun createRetrofit(baseUrl: String, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith('/')) baseUrl else "$baseUrl/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    fun createAuthApi(client: OkHttpClient): AuthApi =
        createRetrofit(EnvConfig.apiWorkerBaseUrl, client).create(AuthApi::class.java)

    fun createChatApi(client: OkHttpClient): ChatApi =
        createRetrofit(EnvConfig.chatBaseUrl, client).create(ChatApi::class.java)
}
