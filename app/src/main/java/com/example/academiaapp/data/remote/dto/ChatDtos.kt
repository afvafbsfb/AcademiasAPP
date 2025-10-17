package com.example.academiaapp.data.remote.dto

data class Envelope<T>(
    val status: String,
    val message: String?,
    val data: DataSection<T>?,
    val suggestions: List<String>?,
    val error: Any? = null
)

data class DataSection<T>(
    val type: String?,
    val items: List<T>?,
    val summaryFields: List<String>? = null,
    val pagination: PaginationInfo? = null,
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
