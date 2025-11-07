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
 * Lógica determinista (NO depende del backend):
 * - Paginacion: NUNCA requiere confirmación (tiene toda la info: page, size, direction)
 * - Registro: Depende del tipo de acción y si hay registro específico:
 *   - Alta sin registro específico: NO requiere confirmación (mock autónomo)
 *   - Consulta sin registro específico: NO requiere confirmación (listado filtrado)
 *   - Baja/Modificación: SIEMPRE requiere confirmación (seguridad)
 *   - Cualquiera con registro específico: SIEMPRE requiere confirmación
 * - Generica: Depende del contenido del texto:
 *   - Si contiene palabras de listado ("listar", "lista", "listado"): NO requiere confirmación
 *   - En cualquier otro caso: SÍ requiere confirmación (buscar, filtrar, etc.)
 */
fun Suggestion.needsClarification(): Boolean {
    return when (type.lowercase()) {
        "paginacion" -> false  // Paginación nunca necesita aclaración
        "registro" -> {
            // ✅ MEJORADO: Distinguir entre acciones autónomas (mock) y específicas (backend real)
            when (recordAction?.lowercase()) {
                "alta" -> record != null  // Alta sin registro específico es autónoma (mock)
                "consulta" -> record != null  // Consulta sin registro es listado filtrado (mock)
                "baja", "modificacion" -> true  // Estas siempre necesitan confirmación (seguridad)
                else -> true  // Fallback seguro para acciones desconocidas
            }
        }
        "generica" -> {
            // Heurística: detectar acciones de listado/visualización que son autosuficientes
            val text = displayText.trim().lowercase()
            val isListAction = text.contains("listar") || 
                               text.contains("lista") || 
                               text.contains("listado") ||
                               text.contains("ver clases") ||  // ✅ Sesiones: "Ver clases de hoy/ayer/mañana"
                               text.contains("ver toda la semana")  // ✅ Sesiones: vista semanal
            
            !isListAction  // true si NO es listado, false si es listado
        }
        else -> true  // Fallback seguro para tipos desconocidos
    }
}
