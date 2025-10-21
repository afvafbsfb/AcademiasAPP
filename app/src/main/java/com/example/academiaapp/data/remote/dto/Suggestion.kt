package com.example.academiaapp.data.remote.dto

/**
 * Sugerencia tipada del backend-chat.
 * Tipos: "Paginacion" | "Registro" | "Generica"
 */
data class Suggestion(
    val id: String,
    val displayText: String,
    val type: String,
    
    // Solo si type="Registro"
    val recordAction: String? = null,  // "Consulta" | "Modificacion" | "Baja" | "Alta"
    val record: RecordRef? = null,
    
    // Solo si type="Paginacion"
    val pagination: PaginationSuggestion? = null,
    val contextToken: String? = null
)

/**
 * Referencia a un registro específico
 */
data class RecordRef(
    val resource: String,  // "usuarios" | "academias" | etc.
    val id: String
)

/**
 * Información de paginación para sugerencias de navegación
 */
data class PaginationSuggestion(
    val direction: String,  // "next" | "prev" | "goto"
    val page: Int?,
    val size: Int?
)
