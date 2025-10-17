package com.example.academiaapp.domain

import com.example.academiaapp.data.remote.AuthApi
import com.example.academiaapp.data.remote.AcademiasApi
import com.example.academiaapp.data.remote.dto.LoginResponse
import com.example.academiaapp.data.remote.dto.LoginRequest
import com.example.academiaapp.data.session.SessionStore
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.HttpException
import java.io.IOException

class LoginRepository(
    private val api: AuthApi,
    private val session: SessionStore,
    private val academiasRepo: AcademiasRepository
) {
    suspend fun login(email: String, password: String): Result<Unit> = try {
        val resp = api.login(LoginRequest(email, password))
        if (resp.ok && resp.tokens != null) {
            session.saveSession(
                access = resp.tokens.access_token,
                refresh = resp.tokens.refresh_token,
                role = resp.role,
                name = resp.name,
                academiaId = resp.academiaId
            )
            // Si viene academiaId, intentar resolver el nombre (no bloqueante para login)
            val academiaId = resp.academiaId
            if (academiaId != null && academiaId > 0) {
                try {
                    academiasRepo.resolveAndCacheAcademiaName(academiaId)
                } catch (_: Throwable) { /* ignora errores aquí */ }
            }
            Result.Success(Unit)
        } else {
            // Backend devolvió 200 pero ok=false: mapear por código/mensaje si está disponible
            val (code, message) = when (val e = resp.error) {
                is String -> null to e
                is Map<*, *> -> (e["code"]?.toString()) to (e["msg"]?.toString() ?: e["message"]?.toString())
                else -> null to null
            }
            val mapped = mapAuthError(code, message, httpStatus = null)
            Result.Error(mapped)
        }
    } catch (t: Throwable) {
        // Distinguir errores HTTP (401/403/423) de errores de red
        if (t is HttpException) {
            val status = t.code()
            val body = try { t.response()?.errorBody()?.string() } catch (_: Throwable) { null }
            val (code, msg) = parseErrorBody(body)
            val mapped = mapAuthError(code, msg, httpStatus = status)
            Result.Error(mapped, t)
        } else if (t is IOException) {
            Result.Error("No se pudo conectar. Revisa tu red.", t)
        } else {
            Result.Error("No se pudo conectar. Revisa tu red.", t)
        }
    }

    private fun parseErrorBody(body: String?): Pair<String?, String?> {
        if (body.isNullOrBlank()) return null to null
        return try {
            val root = JsonParser.parseString(body).asJsonObject
            // Posibles formas: { error: { code, msg } } | { code, msg } | { error: "..." }
            if (root.has("error") && root.get("error").isJsonObject) {
                val err = root.getAsJsonObject("error")
                (err.get("code")?.asString) to (err.get("msg")?.asString ?: err.get("message")?.asString)
            } else if (root.has("code") || root.has("msg") || root.has("message")) {
                (root.get("code")?.asString) to (root.get("msg")?.asString ?: root.get("message")?.asString)
            } else if (root.has("error") && root.get("error").isJsonPrimitive) {
                null to root.get("error").asString
            } else null to null
        } catch (_: Throwable) {
            null to null
        }
    }

    private fun mapAuthError(code: String?, message: String?, httpStatus: Int?): String {
        val c = code?.lowercase()?.trim()
        val m = message?.lowercase()?.trim()

        // Usuario bloqueado por política: status 423 o código específico
        if (httpStatus == 423 || c == "user_blocked" || c == "blocked" || (m?.contains("bloquead") == true) || (m?.contains("blocked") == true)) {
            return "Credenciales inválidas. Usuario bloqueado"
        }
        // Credenciales inválidas: 401/403 o código
        if (httpStatus == 401 || httpStatus == 403 || c == "invalid_credentials" || (m?.contains("credenciales") == true)) {
            return "Credenciales inválidas"
        }
        // Fallback
        return "Credenciales inválidas"
    }
}
