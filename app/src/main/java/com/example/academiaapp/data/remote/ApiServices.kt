package com.example.academiaapp.data.remote

import com.example.academiaapp.data.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {
    @POST("/auth/login")
    suspend fun login(@Body req: LoginRequest): LoginResponse
}

interface ChatApi {
    @POST("/chat")
    suspend fun chat(@Body req: ChatPayload, @HeaderMap headers: Map<String, String> = emptyMap()): Envelope<GenericItem>
}

interface AcademiasApi {
    @GET("/academias/{id}")
    suspend fun getAcademia(
        @Path("id") id: Int,
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): AcademiaResponse
}

interface UsuariosApi {
    @PUT("/usuarios/{userId}/credentials")
    suspend fun changePassword(
        @Path("userId") userId: String,
        @Body req: ChangePasswordRequest,
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): ChangePasswordResponse
}
