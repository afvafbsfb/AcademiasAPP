package com.example.academiaapp.data.remote.dto

/**
 * Sugerencia tipada del backend-chat.
 * Tipos: "Paginacion" | "Registro" | "Generica"
 */
data class Suggestion(
    val id: String,
    val displayText: String,
    val type: String,
    
    // Indica si el cliente debe pedir más información antes de enviar
    // Default = true (comportamiento seguro si el backend no lo envía)
    val requiresClarification: Boolean? = null,
    
    // Solo si type="Registro"
    val recordAction: String? = null,  // "Alta" | "Baja" | "Modificacion"
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

/**
 * Determina si una sugerencia necesita aclaración del usuario antes de enviarla.
 * 
 * Reglas:
 * - Paginacion: NUNCA (siempre false)
 * - Registro: SIEMPRE (siempre true)
 * - Generica: Depende del flag requiresClarification O del texto
 *   - Si empieza con "Listar/Lista/Listado" (case-insensitive): false
 *   - Si el flag existe: usa su valor
 *   - Si null (no existe): true (fallback seguro)
 */
fun Suggestion.needsClarification(): Boolean {
    return when (type.lowercase()) {
        "paginacion" -> false  // Paginación nunca necesita aclaración
        "registro" -> true     // Registro SIEMPRE necesita aclaración
        "generica" -> {
            // Heurística: Si empieza con "Listar", "Lista" o "Listado" (case-insensitive), no necesita aclaración
            val text = displayText.trim().lowercase()
            val isListAction = text.startsWith("listar ") || 
                               text.startsWith("lista ") || 
                               text.startsWith("listado ")
            
            if (isListAction) {
                false  // Los listados son autosuficientes
            } else {
                requiresClarification ?: true  // Usa el flag si existe, sino true (seguro)
            }
        }
        else -> true  // Fallback seguro para tipos desconocidos
    }
}
