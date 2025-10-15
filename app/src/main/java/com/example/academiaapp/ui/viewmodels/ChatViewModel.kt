package com.example.academiaapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.academiaapp.data.remote.dto.Envelope
import com.example.academiaapp.data.remote.dto.GenericItem
import com.example.academiaapp.domain.ChatRepository
import com.example.academiaapp.domain.Result
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
    val suggestions: List<String> = emptyList()
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
            when (val res = repo.sendMessage(trimmed)) {
                is Result.Success -> applyEnvelope(res.data)
                is Result.Error -> _ui.update { it.copy(loading = false, error = res.message) }
            }
        }
    }

    private fun applyEnvelope(env: Envelope<GenericItem>) {
        val text = env.message?.takeIf { it.isNotBlank() } ?: ""
        val assistantMsg = if (text.isNotEmpty()) ChatMessage("assistant", text) else null
        _ui.update { current ->
            current.copy(
                loading = false,
                error = null,
                messages = if (assistantMsg != null) current.messages + assistantMsg else current.messages,
                items = env.data?.items.orEmpty(),
                suggestions = env.suggestions.orEmpty()
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
