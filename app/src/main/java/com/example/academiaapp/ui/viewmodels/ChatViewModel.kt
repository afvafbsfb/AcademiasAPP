package com.example.academiaapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.academiaapp.data.remote.dto.Envelope
import com.example.academiaapp.data.remote.dto.ChatMessageDto
import com.example.academiaapp.data.remote.dto.GenericItem
import com.example.academiaapp.data.remote.dto.PaginationInfo
import com.example.academiaapp.domain.ChatRepository
import com.example.academiaapp.domain.Result
import java.text.Normalizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(val role: String, val text: String)

data class ChatUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val items: List<GenericItem> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val summaryFields: List<String> = emptyList(),
    val type: String? = null,
    val pagination: PaginationInfo? = null
)

class ChatViewModel(private val repo: ChatRepository) : ViewModel() {
    private val _ui = MutableStateFlow(ChatUiState())
    val ui: StateFlow<ChatUiState> = _ui

    fun loadWelcome() {
        if (_ui.value.messages.isNotEmpty()) return
        _ui.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            when (val res = repo.welcome()) {
                is Result.Success -> applyEnvelope(res.data)
                is Result.Error -> _ui.update { it.copy(loading = false, error = res.message) }
            }
        }
    }

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        // Add user message immediately
        _ui.update { it.copy(messages = it.messages + ChatMessage("user", trimmed)) }
        _ui.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            val conversation = buildConversationPayload(trimmed)
            when (val res = repo.sendConversation(conversation)) {
                is Result.Success -> applyEnvelope(res.data)
                is Result.Error -> _ui.update { it.copy(loading = false, error = res.message) }
            }
        }
    }


    private fun buildConversationPayload(latestUserText: String): List<ChatMessageDto> {
        val state = _ui.value
    val isNav = isNavigationCommand(latestUserText)
    val lastAssistant = state.messages.asReversed().firstOrNull { it.role == "assistant" }
        val userDto = ChatMessageDto(role = "user", content = latestUserText)
    // Permitir construir contexto aunque items esté vacío, si hay paginación y tipo
    if (isNav && lastAssistant != null && state.pagination != null && state.type != null) {
            // Construir mensaje de contexto de paginación para que la IA decida 'page' correctamente
            // Usar snake_case para alinear con el prompt ('next_page', 'prev_page', 'has_more')
            fun esc(s: String?): String = s?.replace("\\", "\\\\")?.replace("\"", "\\\"") ?: ""
            val parts = mutableListOf<String>()
            parts += "\"type\":\"${esc(state.type)}\""
            if (state.pagination.page != null) parts += "\"page\":${state.pagination.page}"
            if (state.pagination.size != null) parts += "\"size\":${state.pagination.size}"
            if (state.pagination.returned != null) parts += "\"returned\":${state.pagination.returned}"
            if (state.pagination.hasMore != null) parts += "\"has_more\":${state.pagination.hasMore}"
            if (state.pagination.nextPage != null) parts += "\"next_page\":${state.pagination.nextPage}"
            if (state.pagination.prevPage != null) parts += "\"prev_page\":${state.pagination.prevPage}"
            if (state.pagination.total != null) parts += "\"total\":${state.pagination.total}"
             val ctxJson = "{" + parts.joinToString(",") + "}"
             val ctxAssistant = ChatMessageDto(role = "assistant", content = "Contexto_paginacion: $ctxJson")
             // Orden alineado con el backend/test: primero "Mostrando...", luego "Contexto_paginacion", luego el user
             return listOf(
                 ChatMessageDto(role = "assistant", content = lastAssistant.text),
                 ctxAssistant,
                 userDto
             )
         }
        return listOf(userDto)
    }

    private fun isNavigationCommand(text: String): Boolean {
        val n = normalize(text)
        val exact = setOf(
            "siguiente", "siguiente pagina", "siguiente página", "ver siguiente", "ver mas", "ver más",
            "next", "more", "anterior", "pagina anterior", "página anterior", "previo", "prev", "atras", "atrás"
        )
        if (n in exact) return true
        return n.startsWith("siguiente") || n.startsWith("anterior") || n.startsWith("next") || n.startsWith("prev")
    }

    private fun normalize(s: String): String = Normalizer
        .normalize(s.lowercase().trim(), Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .replace("\\s+".toRegex(), " ")

    private fun applyEnvelope(env: Envelope<GenericItem>) {
        val text = env.message?.takeIf { it.isNotBlank() } ?: ""
        val assistantMsg = if (text.isNotEmpty()) ChatMessage("assistant", text) else null
        _ui.update { current ->
            current.copy(
                loading = false,
                error = null,
                messages = if (assistantMsg != null) current.messages + assistantMsg else current.messages,
                items = env.data?.items.orEmpty(),
                suggestions = env.suggestions.orEmpty(),
                summaryFields = env.data?.summaryFields.orEmpty(),
                type = env.data?.type,
                pagination = env.data?.pagination
            )
        }
    }
}

class ChatViewModelFactory(private val repo: ChatRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
