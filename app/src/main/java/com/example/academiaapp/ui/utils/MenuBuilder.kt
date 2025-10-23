package com.example.academiaapp.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.academiaapp.ui.models.MenuOption

/**
 * Generador de opciones del menú según el rol del usuario
 */
object MenuBuilder {

    /**
     * Opciones comunes para todos los roles
     */
    private fun getCommonOptions(): List<MenuOption> = listOf(
        MenuOption(
            id = "inicio",
            label = "Inicio",
            icon = Icons.Default.Home,
            chatMessage = "Muéstrame un resumen de inicio",
            roles = listOf("Admin_plataforma", "Admin_academia", "Profesor_academia")
        ),
        MenuOption(
            id = "chat_libre",
            label = "Chat Libre",
            icon = Icons.Default.Chat,
            chatMessage = null, // null = limpiar chat y dejar libre
            roles = listOf("Admin_plataforma", "Admin_academia", "Profesor_academia")
        )
    )

    /**
     * Opciones exclusivas para Admin_plataforma
     */
    private fun getAdminPlataformaOptions(): List<MenuOption> = listOf(
        MenuOption(
            id = "academias",
            label = "Academias",
            icon = Icons.Default.Business,
            chatMessage = "Muéstrame todas las academias de la plataforma",
            contextData = mapOf("resource" to "academias", "scope" to "global"),
            roles = listOf("Admin_plataforma")
        ),
        MenuOption(
            id = "usuarios_sistema",
            label = "Usuarios del Sistema",
            icon = Icons.Default.People,
            chatMessage = "Muéstrame todos los usuarios del sistema",
            contextData = mapOf("resource" to "usuarios", "scope" to "global"),
            roles = listOf("Admin_plataforma")
        ),
        MenuOption(
            id = "cursos_global",
            label = "Cursos (Todos)",
            icon = Icons.Default.School,
            chatMessage = "Muéstrame todos los cursos de todas las academias",
            contextData = mapOf("resource" to "cursos", "scope" to "global"),
            roles = listOf("Admin_plataforma")
        ),
        MenuOption(
            id = "alumnos_global",
            label = "Alumnos (Todos)",
            icon = Icons.Default.Person,
            chatMessage = "Muéstrame todos los alumnos de todas las academias",
            contextData = mapOf("resource" to "alumnos", "scope" to "global"),
            roles = listOf("Admin_plataforma")
        ),
        MenuOption(
            id = "profesores_global",
            label = "Profesores (Todos)",
            icon = Icons.Default.Group,
            chatMessage = "Muéstrame todos los profesores de todas las academias",
            contextData = mapOf("resource" to "profesores", "scope" to "global"),
            roles = listOf("Admin_plataforma")
        ),
        MenuOption(
            id = "sesiones_global",
            label = "Clases/Sesiones (Todas)",
            icon = Icons.Default.CalendarToday,
            chatMessage = "Muéstrame todas las clases de hoy de todas las academias",
            contextData = mapOf("resource" to "sesiones", "scope" to "global", "filter" to "hoy"),
            roles = listOf("Admin_plataforma")
        ),
        MenuOption(
            id = "gestion_economica_global",
            label = "Gestión Económica (Global)",
            icon = Icons.Default.AttachMoney,
            chatMessage = "Muéstrame un resumen económico de toda la plataforma",
            contextData = mapOf("resource" to "extractos", "scope" to "global"),
            roles = listOf("Admin_plataforma")
        )
    )

    /**
     * Opciones exclusivas para Admin_academia
     */
    private fun getAdminAcademiaOptions(): List<MenuOption> = listOf(
        MenuOption(
            id = "usuarios_sistema",
            label = "Usuarios",
            icon = Icons.Default.People,
            chatMessage = "Muéstrame todos los usuarios de mi academia",
            contextData = mapOf("resource" to "usuarios", "scope" to "academia"),
            roles = listOf("Admin_academia")
        ),
        MenuOption(
            id = "cursos",
            label = "Cursos",
            icon = Icons.Default.School,
            chatMessage = "Muéstrame todos los cursos de mi academia",
            contextData = mapOf("resource" to "cursos", "scope" to "academia"),
            roles = listOf("Admin_academia")
        ),
        MenuOption(
            id = "alumnos",
            label = "Alumnos",
            icon = Icons.Default.Person,
            chatMessage = "Muéstrame todos los alumnos de mi academia",
            contextData = mapOf("resource" to "alumnos", "scope" to "academia"),
            roles = listOf("Admin_academia")
        ),
        MenuOption(
            id = "profesores",
            label = "Profesores",
            icon = Icons.Default.Group,
            chatMessage = "Muéstrame los profesores de mi academia",
            contextData = mapOf("resource" to "profesores", "scope" to "academia"),
            roles = listOf("Admin_academia")
        ),
        MenuOption(
            id = "sesiones_clases",
            label = "Clases/Sesiones",
            icon = Icons.Default.CalendarToday,
            chatMessage = "Muéstrame las clases de hoy",
            contextData = mapOf("resource" to "sesiones", "scope" to "academia", "filter" to "hoy"),
            roles = listOf("Admin_academia")
        ),
        MenuOption(
            id = "gestion_economica",
            label = "Gestión Económica",
            icon = Icons.Default.AttachMoney,
            chatMessage = "Muéstrame los extractos y movimientos pendientes",
            contextData = mapOf("resource" to "extractos", "scope" to "academia"),
            roles = listOf("Admin_academia")
        ),
        MenuOption(
            id = "mi_academia",
            label = "Mi Academia",
            icon = Icons.Default.Business,
            chatMessage = "Muéstrame los datos de mi academia",
            contextData = mapOf("resource" to "academia", "scope" to "propia"),
            roles = listOf("Admin_academia")
        )
    )

    /**
     * Opciones exclusivas para Profesor_academia
     */
    private fun getProfesorAcademiaOptions(): List<MenuOption> = listOf(
        MenuOption(
            id = "mis_clases",
            label = "Mis Clases",
            icon = Icons.Default.CalendarToday,
            chatMessage = "Muéstrame mis clases",
            contextData = mapOf(
                "resource" to "sesiones",
                "scope" to "profesor"
            ),
            roles = listOf("Profesor_academia")
        ),
        MenuOption(
            id = "mis_cursos",
            label = "Mis Cursos",
            icon = Icons.Default.School,
            chatMessage = "Muéstrame los cursos que imparto",
            contextData = mapOf(
                "resource" to "cursos",
                "scope" to "profesor"
            ),
            roles = listOf("Profesor_academia")
        ),
        MenuOption(
            id = "mis_alumnos",
            label = "Mis Alumnos",
            icon = Icons.Default.Person,
            chatMessage = "Muéstrame mis alumnos",
            contextData = mapOf(
                "resource" to "alumnos",
                "scope" to "profesor"
            ),
            roles = listOf("Profesor_academia")
        ),
        MenuOption(
            id = "mis_anotaciones",
            label = "Mis Anotaciones",
            icon = Icons.Default.EditNote,
            chatMessage = "Muéstrame mis anotaciones recientes",
            contextData = mapOf(
                "resource" to "anotaciones",
                "scope" to "profesor",
                "order" to "recientes"
            ),
            roles = listOf("Profesor_academia")
        )
    )

    /**
     * Opciones finales (siempre al final del menú)
     */
    private fun getFooterOptions(userRole: String): List<MenuOption> {
        val normalizedRole = userRole.trim()

        val options = mutableListOf<MenuOption>()

        // Configuración solo para Admin_plataforma y Admin_academia
        if (normalizedRole.equals("Admin_plataforma", ignoreCase = true) ||
            normalizedRole.equals("Admin_academia", ignoreCase = true)) {
            options.add(
                MenuOption(
                    id = "configuracion",
                    label = "Configuración",
                    icon = Icons.Default.Settings,
                    chatMessage = "Muéstrame mi perfil y configuración",
                    roles = listOf("Admin_plataforma", "Admin_academia")
                )
            )
        }

        // Cerrar sesión para todos
        options.add(
            MenuOption(
                id = "cerrar_sesion",
                label = "Cerrar sesión",
                icon = Icons.Default.Logout,
                chatMessage = null, // null = acción especial (logout)
                roles = listOf("Admin_plataforma", "Admin_academia", "Profesor_academia")
            )
        )

        return options
    }

    /**
     * Genera la lista de opciones del menú según el rol del usuario
     * @param userRole Rol del usuario (Admin_plataforma, Admin_academia, Profesor_academia)
     * @return Lista ordenada de opciones del menú
     */
    fun getMenuOptions(userRole: String): List<MenuOption> {
        val normalizedRole = userRole.trim()

        val commonOptions = getCommonOptions()
        val roleSpecificOptions = when {
            normalizedRole.equals("Admin_plataforma", ignoreCase = true) -> getAdminPlataformaOptions()
            normalizedRole.equals("Admin_academia", ignoreCase = true) -> getAdminAcademiaOptions()
            normalizedRole.equals("Profesor_academia", ignoreCase = true) -> getProfesorAcademiaOptions()
            else -> emptyList()
        }
        val footerOptions = getFooterOptions(normalizedRole)

        // Combinar: opciones comunes + opciones específicas del rol + opciones finales
        return commonOptions + roleSpecificOptions + footerOptions
    }
}
