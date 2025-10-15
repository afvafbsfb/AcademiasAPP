package com.example.academiaapp.data.remote.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class TokensDto(
    val access_token: String,
    val refresh_token: String
)

data class LoginResponse(
    val ok: Boolean,
    val tokens: TokensDto?,
    val role: String?,
    val name: String?,
    val error: Any? = null
)
