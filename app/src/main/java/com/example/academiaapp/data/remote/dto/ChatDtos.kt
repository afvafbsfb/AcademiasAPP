package com.example.academiaapp.data.remote.dto

data class Envelope<T>(
    val status: String,
    val message: String?,
    val data: DataSection<T>?,
    
    // ✅ NUEVO: Sugerencias tipadas del backend (reemplaza suggestions)
    val uiSuggestions: List<Suggestion>? = null,
    val uiSuggestionsVersion: Int? = null,
    
    // ⚠️ DEPRECADO: Mantener temporalmente para compatibilidad
    @Deprecated("Usar uiSuggestions en su lugar", ReplaceWith("uiSuggestions"))
    val suggestions: List<String>? = null,
    
    val error: Any? = null,
    val messages: List<MessageEntry>? = null
)

data class DataSection<T>(
    val type: String?,
    val items: List<T>?,
    val summaryFields: List<String>? = null,  // ✅ Campos relevantes para resumen
    val pagination: PaginationInfo? = null,
    val hierarchy: Any? = null  // Reservado para futuras jerarquías
)

// Dynamic item as key-value pairs to preserve all fields from backend
typealias GenericItem = Map<String, Any?>

data class ChatMessageDto(
    val role: String,
    val content: String
)

data class ChatPayload(
    val messages: List<ChatMessageDto>,
    val context: Map<String, Any?>? = null
)

data class PaginationInfo(
    val page: Int? = null,
    val size: Int? = null,
    val returned: Int? = null,
    val hasMore: Boolean? = null,
    val nextPage: Int? = null,
    val prevPage: Int? = null,
    val total: Int? = null
)

/**
 * Mensaje de diagnóstico del backend (opcional en UI)
 */
data class MessageEntry(
    val type: String,     // "info" | "warning" | "system" | "debug"
    val content: String
)
