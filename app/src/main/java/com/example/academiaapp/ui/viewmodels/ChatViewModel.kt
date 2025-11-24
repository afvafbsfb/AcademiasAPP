package com.example.academiaapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.academiaapp.data.remote.dto.Envelope
import com.example.academiaapp.data.remote.dto.ChatMessageDto
import com.example.academiaapp.data.remote.dto.GenericItem
import com.example.academiaapp.data.remote.dto.PaginationInfo
import com.example.academiaapp.data.remote.dto.Suggestion
import com.example.academiaapp.data.session.SessionStore
import com.example.academiaapp.domain.ChatRepository
import com.example.academiaapp.domain.Result
import com.example.academiaapp.data.mock.MockChatRepository
import com.example.academiaapp.data.mock.MockConfig
import java.text.Normalizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(
    val role: String,
    val text: String,
    val items: List<GenericItem> = emptyList(),
    val suggestions: List<Suggestion> = emptyList(),
    val summaryFields: List<String> = emptyList(),
    val type: String? = null,
    val pagination: PaginationInfo? = null,
    val suggestionsEnabled: Boolean = true  // ✅ NUEVO: controlar si las sugerencias están habilitadas
)

data class ChatUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    
    // 🆕 Input del chat
    val input: String = "",
    
    // 🆕 Dialog de aclaración de sugerencias
    val showClarificationDialog: Boolean = false,
    val clarificationSuggestion: Suggestion? = null,
    val clarificationMessageIndex: Int = -1,
    
    // 🆕 Dialog de baja de alumno
    val showBajaAlumnoDialog: Boolean = false,
    
    // 🆕 Detalles de item seleccionado
    val detailsItem: Map<String, Any?>? = null,
    
    // 🆕 Estados de visualización
    val showScrollbar: Boolean = false,
    val itemsToShow: Int = 5,
    val selectedItemId: Int? = null,
    val expandedDay: String? = null,
    
    // 🆕 Estados de validación de formularios (Alta y Modificación)
    val nombreError: Boolean = false,
    val telefonoError: Boolean = false,
    val fechaError: Boolean = false,
    
    // 🆕 Estados de confirmación de formularios
    val showConfirmDialogAlta: Boolean = false,
    val showConfirmDialogModificacion: Boolean = false,
    
    // 🆕 Estados de dropdown de cursos
    val expandedCursosAlta: Boolean = false,
    val expandedCursosModificacion: Boolean = false,
    
    // 🆕 Labels de cursos seleccionados
    val cursoLabelAlta: String = "Seleccionar curso",
    val cursoLabelModificacion: String = "Seleccionar curso",
    
    // 🆕 IDs de cursos seleccionados
    val cursoSeleccionadoAlta: Int? = null,
    val cursoSeleccionadoModificacion: Int? = null
)

class ChatViewModel(
    private val repo: ChatRepository,
    private val session: SessionStore
) : ViewModel() {
    private val _ui = MutableStateFlow(ChatUiState())
    val ui: StateFlow<ChatUiState> = _ui

    // ✅ Mantener el contexto activo para navegación de páginas y sugerencias
    private var activeContext: Map<String, Any>? = null

    companion object {
        private const val MAX_MESSAGES = 75
    }

    // 🆕 Actualizar el input del chat
    fun onInputChange(newInput: String) {
        _ui.update { it.copy(input = newInput) }
    }

    // 🆕 Mostrar dialog de aclaración de sugerencias
    fun showClarificationDialog(suggestion: Suggestion, messageIndex: Int) {
        _ui.update {
            it.copy(
                showClarificationDialog = true,
                clarificationSuggestion = suggestion,
                clarificationMessageIndex = messageIndex
            )
        }
    }

    // 🆕 Cerrar dialog de aclaración
    fun hideClarificationDialog() {
        _ui.update {
            it.copy(
                showClarificationDialog = false,
                clarificationSuggestion = null,
                clarificationMessageIndex = -1
            )
        }
    }

    // 🆕 Mostrar dialog de baja de alumno
    fun showBajaAlumnoDialog() {
        _ui.update { it.copy(showBajaAlumnoDialog = true) }
    }

    // 🆕 Cerrar dialog de baja de alumno
    fun hideBajaAlumnoDialog() {
        _ui.update { it.copy(showBajaAlumnoDialog = false) }
    }

    // 🆕 Mostrar detalles de un item
    fun showDetailsItem(item: Map<String, Any?>) {
        _ui.update { it.copy(detailsItem = item) }
    }

    // 🆕 Cerrar detalles
    fun hideDetailsItem() {
        _ui.update { it.copy(detailsItem = null) }
    }

    // 🆕 Actualizar estado de scrollbar
    fun setScrollbarVisible(visible: Boolean) {
        _ui.update { it.copy(showScrollbar = visible) }
    }

    // 🆕 Actualizar items a mostrar en lista compacta
    fun setItemsToShow(count: Int) {
        _ui.update { it.copy(itemsToShow = count) }
    }

    // 🆕 Seleccionar item
    fun selectItem(itemId: Int?) {
        _ui.update { it.copy(selectedItemId = itemId) }
    }

    // 🆕 Expandir/contraer día en tabla semanal
    fun toggleExpandedDay(day: String?) {
        _ui.update { it.copy(expandedDay = day) }
    }

    // 🆕 Actualizar errores de validación
    fun setNombreError(hasError: Boolean) {
        _ui.update { it.copy(nombreError = hasError) }
    }

    fun setTelefonoError(hasError: Boolean) {
        _ui.update { it.copy(telefonoError = hasError) }
    }

    fun setFechaError(hasError: Boolean) {
        _ui.update { it.copy(fechaError = hasError) }
    }

    // 🆕 Mostrar/ocultar diálogos de confirmación de formularios
    fun showConfirmDialogAlta() {
        _ui.update { it.copy(showConfirmDialogAlta = true) }
    }

    fun hideConfirmDialogAlta() {
        _ui.update { it.copy(showConfirmDialogAlta = false) }
    }

    fun showConfirmDialogModificacion() {
        _ui.update { it.copy(showConfirmDialogModificacion = true) }
    }

    fun hideConfirmDialogModificacion() {
        _ui.update { it.copy(showConfirmDialogModificacion = false) }
    }

    // 🆕 Manejar estado de expansión de dropdown de cursos
    fun setExpandedCursosAlta(expanded: Boolean) {
        _ui.update { it.copy(expandedCursosAlta = expanded) }
    }

    fun setExpandedCursosModificacion(expanded: Boolean) {
        _ui.update { it.copy(expandedCursosModificacion = expanded) }
    }

    // 🆕 Manejar labels de cursos seleccionados
    fun setCursoLabelAlta(label: String) {
        _ui.update { it.copy(cursoLabelAlta = label) }
    }

    fun setCursoLabelModificacion(label: String) {
        _ui.update { it.copy(cursoLabelModificacion = label) }
    }

    // 🆕 Inicializar label de curso en formulario de modificación
    fun initCursoLabelModificacion(cursos: List<Map<String, Any?>>, cursoIdInicial: Int?) {
        if (cursoIdInicial != null) {
            val label = cursos.find { (it["id"] as? Number)?.toInt() == cursoIdInicial }
                ?.get("display_text") as? String ?: "Seleccionar curso"
            _ui.update { it.copy(cursoLabelModificacion = label) }
        }
    }

    // 🆕 Manejar IDs de cursos seleccionados
    fun setCursoSeleccionadoAlta(cursoId: Int?) {
        _ui.update { it.copy(cursoSeleccionadoAlta = cursoId) }
    }

    fun setCursoSeleccionadoModificacion(cursoId: Int?) {
        _ui.update { it.copy(cursoSeleccionadoModificacion = cursoId) }
    }

    // 🆕 Inicializar curso seleccionado en formulario de modificación
    fun initCursoSeleccionadoModificacion(cursoIdInicial: Int?) {
        if (cursoIdInicial != null) {
            _ui.update { it.copy(cursoSeleccionadoModificacion = cursoIdInicial) }
        }
    }

    fun loadWelcome() {
        if (_ui.value.messages.isNotEmpty()) return
        _ui.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            println("🔧 DEBUG: loadWelcome() iniciado")
            when (val res = repo.welcome()) {
                is Result.Success -> {
                    println("🔧 DEBUG: Welcome Success - message=${res.data.message?.take(50)}")
                    applyEnvelope(res.data)
                }
                is Result.Error -> {
                    println("🔧 DEBUG: Welcome Error - ${res.message}")
                    _ui.update { it.copy(loading = false, error = res.message) }
                }
            }
        }
    }

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        
        _ui.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            // Si hay un contexto activo (ej. desde menú), usarlo también aquí
            val screen = activeContext?.get("screen") as? String
            // ✅ Bypass de mocks: mensajes que empiezan por "ok," van siempre al backend real
            val forceBackend = trimmed.startsWith("ok,", ignoreCase = true)
            val shouldUseMock = !forceBackend && screen != null && MockConfig.isMocked(screen)

            // 1. Construir payload CON el mensaje nuevo (pero sin añadirlo al estado todavía)
            val conversation = buildConversationPayload(trimmed)
            
            // 2. Ahora sí actualizar UI con el mensaje del usuario
            _ui.update { state ->
                val newList = (state.messages + ChatMessage("user", trimmed)).takeLast(MAX_MESSAGES)
                state.copy(messages = newList)
            }
            
            // 3. Routing: usar MockChatRepository si hay contexto activo de mock
            val res = if (shouldUseMock) {
                println("🔧 DEBUG: sendMessage usando MockChatRepository (contexto activo: $activeContext)")
                MockChatRepository(session).sendConversation(conversation, activeContext)
            } else {
                println("🔧 DEBUG: sendMessage usando ChatRepository real")
                repo.sendConversation(conversation)
            }
            
            when (res) {
                is Result.Success -> applyEnvelope(res.data)
                is Result.Error -> _ui.update { it.copy(loading = false, error = res.message) }
            }
        }
    }

    // ✅ NUEVO: Enviar mensaje con contexto adicional (para opciones del menú)
    fun sendMessageWithContext(text: String, context: Map<String, Any>? = null) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        
        // ✅ MEJORADO: Combinar el nuevo contexto con el activo, preservando el screen
        if (context != null) {
            activeContext = if (activeContext != null) {
                // Combinar: mantiene campos del contexto activo y agrega/sobreescribe con los nuevos
                activeContext!! + context
            } else {
                context
            }
        }

        _ui.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            // Detectar si debemos usar mock basándonos en el screen del contexto activo
            val screen = activeContext?.get("screen") as? String
            // ✅ Bypass de mocks: mensajes que empiezan por "ok," van siempre al backend real
            val forceBackend = trimmed.startsWith("ok,", ignoreCase = true)
            val shouldUseMock = !forceBackend && screen != null && MockConfig.isMocked(screen)

            // 1. Construir payload CON el mensaje nuevo (pero sin añadirlo al estado todavía)
            val conversation = buildConversationPayload(trimmed)
            
            // 2. Ahora sí actualizar UI con el mensaje del usuario
            _ui.update { state ->
                val newList = (state.messages + ChatMessage("user", trimmed)).takeLast(MAX_MESSAGES)
                state.copy(messages = newList)
            }

            // 3. Routing: usar MockChatRepository si es necesario
            val res = if (shouldUseMock) {
                println("🔧 DEBUG: Usando MockChatRepository para screen=$screen, activeContext=$activeContext")
                MockChatRepository(session).sendConversation(conversation, activeContext)
            } else {
                println("🔧 DEBUG: Usando ChatRepository real para screen=$screen, activeContext=$activeContext")
                repo.sendConversation(conversation, activeContext)
            }

            when (res) {
                is Result.Success -> {
                    println("🔧 DEBUG: Success - envelope.message=${res.data.message?.take(50)}")
                    applyEnvelope(res.data)
                }
                is Result.Error -> {
                    println("🔧 DEBUG: Error - ${res.message}")
                    _ui.update { it.copy(loading = false, error = res.message) }
                }
            }
        }
    }

    // ✅ NUEVO: Deshabilitar sugerencias de un mensaje específico cuando se hace clic en una
    fun disableSuggestionsForMessage(messageIndex: Int) {
        _ui.update { state ->
            val updatedMessages = state.messages.mapIndexed { index, message ->
                if (index == messageIndex && message.role == "assistant") {
                    message.copy(suggestionsEnabled = false)
                } else {
                    message
                }
            }
            state.copy(messages = updatedMessages)
        }
    }

    private fun buildConversationPayload(latestUserText: String): List<ChatMessageDto> {
        val state = _ui.value
        val isNav = isNavigationCommand(latestUserText)
        val lastAssistant = state.messages.asReversed().firstOrNull { it.role == "assistant" }
        val userDto = ChatMessageDto(role = "user", content = latestUserText)
        
        // Caso especial: navegación de paginación (requiere contexto específico)
        if (isNav && lastAssistant != null && lastAssistant.pagination != null && lastAssistant.type != null) {
            // Construir mensaje de contexto de paginación para que la IA decida 'page' correctamente
            fun esc(s: String?): String = s?.replace("\\", "\\\\")?.replace("\"", "\\\"") ?: ""
            val parts = mutableListOf<String>()
            parts += "\"type\":\"${esc(lastAssistant.type)}\""
            if (lastAssistant.pagination.page != null) parts += "\"page\":${lastAssistant.pagination.page}"
            if (lastAssistant.pagination.size != null) parts += "\"size\":${lastAssistant.pagination.size}"
            if (lastAssistant.pagination.returned != null) parts += "\"returned\":${lastAssistant.pagination.returned}"
            if (lastAssistant.pagination.hasMore != null) parts += "\"has_more\":${lastAssistant.pagination.hasMore}"
            if (lastAssistant.pagination.nextPage != null) parts += "\"next_page\":${lastAssistant.pagination.nextPage}"
            if (lastAssistant.pagination.prevPage != null) parts += "\"prev_page\":${lastAssistant.pagination.prevPage}"
            if (lastAssistant.pagination.total != null) parts += "\"total\":${lastAssistant.pagination.total}"
            val ctxJson = "{" + parts.joinToString(",") + "}"
            val ctxAssistant = ChatMessageDto(role = "assistant", content = "Contexto_paginacion: $ctxJson")
            return listOf(
                ChatMessageDto(role = "assistant", content = lastAssistant.text),
                ctxAssistant,
                userDto
            )
        }
        
        // ✅ Incluir historial completo de conversación (últimos 10 mensajes = ~5 turnos)
        // Esto permite que OpenAI mantenga el contexto entre múltiples turnos para altas/bajas/modificaciones
        val maxContextMessages = 10
        val conversationHistory = state.messages
            .takeLast(maxContextMessages)  // Toma los últimos N mensajes
            .map { msg ->
                ChatMessageDto(
                    role = msg.role,      // "user" o "assistant" - preserva quién dijo qué
                    content = msg.text    // Solo el texto, no items/suggestions
                )
            }
            .toMutableList()
        
        // Añadir el nuevo mensaje del usuario al final (orden cronológico)
        conversationHistory.add(userDto)
        
        return conversationHistory
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
        println("🔧 DEBUG: applyEnvelope - message='${env.message}', items=${env.data?.items?.size}, suggestions=${env.uiSuggestions?.size}")
        
        // Detectar si es un mensaje de alta exitosa (para actualizar contexto)
        if (env.message?.contains("Alta de nuevo alumno:", ignoreCase = true) == true && activeContext != null) {
            println("🔧 DEBUG: Alta exitosa detectada, actualizando activeContext con after_alta=true")
            activeContext = activeContext?.toMutableMap()?.apply {
                put("after_alta", true)
            }
        }
        
        // Si no hay mensaje pero hay items o sugerencias, crear un mensaje vacío pero válido
        val text = env.message?.takeIf { it.isNotBlank() } ?: ""
        val hasContent = text.isNotEmpty() || 
                         env.data?.items?.isNotEmpty() == true || 
                         env.uiSuggestions?.isNotEmpty() == true
        
        val assistantMsg = if (hasContent) {
            ChatMessage(
                role = "assistant",
                text = text,
                items = env.data?.items.orEmpty(),
                suggestions = env.uiSuggestions.orEmpty(),
                summaryFields = env.data?.summaryFields.orEmpty(),
                type = env.data?.type,
                pagination = env.data?.pagination,
                suggestionsEnabled = true
            )
        } else {
            println("🔧 DEBUG: applyEnvelope - NO SE CREÓ MENSAJE (sin contenido)")
            null
        }

        _ui.update { current ->
            val newMessages = if (assistantMsg != null) (current.messages + assistantMsg).takeLast(MAX_MESSAGES) else current.messages
            println("🔧 DEBUG: applyEnvelope - total messages after update: ${newMessages.size}")
            current.copy(
                loading = false,
                error = null,
                messages = newMessages
            )
        }
    }

    // Limpiar el contexto activo (útil al cambiar de pantalla o salir del chat)
    fun clearContext() {
        activeContext = null
        println("🔧 DEBUG: Contexto activo limpiado")
    }

    // Limpiar el historial del chat (se usará al cerrar sesión)
    fun reset() {
        _ui.value = ChatUiState()
        activeContext = null
    }
}

class ChatViewModelFactory(
    private val repo: ChatRepository,
    private val session: SessionStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(repo, session) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
