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
)

// Generic item for early stages; later we can create models per type
data class GenericItem(
    val id: String? = null,
    val name: String? = null,
    val title: String? = null,
    val description: String? = null
)

data class ChatRequest(
    val query: String,
    val context: Map<String, Any?>? = null
)
