package com.example.academiaapp.data.mock

import com.example.academiaapp.data.remote.dto.*
import com.example.academiaapp.data.session.SessionStore
import com.example.academiaapp.domain.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlin.random.Random

/**
 * Repositorio mock que simula las respuestas del backend
 * Usado para desarrollo mientras se implementan los endpoints reales
 */
class MockChatRepository(
    private val session: SessionStore
) {
    
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
            
            // Pantalla de sesiones
            screen == "sesiones" -> handleSesionesScreen(lastMessage, context)
            
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
            // Volver a lista después de alta
            message.contains("volver", ignoreCase = true) && message.contains("lista", ignoreCase = true) ->
                MockDataGenerator.generateAlumnosListResponse(page = 1)

            // Lista de alumnos (inicial o paginación)
            message.contains("muéstrame", ignoreCase = true) ||
            message.contains("listar alumnos", ignoreCase = true) ||
            message.contains("página", ignoreCase = true) -> {
                MockDataGenerator.generateAlumnosListResponse(page)
            }

            // Envío de formulario de alta: si se recibe la acción submit en el contexto
            message.contains("alta nuevo alumno", ignoreCase = true) && (context?.get("action") == "submit_alta") -> {
                val form = context["form_data"] as? Map<String, Any?> ?: emptyMap()
                MockDataGenerator.generateAltaAlumnoSuccessResponse(form)
            }

            // Alta de alumno (mostrar formulario)
            message.contains("alta nuevo alumno", ignoreCase = true) ->
                generateAltaAlumnoForm()

            // Envío de formulario de modificación: si se recibe la acción submit_modificacion
            message.contains("modificación de alumno", ignoreCase = true) && (context?.get("action") == "submit_modificacion") -> {
                val alumnoId = (context["alumno_id"] as? Number)?.toInt() ?: 0
                val form = context["form_data"] as? Map<String, Any?> ?: emptyMap()
                MockDataGenerator.generateModificacionAlumnoSuccess(alumnoId, form)
            }

            // Modificación de alumno (mostrar formulario con datos pre-cargados)
            message.contains("modificación de alumno", ignoreCase = true) -> {
                val alumnoId = (context?.get("alumno_id") as? Number)?.toInt() ?: 0
                if (alumnoId > 0) {
                    MockDataGenerator.generateModificacionAlumnoForm(alumnoId)
                } else {
                    MockDataGenerator.generateErrorResponse("No se especificó el alumno a modificar")
                }
            }

            // ✅ PASO 5: Baja de alumno - procesar confirmación y eliminar de memoria
            // IMPORTANTE: Esta condición debe ir ANTES que las más genéricas
            (context?.get("action") == "baja_alumno") -> {
                val alumnoId = (context["alumno_id"] as? Number)?.toInt() ?: 0
                val alumnoNombre = context["alumno_nombre"] as? String ?: "Alumno"
                
                // Eliminar alumno de la lista en memoria
                val eliminado = MockData.deleteAlumno(alumnoId)
                
                if (eliminado) {
                    MockDataGenerator.generateBajaAlumnoSuccess(alumnoNombre, alumnoId)
                } else {
                    MockDataGenerator.generateErrorResponse("No se pudo eliminar el alumno (ID: $alumnoId)")
                }
            }

            // Confirmación de baja: si se recibe la acción confirm_baja (legacy - no usado actualmente)
            message.contains("baja de alumno", ignoreCase = true) && (context?.get("action") == "confirm_baja") -> {
                val alumnoId = (context["alumno_id"] as? Number)?.toInt() ?: 0
                val alumnoNombre = context["alumno_nombre"] as? String ?: "Alumno"
                MockDataGenerator.generateBajaAlumnoSuccess(alumnoNombre, alumnoId)
            }

            // Baja de alumno (mostrar formulario de confirmación)
            message.contains("baja de alumno", ignoreCase = true) -> {
                val alumnoId = (context?.get("alumno_id") as? Number)?.toInt() ?: 0
                if (alumnoId > 0) {
                    MockDataGenerator.generateBajaAlumnoForm(alumnoId)
                } else {
                    MockDataGenerator.generateErrorResponse("No se especificó el alumno a dar de baja")
                }
            }

            // Filtrar por pagos pendientes
            message.contains("pagos pendientes", ignoreCase = true) ->
                MockDataGenerator.generateAlumnosPagosPendientesResponse()

            // Ver detalle de alumno (solo si NO es modificación ni baja)
            context?.containsKey("alumno_id") == true &&
            !message.contains("modificación", ignoreCase = true) &&
            !message.contains("baja", ignoreCase = true) -> {
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
     * Maneja las interacciones en la pantalla de sesiones/clases
     */
    private suspend fun handleSesionesScreen(
        message: String,
        context: Map<String, Any?>?
    ): Envelope<GenericItem> {
        // Obtener nombre del usuario logueado
        val nombreProfesor = session.name.first().orEmpty().ifBlank { "Profesor" }
        
        // Calcular fechas dinámicamente usando java.time
        val hoy = java.time.LocalDate.now()
        val ayer = hoy.minusDays(1)
        val manana = hoy.plusDays(1)
        
        // Formatear fechas para consulta (YYYY-MM-DD)
        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val fechaHoy = hoy.format(formatter)
        val fechaAyer = ayer.format(formatter)
        val fechaManana = manana.format(formatter)
        
        // Obtener día de la semana en español
        val diasSemana = mapOf(
            java.time.DayOfWeek.MONDAY to "Lunes",
            java.time.DayOfWeek.TUESDAY to "Martes",
            java.time.DayOfWeek.WEDNESDAY to "Miércoles",
            java.time.DayOfWeek.THURSDAY to "Jueves",
            java.time.DayOfWeek.FRIDAY to "Viernes",
            java.time.DayOfWeek.SATURDAY to "Sábado",
            java.time.DayOfWeek.SUNDAY to "Domingo"
        )
        
        val diaSemanaHoy = diasSemana[hoy.dayOfWeek] ?: "Miércoles"
        val diaSemanaAyer = diasSemana[ayer.dayOfWeek] ?: "Martes"
        val diaSemanaManana = diasSemana[manana.dayOfWeek] ?: "Jueves"
        
        // Formatear fechas legibles
        val meses = listOf("", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                           "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
        val fechaLegibleHoy = "$diaSemanaHoy ${hoy.dayOfMonth} de ${meses[hoy.monthValue]} de ${hoy.year}"
        val fechaLegibleAyer = "$diaSemanaAyer ${ayer.dayOfMonth} de ${meses[ayer.monthValue]} de ${ayer.year}"
        val fechaLegibleManana = "$diaSemanaManana ${manana.dayOfMonth} de ${meses[manana.monthValue]} de ${manana.year}"
        
        // Detectar si es navegación entre días (delay corto) o carga inicial (delay largo)
        val esNavegacionDia = message.contains("ayer", ignoreCase = true) ||
                              message.contains("mañana", ignoreCase = true)
        
        if (esNavegacionDia) {
            delay(1000L) // 1 segundo para cambio de día
        } else {
            delay(Random.nextLong(4000L, 5000L)) // 4-5 segundos para carga inicial
        }
        
        return when {
            // Ver clases de toda la semana
            message.contains("toda la semana", ignoreCase = true) ||
            message.contains("ver semana", ignoreCase = true) ->
                MockDataGenerator.generateMisClasesSemanalesResponse(
                    fechaInicio = fechaHoy,
                    nombreProfesor = nombreProfesor
                )
            
            // Ver clases de mañana
            message.contains("mañana", ignoreCase = true) ||
            message.contains("clases de mañana", ignoreCase = true) ->
                MockDataGenerator.generateMisClasesHoyResponse(
                    diaSemana = diaSemanaManana,
                    fecha = fechaManana,
                    fechaLegible = fechaLegibleManana,
                    nombreProfesor = nombreProfesor
                )
            
            // Ver clases de ayer
            message.contains("ayer", ignoreCase = true) ||
            message.contains("clases de ayer", ignoreCase = true) ->
                MockDataGenerator.generateMisClasesHoyResponse(
                    diaSemana = diaSemanaAyer,
                    fecha = fechaAyer,
                    fechaLegible = fechaLegibleAyer,
                    nombreProfesor = nombreProfesor
                )
            
            // Ver clases de hoy (default)
            message.contains("hoy", ignoreCase = true) ||
            message.contains("muéstrame", ignoreCase = true) ->
                MockDataGenerator.generateMisClasesHoyResponse(
                    diaSemana = diaSemanaHoy,
                    fecha = fechaHoy,
                    fechaLegible = fechaLegibleHoy,
                    nombreProfesor = nombreProfesor
                )
            
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
