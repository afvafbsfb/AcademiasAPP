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
    val academiaId: Int? = null,
    val must_change_password: Boolean = false,
    val error: Any? = null
)

data class ChangePasswordRequest(
    val current_password: String,
    val new_password: String
)

data class ChangePasswordResponse(
    val ok: Boolean,
    val message: String? = null,
    val error: String? = null
)
