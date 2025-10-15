package com.example.academiaapp.data.remote

import com.example.academiaapp.data.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/login")
    suspend fun login(@Body req: LoginRequest): LoginResponse
}

interface ChatApi {
    @POST("/chat")
    suspend fun chat(@Body req: ChatPayload, @HeaderMap headers: Map<String, String> = emptyMap()): Envelope<GenericItem>
}
