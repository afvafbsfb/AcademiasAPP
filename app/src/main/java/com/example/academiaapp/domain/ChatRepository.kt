package com.example.academiaapp.domain

import com.example.academiaapp.data.remote.ChatApi
import com.example.academiaapp.data.remote.dto.ChatPayload
import com.example.academiaapp.data.remote.dto.ChatMessageDto
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
        val userName = session.name.first().orEmpty().trim()
        val content = if (userName.isNotEmpty()) "hola, soy $userName" else "hola"
        val payload = ChatPayload(messages = listOf(ChatMessageDto(role = "user", content = content)))
        api.chat(payload, headers = authHeaders())
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error("No se pudo obtener la bienvenida", it) }
    )

    suspend fun sendMessage(text: String): Result<Envelope<GenericItem>> = runCatching {
        val payload = ChatPayload(messages = listOf(ChatMessageDto(role = "user", content = text)))
        api.chat(payload, headers = authHeaders())
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error("No se pudo enviar el mensaje", it) }
    )
}
