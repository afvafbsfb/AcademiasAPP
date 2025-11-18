package com.example.academiaapp.data.mock

import com.example.academiaapp.data.remote.dto.*
import com.example.academiaapp.data.session.SessionStore
import com.example.academiaapp.domain.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
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
            (message.contains("alta de alumno", ignoreCase = true) || message.contains("alta nuevo alumno", ignoreCase = true)) && 
            (context?.get("action") == "submit_alta") -> {
                val form = context["form_data"] as? Map<String, Any?> ?: emptyMap()
                MockDataGenerator.generateAltaAlumnoSuccessResponse(form)
            }

            // Alta de alumno (mostrar formulario)
            message.contains("alta nuevo alumno", ignoreCase = true) ->
                generateAltaAlumnoForm()

            // Envío de formulario de modificación: si se recibe la acción submit_modificacion
            (message.contains("modificación de alumno", ignoreCase = true) || message.contains("modificación del alumno", ignoreCase = true)) && 
            (context?.get("action") == "submit_modificacion") -> {
                val alumnoId = (context["alumno_id"] as? Number)?.toInt() ?: 0
                val form = context["form_data"] as? Map<String, Any?> ?: emptyMap()
                MockDataGenerator.generateModificacionAlumnoSuccess(alumnoId, form)
            }

            // Modificación de alumno (mostrar formulario con datos pre-cargados)
            message.contains("modificación de alumno", ignoreCase = true) || message.contains("modificación del alumno", ignoreCase = true) -> {
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
            (message.contains("baja de alumno", ignoreCase = true) || message.contains("baja del alumno", ignoreCase = true)) && 
            (context?.get("action") == "confirm_baja") -> {
                val alumnoId = (context["alumno_id"] as? Number)?.toInt() ?: 0
                val alumnoNombre = context["alumno_nombre"] as? String ?: "Alumno"
                MockDataGenerator.generateBajaAlumnoSuccess(alumnoNombre, alumnoId)
            }

            // Baja de alumno (mostrar formulario de confirmación)
            // ✅ MODIFICADO: Ya no requiere alumnoId, siempre coge el primer alumno de la lista
            message.contains("baja de alumno", ignoreCase = true) || message.contains("baja del alumno", ignoreCase = true) -> {
                MockDataGenerator.generateBajaAlumnoForm()
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
            // ✅ NUEVO: Iniciar sesión
            message.contains("iniciar sesión", ignoreCase = true) ||
            message.contains("iniciar clase", ignoreCase = true) -> {
                val horarioCursoId = (context?.get("horario_curso_id") as? Number)?.toInt()

                if (horarioCursoId != null) {
                    // Crear timestamp actual
                    val ahora = java.time.LocalDateTime.now()
                    val timestampFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val timestampAlta = ahora.format(timestampFormatter)
                    val fechaHoy = ahora.toLocalDate().toString()

                    // 🔧 FIX: Buscar sesión existente (programada) antes de crear una nueva
                    val sesionExistente = MockData.getSesionDinamica(horarioCursoId, fechaHoy)
                    
                    if (sesionExistente != null && sesionExistente["timestamp_alta"] == null) {
                        // ✅ La sesión ya existe (programada) - actualizarla
                        val idExistente = (sesionExistente["id"] as? Number)?.toInt() ?: 0
                        println("🔧 DEBUG: Iniciando sesión existente id=$idExistente, horarioId=$horarioCursoId")
                        MockData.iniciarSesion(idExistente, timestampAlta)
                        
                        // Mostrar alumnos de la sesión recién iniciada
                        MockDataGenerator.generateAlumnosSesionResponse(
                            sesionId = idExistente,
                            horarioCursoId = null,
                            nombreProfesor = nombreProfesor
                        )
                    } else {
                        // ✅ No existe sesión programada - crear nueva
                        val horario = MockData.getHorariosProfesor().find {
                            (it["id"] as Int) == horarioCursoId
                        }
                        
                        if (horario != null) {
                            val horaInicio = horario["hora_inicio"] as String
                            val horaFin = horario["hora_fin"] as String
                            
                            println("🔧 DEBUG: Creando nueva sesión para horarioId=$horarioCursoId")
                            val nuevaSesionId = MockData.crearSesion(
                                horarioCursoId = horarioCursoId,
                                timestampAlta = timestampAlta,
                                horaInicio = horaInicio,
                                horaFin = horaFin
                            )
                            
                            // Mostrar alumnos de la sesión recién iniciada
                            MockDataGenerator.generateAlumnosSesionResponse(
                                sesionId = nuevaSesionId,
                                horarioCursoId = null,
                                nombreProfesor = nombreProfesor
                            )
                        } else {
                            MockDataGenerator.generateErrorResponse("Horario no encontrado")
                        }
                    }
                } else {
                    MockDataGenerator.generateErrorResponse("No se especificó el horario de la sesión")
                }
            }

            // ✅ NUEVO: Pasar lista de sesión
            message.contains("Pasar lista de la sesión", ignoreCase = true) -> {
                // ✅ Extraer datos del contexto
                val sesionId = (context?.get("sesion_id") as? Number)?.toInt()
                @Suppress("UNCHECKED_CAST")
                val alumnosAusentes = (context?.get("alumnosAusentes") as? List<Int>) ?: emptyList()
                
                if (sesionId != null) {
                    println("🔧 DEBUG Pasar lista - sesionId=$sesionId, ausentes=$alumnosAusentes")
                    
                    // Llamar a la función que registra ausencias
                    val numAnotaciones = MockData.pasarListaSesion(sesionId, alumnosAusentes)
                    
                    // Obtener info de la sesión para mensaje
                    val sesion = MockData.getSesionById(sesionId)
                    val horarioId = sesion?.get("horario_curso_id") as? Int
                    val horario = horarioId?.let { MockData.getHorarioById(it) }
                    val cursoId = horario?.get("curso_id") as? Int
                    val aulaId = sesion?.get("aula_id") as? Int
                    val curso = cursoId?.let { MockData.getCurso(it) }
                    val aula = aulaId?.let { MockData.getAula(it) }
                    
                    val cursoNombre = curso?.get("nombre") as? String ?: "la sesión"
                    val aulaNombre = aula?.get("nombre") as? String ?: "Aula"
                    // ✅ FIX: Obtener horarios del objeto horario (siempre están ahí) en lugar de la sesión
                    val horaInicio = horario?.get("hora_inicio") as? String ?: sesion?.get("hora_inicio") as? String ?: "00:00"
                    val horaFin = horario?.get("hora_fin") as? String ?: sesion?.get("hora_fin") as? String ?: "00:00"
                    
                    // Formatear fecha legible desde timestamp_alta (formato: "2025-10-22 08:02:00")
                    val timestampAlta = sesion?.get("timestamp_alta") as? String ?: ""
                    val fechaLegible = if (timestampAlta.isNotBlank()) {
                        try {
                            val fecha = java.time.LocalDate.parse(timestampAlta.substring(0, 10))
                            val diasSemana = listOf("", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
                            val meses = listOf("", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                                             "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
                            "${diasSemana[fecha.dayOfWeek.value]} ${fecha.dayOfMonth} de ${meses[fecha.monthValue]} de ${fecha.year}"
                        } catch (e: Exception) {
                            "Fecha no disponible"
                        }
                    } else "Fecha no disponible"
                    
                    val asistenciaInfo = MockData.calcularAsistenciaSesion(sesionId)
                    val (presentes, ausentes, total) = asistenciaInfo ?: Triple(0, 0, 0)
                    
                    // Obtener nombres de alumnos ausentes
                    val nombresAusentes = if (alumnosAusentes.isNotEmpty()) {
                        val nombres = alumnosAusentes.mapNotNull { id ->
                            MockData.getAlumno(id)?.get("nombre") as? String
                        }.joinToString(", ")
                        "\n  - $nombres"
                    } else {
                        ""
                    }
                    
                    println("🔧 DEBUG Pasar lista - presentes=$presentes, ausentes=$ausentes, total=$total, anotaciones=$numAnotaciones")
                    
                    // Generar respuesta de confirmación
                    Envelope(
                        status = "success",
                        message = """
                            Se ha pasado lista correctamente
                            
                            Fecha: $fechaLegible
                            Horario: $horaInicio - $horaFin
                            Aula: $aulaNombre
                            Curso: $cursoNombre
                            
                            Resumen de asistencia:
                            • Presentes: $presentes de $total alumnos
                            • Ausentes: $ausentes alumnos$nombresAusentes
                            • Anotaciones de ausencia creadas: $numAnotaciones
                        """.trimIndent(),
                        data = null,
                        uiSuggestions = listOf(
                            Suggestion(
                                id = "sug_volver_clases_hoy",
                                displayText = "Ver clases de hoy",
                                type = "Generica",
                                recordAction = null,
                                record = null,
                                pagination = null,
                                contextToken = null
                            )
                        )
                    )
                } else {
                    MockDataGenerator.generateErrorResponse("No se especificó el ID de la sesión")
                }
            }
            
            // ✅ NUEVO: Alta de anotación
            message.contains("Alta de anotación", ignoreCase = true) -> {
                // ✅ Extraer datos del contexto
                val sesionId = (context?.get("sesion_id") as? Number)?.toInt()
                val alumnoId = (context?.get("alumno_id") as? Number)?.toInt()
                val tipoAnotacion = context?.get("tipo_anotacion") as? String
                val descripcion = context?.get("descripcion") as? String
                val curso = context?.get("curso") as? String ?: ""
                val fecha = context?.get("fecha") as? String ?: ""
                val horaInicio = context?.get("hora_inicio") as? String ?: ""
                val horaFin = context?.get("hora_fin") as? String ?: ""
                val aula = context?.get("aula") as? String ?: ""
                
                if (sesionId != null && alumnoId != null && tipoAnotacion != null && descripcion != null) {
                    println("🔧 DEBUG Alta anotación - sesionId=$sesionId, alumnoId=$alumnoId, tipo=$tipoAnotacion")
                    
                    // Llamar a la función que crea la anotación
                    val newId = MockData.crearAnotacion(sesionId, alumnoId, tipoAnotacion, descripcion)
                    
                    // Obtener nombre del alumno
                    val alumno = MockData.getAlumnoById(alumnoId)
                    val alumnoNombre = alumno?.get("nombre") as? String ?: "Alumno"
                    
                    println("🔧 DEBUG Alta anotación - newId=$newId, alumno=$alumnoNombre")
                    
                    // Generar respuesta de confirmación
                    Envelope(
                        status = "success",
                        message = """
                            Se ha dado de alta la anotación correctamente
                            
                            Fecha: $fecha
                            Horario: $horaInicio - $horaFin
                            Aula: $aula
                            Curso: $curso
                            Alumno: $alumnoNombre
                            Tipo: $tipoAnotacion
                            Descripción: $descripcion
                        """.trimIndent(),
                        data = null,
                        uiSuggestions = listOf(
                            Suggestion(
                                id = "sug_volver_clases_hoy",
                                displayText = "Ver clases de hoy",
                                type = "Generica",
                                recordAction = null,
                                record = null,
                                pagination = null,
                                contextToken = null
                            )
                        )
                    )
                } else {
                    MockDataGenerator.generateErrorResponse("Faltan datos para crear la anotación")
                }
            }
            
            // ✅ ANTIGUO: Pasar lista sesión (formato antiguo - mantener por compatibilidad)
            message.startsWith("Pasar lista sesión", ignoreCase = true) -> {
                // Extraer sesionId y ausencias del mensaje
                // Formato esperado: "Pasar lista sesión 123: ausentes=1,5,12"
                val regex = """Pasar lista sesión (\d+): ausentes=(.*)""".toRegex(RegexOption.IGNORE_CASE)
                val matchResult = regex.find(message)
                
                if (matchResult != null) {
                    val sesionId = matchResult.groupValues[1].toIntOrNull()
                    val ausentesStr = matchResult.groupValues[2].trim()
                    
                    if (sesionId != null) {
                        // Parsear lista de ausentes
                        val alumnosAusentes = if (ausentesStr.isNotEmpty() && ausentesStr != "ninguno") {
                            ausentesStr.split(",").mapNotNull { it.trim().toIntOrNull() }
                        } else {
                            emptyList()
                        }
                        
                        // Llamar a la función que registra ausencias
                        val numAnotaciones = MockData.pasarListaSesion(sesionId, alumnosAusentes)
                        
                        // Obtener info de la sesión para mensaje
                        val sesion = MockData.getSesionById(sesionId)
                        val horarioId = sesion?.get("horario_curso_id") as? Int
                        val horario = horarioId?.let { MockData.getHorarioById(it) }
                        val cursoId = horario?.get("curso_id") as? Int
                        val curso = cursoId?.let { MockData.getCurso(it) }
                        val cursoNombre = curso?.get("nombre") as? String ?: "la sesión"
                        
                        val asistenciaInfo = MockData.calcularAsistenciaSesion(sesionId)
                        val (presentes, ausentes, total) = asistenciaInfo ?: Triple(0, 0, 0)
                        
                        // Generar respuesta de confirmación
                        Envelope(
                            status = "success",
                            message = """
                                ✅ Lista pasada correctamente para $cursoNombre
                                
                                📊 Resumen de asistencia:
                                • Presentes: $presentes/$total alumnos
                                • Ausentes: $ausentes alumnos
                                • Anotaciones creadas: $numAnotaciones
                            """.trimIndent(),
                            data = null,
                            uiSuggestions = listOf(
                                Suggestion(
                                    id = "sug_ver_alumnos_sesion",
                                    displayText = "Ver lista completa",
                                    type = "Generica",
                                    recordAction = null,
                                    record = null,
                                    pagination = null,
                                    contextToken = mapOf("sesion_id" to sesionId).toString()
                                ),
                                Suggestion(
                                    id = "sug_volver_clases",
                                    displayText = "Volver a mis clases",
                                    type = "Generica",
                                    recordAction = null,
                                    record = null,
                                    pagination = null,
                                    contextToken = null
                                )
                            )
                        )
                    } else {
                        MockDataGenerator.generateErrorResponse("ID de sesión inválido")
                    }
                } else {
                    MockDataGenerator.generateErrorResponse("Formato de mensaje incorrecto. Use: 'Pasar lista sesión X: ausentes=1,2,3'")
                }
            }

            // Ver alumnos de una sesión específica (completada/en curso O futura)
            message.contains("alumnos de la sesión", ignoreCase = true) ||
            message.contains("alumnos de la clase", ignoreCase = true) ||
            message.contains("muéstrame los alumnos", ignoreCase = true) && message.contains("sesión", ignoreCase = true) -> {
                
                // ✅ Detectar si es sesión existente (sesion_id) o futura (horario_curso_id)
                val sesionId = (context?.get("sesion_id") as? Number)?.toInt()
                val horarioCursoId = (context?.get("horario_curso_id") as? Number)?.toInt()
                
                MockDataGenerator.generateAlumnosSesionResponse(
                    sesionId = sesionId,
                    horarioCursoId = horarioCursoId,
                    nombreProfesor = nombreProfesor
                )
            }
            
            // Ver clases de la próxima semana
            message.contains("próxima semana", ignoreCase = true) ||
            message.contains("proxima semana", ignoreCase = true) ->
                MockDataGenerator.generateMisClasesProximaSemanaResponse(
                    fechaInicio = fechaHoy,
                    nombreProfesor = nombreProfesor
                )
            
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
            
            // Ver clases del último [día de semana]
            (message.contains("último", ignoreCase = true) || message.contains("ultimo", ignoreCase = true)) && 
            (message.contains("lunes", ignoreCase = true) ||
             message.contains("martes", ignoreCase = true) ||
             message.contains("miércoles", ignoreCase = true) || message.contains("miercoles", ignoreCase = true) ||
             message.contains("jueves", ignoreCase = true) ||
             message.contains("viernes", ignoreCase = true) ||
             message.contains("sábado", ignoreCase = true) || message.contains("sabado", ignoreCase = true) ||
             message.contains("domingo", ignoreCase = true)) -> {
                
                val diaBuscado = when {
                    message.contains("lunes", ignoreCase = true) -> "lunes"
                    message.contains("martes", ignoreCase = true) -> "martes"
                    message.contains("miércoles", ignoreCase = true) || message.contains("miercoles", ignoreCase = true) -> "miércoles"
                    message.contains("jueves", ignoreCase = true) -> "jueves"
                    message.contains("viernes", ignoreCase = true) -> "viernes"
                    message.contains("sábado", ignoreCase = true) || message.contains("sabado", ignoreCase = true) -> "sábado"
                    message.contains("domingo", ignoreCase = true) -> "domingo"
                    else -> "viernes" // default
                }
                
                val fechaCalculada = calcularUltimoDiaSemana(diaBuscado)
                val diaSemana = getDiaSemanaCapitalizado(fechaCalculada)
                val fechaStr = fechaCalculada.toString()
                val fechaLegible = formatearFechaLegible(fechaCalculada)
                
                MockDataGenerator.generateMisClasesHoyResponse(
                    diaSemana = diaSemana,
                    fecha = fechaStr,
                    fechaLegible = fechaLegible,
                    nombreProfesor = nombreProfesor
                )
            }
            
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
     * Calcula la fecha del último [día de semana] pasado.
     * Por ejemplo: si hoy es domingo 16/nov y buscamos "viernes", devuelve viernes 14/nov.
     * Si hoy ES el día buscado, devuelve el anterior (7 días atrás).
     * 
     * @param diaSemana Nombre del día en español ("lunes", "martes", etc.)
     * @return LocalDate del último día encontrado
     */
    private fun calcularUltimoDiaSemana(diaSemana: String): LocalDate {
        val hoy = LocalDate.now()
        val diaTarget = when (diaSemana.lowercase()) {
            "lunes" -> DayOfWeek.MONDAY
            "martes" -> DayOfWeek.TUESDAY
            "miércoles", "miercoles" -> DayOfWeek.WEDNESDAY
            "jueves" -> DayOfWeek.THURSDAY
            "viernes" -> DayOfWeek.FRIDAY
            "sábado", "sabado" -> DayOfWeek.SATURDAY
            "domingo" -> DayOfWeek.SUNDAY
            else -> DayOfWeek.FRIDAY // default
        }
        
        // Empezar desde ayer para evitar que "último viernes" un viernes sea hoy mismo
        var fecha = hoy.minusDays(1)
        
        // Buscar hacia atrás hasta encontrar el día (máximo 7 días)
        var intentos = 0
        while (fecha.dayOfWeek != diaTarget && intentos < 7) {
            fecha = fecha.minusDays(1)
            intentos++
        }
        
        return fecha
    }
    
    /**
     * Obtiene el día de la semana capitalizado en español
     * @param fecha LocalDate
     * @return String con el día capitalizado (ej: "Viernes")
     */
    private fun getDiaSemanaCapitalizado(fecha: LocalDate): String {
        val locale = Locale("es", "ES")
        return fecha.dayOfWeek.getDisplayName(TextStyle.FULL, locale).capitalize()
    }
    
    /**
     * Formatea una fecha en formato legible español
     * @param fecha LocalDate
     * @return String formato "viernes 14 de noviembre de 2025"
     */
    private fun formatearFechaLegible(fecha: LocalDate): String {
        val locale = Locale("es", "ES")
        val diaSemana = fecha.dayOfWeek.getDisplayName(TextStyle.FULL, locale).lowercase()
        val dia = fecha.dayOfMonth
        val mes = fecha.month.getDisplayName(TextStyle.FULL, locale).lowercase()
        val anio = fecha.year
        
        return "$diaSemana $dia de $mes de $anio"
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
