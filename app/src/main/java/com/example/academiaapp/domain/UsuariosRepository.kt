package com.example.academiaapp.domain

import com.example.academiaapp.data.remote.UsuariosApi
import com.example.academiaapp.data.remote.dto.ChangePasswordRequest
import com.example.academiaapp.data.session.SessionStore
import retrofit2.HttpException
import java.io.IOException

class UsuariosRepository(
    private val api: UsuariosApi,
    private val session: SessionStore
) {
    /**
     * Cambiar contraseña del usuario actual.
     * Usa "me" como userId para referirse al usuario autenticado.
     */
    suspend fun changeMyPassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> = changePassword("me", currentPassword, newPassword)
    
    private suspend fun changePassword(
        userIdOrMe: String,
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            val token = session.getAccessToken()
            if (token.isNullOrBlank()) {
                return Result.Error("No hay sesión activa")
            }

            val headers = mapOf("Authorization" to "Bearer $token")
            val request = ChangePasswordRequest(
                current_password = currentPassword,
                new_password = newPassword
            )

            val resp = api.changePassword(userIdOrMe, request, headers)

            if (resp.ok) {
                Result.Success(Unit)
            } else {
                // El backend ya devuelve mensajes en castellano, usarlos directamente
                val errorMsg = resp.message ?: when (resp.error) {
                    "invalid_credentials" -> "La contraseña actual es incorrecta"
                    "validation_error" -> "Error de validación en la contraseña"
                    "missing_fields" -> "Faltan campos requeridos"
                    else -> "Error al cambiar la contraseña"
                }
                Result.Error(errorMsg)
            }
        } catch (t: Throwable) {
            if (t is HttpException) {
                val status = t.code()
                // Intentar parsear el mensaje del body
                val body = try {
                    t.response()?.errorBody()?.string()
                } catch (_: Throwable) {
                    null
                }

                val errorMsg = if (body != null) {
                    try {
                        val json = org.json.JSONObject(body)
                        json.optString("message", "")
                    } catch (_: Throwable) {
                        null
                    }
                } else {
                    null
                } ?: when (status) {
                    401 -> "La contraseña actual es incorrecta"
                    403 -> "No tienes permiso para cambiar esta contraseña"
                    404 -> "Usuario no encontrado"
                    400 -> "Datos de contraseña inválidos"
                    else -> "Error al cambiar la contraseña"
                }
                Result.Error(errorMsg, t)
            } else if (t is IOException) {
                Result.Error("No se pudo conectar. Revisa tu red.", t)
            } else {
                Result.Error("Error inesperado: ${t.message}", t)
            }
        }
    }
}
