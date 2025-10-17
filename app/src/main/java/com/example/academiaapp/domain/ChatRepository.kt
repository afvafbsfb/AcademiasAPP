package com.example.academiaapp.domain

import com.example.academiaapp.data.remote.ChatApi
import com.example.academiaapp.data.remote.dto.ChatPayload
import com.example.academiaapp.data.remote.dto.ChatMessageDto
import com.example.academiaapp.data.remote.dto.Envelope
import com.example.academiaapp.data.remote.dto.GenericItem
import com.example.academiaapp.data.session.SessionStore
import com.example.academiaapp.core.EnvConfig
import kotlinx.coroutines.flow.first
import java.util.UUID

class ChatRepository(
    private val api: ChatApi,
    private val session: SessionStore
) {
    // Stable flow id for tracing within app process
    private val flowId: String = UUID.randomUUID().toString()

    private suspend fun authHeaders(): Map<String, String> {
        val token = session.accessToken.first()
        val base = if (!token.isNullOrBlank()) mapOf("Authorization" to "Bearer $token") else emptyMap()
        return if (EnvConfig.flowHtmlTraceEnabled) base + mapOf(
            "X-Flow-Diagram" to "true",
            "X-Flow-Id" to flowId
        ) else base
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

    suspend fun sendConversation(messages: List<ChatMessageDto>): Result<Envelope<GenericItem>> = runCatching {
        val payload = ChatPayload(messages = messages)
        api.chat(payload, headers = authHeaders())
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error("No se pudo enviar el mensaje", it) }
    )
}
