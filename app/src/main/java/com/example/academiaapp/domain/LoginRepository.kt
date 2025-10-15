package com.example.academiaapp.domain

import com.example.academiaapp.data.remote.AuthApi
import com.example.academiaapp.data.remote.dto.LoginRequest
import com.example.academiaapp.data.session.SessionStore

class LoginRepository(
    private val api: AuthApi,
    private val session: SessionStore
) {
    suspend fun login(email: String, password: String): Result<Unit> = try {
        val resp = api.login(LoginRequest(email, password))
        if (resp.ok && resp.tokens != null) {
            session.saveSession(
                access = resp.tokens.access_token,
                refresh = resp.tokens.refresh_token,
                role = resp.role,
                name = resp.name
            )
            Result.Success(Unit)
        } else {
            val msg = when (val e = resp.error) {
                is String -> e
                is Map<*, *> -> e["msg"]?.toString() ?: "Login error"
                else -> "Login error"
            }
            Result.Error(msg)
        }
    } catch (t: Throwable) {
        Result.Error("No se pudo conectar. Revisa tu red.", t)
    }
}
