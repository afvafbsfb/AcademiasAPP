package com.example.academiaapp.ui.models

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Representa una opción del menú lateral (Navigation Drawer)
 * @param id Identificador único de la opción
 * @param label Texto visible para el usuario
 * @param icon Icono de Material Icons
 * @param chatMessage Mensaje que se auto-enviará al chat (null para acciones especiales)
 * @param contextData Datos de contexto adicionales para el mensaje
 * @param roles Lista de roles que pueden ver esta opción
 * @param isMocked Si true, usa MockChatRepository en lugar del real
 */
data class MenuOption(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val chatMessage: String?,
    val contextData: Map<String, Any>? = null,
    val roles: List<String>,
    val isMocked: Boolean = false  // Por defecto false (usa endpoint real)
)

