package com.example.academiaapp.data.mock

import com.example.academiaapp.data.remote.dto.*
import com.example.academiaapp.domain.Result
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Repositorio mock que simula las respuestas del backend
 * Usado para desarrollo mientras se implementan los endpoints reales
 */
class MockChatRepository {
    
    /**
     * Simula el mensaje de bienvenida
     * @return Envelope con mensaje de bienvenida personalizado
     */
    suspend fun welcome(): Result<Envelope<GenericItem>> {
        // Simular latencia realista
        delay(Random.nextLong(MockConfig.MIN_DELAY_MS, MockConfig.MAX_DELAY_MS))
        
        val welcomeMessage = "¡Bienvenido! 👋\n\n" +
                "Soy tu asistente virtual de la academia.\n\n" +
                "Puedo ayudarte con:\n" +
                "• Consultar alumnos y cursos\n" +
                "• Ver información detallada\n" +
                "• Gestionar inscripciones\n\n" +
                "¿En qué puedo ayudarte hoy?"
        
        val envelope = Envelope<GenericItem>(
            status = "success",
            message = welcomeMessage,
            data = null,
            uiSuggestions = listOf(
                Suggestion(
                    id = "sug_ver_alumnos",
                    displayText = "Ver mis alumnos",
                    type = "Generica",
                    recordAction = null,
                    record = null,
                    pagination = null,
                    contextToken = null
                ),
                Suggestion(
                    id = "sug_ver_cursos",
                    displayText = "Ver mis cursos",
                    type = "Generica",
                    recordAction = null,
                    record = null,
                    pagination = null,
                    contextToken = null
                )
            )
        )
        
        return Result.Success(envelope)
    }
    
    /**
     * Simula el envío de un mensaje de conversación
     * @param messages Lista de mensajes de la conversación
     * @param context Contexto adicional (pantalla, acción, etc.)
     * @return Envelope con la respuesta simulada
     */
    suspend fun sendConversation(
        messages: List<ChatMessageDto>,
        context: Map<String, Any?>? = null
    ): Result<Envelope<GenericItem>> {
        // Simular latencia realista
        delay(Random.nextLong(MockConfig.MIN_DELAY_MS, MockConfig.MAX_DELAY_MS))
        
        val lastMessage = messages.lastOrNull()?.content?.lowercase() ?: ""
        val screen = context?.get("screen") as? String
        
        // Detectar si viene de sugerencia de paginación
        val page = context?.get("page") as? Int ?: 1
        
        // Generar respuesta según contexto
        val response = when {
            // Pantalla de alumnos
            screen == "alumnos" -> handleAlumnosScreen(lastMessage, context, page)
            
            // Pantalla de cursos
            screen == "cursos" -> handleCursosScreen(lastMessage, context)
            
            // Consulta genérica
            lastMessage.contains("alumno", ignoreCase = true) -> 
                MockDataGenerator.generateAlumnosListResponse(page = 1)
            
            lastMessage.contains("curso", ignoreCase = true) -> 
                MockDataGenerator.generateCursosListResponse()
            
            else -> MockDataGenerator.generateGenericResponse(lastMessage)
        }
        
        return Result.Success(response)
    }
    
    /**
     * Maneja las interacciones en la pantalla de alumnos
     */
    private fun handleAlumnosScreen(
        message: String,
        context: Map<String, Any?>?,
        page: Int
    ): Envelope<GenericItem> {
        return when {
            // Volver a lista después de alta (incluye el nuevo alumno)
            message.contains("volver", ignoreCase = true) && message.contains("lista", ignoreCase = true) ->
                MockDataGenerator.generateAlumnosListResponse(page = 1, includeNewAlumno = true)

            // Lista de alumnos (inicial o paginación)
            message.contains("muéstrame", ignoreCase = true) ||
            message.contains("página", ignoreCase = true) -> {
                val includeNew = context?.get("after_alta") == true
                MockDataGenerator.generateAlumnosListResponse(page, includeNewAlumno = includeNew)
            }

            // Envío de formulario de alta: si se recibe la acción submit en el contexto
            message.contains("alta nuevo alumno", ignoreCase = true) && (context?.get("action") == "submit_alta") -> {
                val form = context["form_data"] as? Map<String, Any?> ?: emptyMap()
                MockDataGenerator.generateAltaAlumnoSuccessResponse(form)
            }

            // Alta de alumno (mostrar formulario)
            message.contains("alta nuevo alumno", ignoreCase = true) ->
                generateAltaAlumnoForm()

            // Filtrar por pagos pendientes
            message.contains("pagos pendientes", ignoreCase = true) ->
                MockDataGenerator.generateAlumnosPagosPendientesResponse()

            // Ver detalle de alumno
            context?.containsKey("alumno_id") == true -> {
                val alumnoId = (context["alumno_id"] as? Number)?.toInt() ?: 1
                MockDataGenerator.generateAlumnoDetailResponse(alumnoId)
            }

            else -> MockDataGenerator.generateGenericResponse(message)
        }
    }
    
    /**
     * Maneja las interacciones en la pantalla de cursos
     */
    private fun handleCursosScreen(
        message: String,
        context: Map<String, Any?>?
    ): Envelope<GenericItem> {
        return when {
            // Lista de cursos
            message.contains("muéstrame", ignoreCase = true) ->
                MockDataGenerator.generateCursosListResponse()
            
            // Ver detalle de curso
            context?.containsKey("curso_id") == true -> {
                val cursoId = (context["curso_id"] as? Number)?.toInt() ?: 1
                MockDataGenerator.generateCursoDetailResponse(cursoId)
            }
            
            else -> MockDataGenerator.generateGenericResponse(message)
        }
    }
    
    /**
     * Genera formulario de alta de alumno
     */
    private fun generateAltaAlumnoForm(): Envelope<GenericItem> {
        val cursosCombo = MockData.getCursos().map { curso ->
            mapOf(
                "id" to curso["id"],
                "display_text" to "${curso["nombre"]} (${curso["fecha_inicio"]} - ${curso["fecha_fin"]})"
            )
        }
        return Envelope(
            status = "success",
            message = "Alta de nuevo alumno - por favor completa los datos obligatorios",
            data = DataSection(
                type = "formulario_alta_alumno",
                items = listOf(
                    mapOf(
                        "field_type" to "form",
                        "cursos_disponibles" to cursosCombo
                    )
                ),
                summaryFields = null,
                pagination = null
            ),
            uiSuggestions = listOf(
                Suggestion(
                    id = "sug_submit_alta",
                    displayText = "Alta",
                    type = "Registro",
                    recordAction = "Alta",
                    record = null,
                    pagination = null,
                    contextToken = null
                ),
                Suggestion(
                    id = "sug_cancelar",
                    displayText = "Cancelar",
                    type = "Generica",
                    recordAction = null,
                    record = null,
                    pagination = null,
                    contextToken = null
                )
            )
        )
    }
    
    /**
     * Limpia el cache (equivalente al método del ChatRepository real)
     */
    fun clearWelcomeCache() {
        // No hay cache en mock, pero mantenemos el método por compatibilidad
    }
}
