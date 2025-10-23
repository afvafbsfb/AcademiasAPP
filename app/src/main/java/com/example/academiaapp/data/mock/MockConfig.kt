package com.example.academiaapp.data.mock

/**
 * Configuración global de mocks
 * Permite activar/desactivar mocks para desarrollo sin tocar el código real
 */
object MockConfig {
    
    // ✅ Flags de activación de mocks (cambiar a false cuando tengamos endpoints reales)
    var mockAlumnos: Boolean = true
    var mockCursos: Boolean = true
    
    // ⏱️ Configuración de latencia simulada (milisegundos)
    const val MIN_DELAY_MS = 2500L
    const val MAX_DELAY_MS = 3500L
    
    /**
     * Verifica si una pantalla específica debe usar mocks
     * @param screen Nombre de la pantalla ("alumnos", "cursos", etc.)
     * @return true si debe usar mock, false si debe usar endpoint real
     */
    fun isMocked(screen: String?): Boolean {
        return when (screen) {
            "alumnos" -> mockAlumnos
            "cursos" -> mockCursos
            else -> false
        }
    }
}
