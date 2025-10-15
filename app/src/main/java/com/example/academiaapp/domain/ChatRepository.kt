package com.example.academiaapp.domain

import com.example.academiaapp.data.remote.ChatApi
import com.example.academiaapp.data.remote.dto.ChatRequest
import com.example.academiaapp.data.remote.dto.Envelope
import com.example.academiaapp.data.remote.dto.GenericItem
import com.example.academiaapp.data.session.SessionStore
import kotlinx.coroutines.flow.first

class ChatRepository(
    private val api: ChatApi,
    private val session: SessionStore
) {
    private suspend fun authHeaders(): Map<String, String> {
        val token = session.accessToken.first()
        return if (!token.isNullOrBlank()) mapOf("Authorization" to "Bearer $token") else emptyMap()
    }

    suspend fun welcome(): Result<Envelope<GenericItem>> = runCatching {
        api.chat(ChatRequest(query = ""), headers = authHeaders())
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error("No se pudo obtener la bienvenida", it) }
    )

    suspend fun sendMessage(text: String): Result<Envelope<GenericItem>> = runCatching {
        api.chat(ChatRequest(query = text), headers = authHeaders())
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error("No se pudo enviar el mensaje", it) }
    )
}
