package com.example.academiaapp.data.mock

import com.example.academiaapp.data.remote.dto.*

/**
 * Generador de respuestas mock con el formato Envelope correcto
 * Simula las respuestas del backend para desarrollo
 */
object MockDataGenerator {
    
    // ===============================================
    // ALUMNOS - Respuestas con paginación
    // ===============================================
    
    /**
     * Genera respuesta de lista de alumnos paginada
     * @param page Número de página (1-indexed)
     * @return Envelope con lista de alumnos y sugerencias de paginación
     */
    fun generateAlumnosListResponse(page: Int = 1): Envelope<GenericItem> {
        val totalAlumnos = MockData.getAlumnos().size
        val (alumnos, returned, hasMore) = MockData.getAlumnosPagina(page, size = 50)
        
        // Generar sugerencias de paginación
        val suggestions = mutableListOf<Suggestion>()
        
        // Botón "Página anterior" si no estamos en la primera página
        if (page > 1) {
            suggestions.add(
                Suggestion(
                    id = "sug_prev_page",
                    displayText = "Página anterior",
                    type = "Paginacion",
                    recordAction = null,
                    record = null,
                    pagination = PaginationSuggestion(
                        direction = "prev",
                        page = page - 1,
                        size = 50
                    ),
                    contextToken = null
                )
            )
        }
        
        // Botón "Página siguiente" si hay más páginas
        if (hasMore) {
            suggestions.add(
                Suggestion(
                    id = "sug_next_page",
                    displayText = "Página siguiente",
                    type = "Paginacion",
                    recordAction = null,
                    record = null,
                    pagination = PaginationSuggestion(
                        direction = "next",
                        page = page + 1,
                        size = 50
                    ),
                    contextToken = null
                )
            )
        }
        
        // Sugerencias de acciones
        suggestions.add(
            Suggestion(
                id = "sug_alta_alumno",
                displayText = "Alta nuevo alumno",
                type = "Registro",
                recordAction = "Alta",
                record = null,
                pagination = null,
                contextToken = null
            )
        )
        
        suggestions.add(
            Suggestion(
                id = "sug_modificacion_alumno",
                displayText = "Modificación de alumno",
                type = "Registro",
                recordAction = "Modificacion",
                record = null,
                pagination = null,
                contextToken = null
            )
        )

        suggestions.add(
            Suggestion(
                id = "sug_baja_alumno",
                displayText = "Baja de alumno",
                type = "Registro",
                recordAction = "Baja",
                record = null,
                pagination = null,
                contextToken = null
            )
        )

        suggestions.add(
            Suggestion(
                id = "sug_pagos_pendientes",
                displayText = "Alumnos con pagos pendientes",
                type = "Registro",
                recordAction = "Consulta",
                record = null,
                pagination = null,
                contextToken = null
            )
        )
        
        val totalPages = (totalAlumnos + 49) / 50
        val pageInfo = "Página $page de $totalPages"

        return Envelope(
            status = "success",
            message = "Alumnos de tu Academia\n$pageInfo | Mostrando $returned de $totalAlumnos alumnos",
            data = DataSection(
                type = "alumnos",
                items = alumnos,
                summaryFields = listOf("nombre", "curso"),
                pagination = PaginationInfo(
                    page = page,
                    size = 50,
                    returned = returned,
                    hasMore = hasMore,
                    nextPage = if (hasMore) page + 1 else null,
                    prevPage = if (page > 1) page - 1 else null,
                    total = totalAlumnos
                )
            ),
            uiSuggestions = suggestions
        )
    }
    
    /**
     * Genera respuesta con detalle de un alumno específico
     * @param alumnoId ID del alumno
     * @return Envelope con datos del alumno
     */
    fun generateAlumnoDetailResponse(alumnoId: Int): Envelope<GenericItem> {
        val alumno = MockData.getAlumno(alumnoId) 
            ?: return generateErrorResponse("Alumno no encontrado")
        
        return Envelope(
            status = "success",
            message = "Detalle del alumno ${alumno["nombre"]}",
            data = DataSection(
                type = "alumno_detalle",
                items = listOf(alumno),
                summaryFields = null,
                pagination = null
            ),
            uiSuggestions = listOf(
                Suggestion(
                    id = "sug_modificar_alumno",
                    displayText = "Modificar alumno",
                    type = "Registro",
                    recordAction = "Modificacion",
                    record = RecordRef(
                        resource = "alumnos",
                        id = alumnoId.toString()
                    ),
                    pagination = null,
                    contextToken = null
                ),
                Suggestion(
                    id = "sug_volver_lista",
                    displayText = "Volver a lista de alumnos",
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
     * Genera respuesta de confirmación de alta de alumno
     * @param nuevoAlumno Datos del alumno creado (sin ID)
     * @return Envelope con confirmación de creación
     */
    fun generateAltaAlumnoSuccessResponse(nuevoAlumno: Map<String, Any?>): Envelope<GenericItem> {
        // Crear alumno sin forzar enlace a curso (no requerimos FK en el mock)
        val alumnoParaCrear = nuevoAlumno.toMutableMap().apply {
            put("estado_pago", "Al día")
        }

        val newId = MockData.addAlumno(alumnoParaCrear)

        val mensajeExito = buildString {
            append("Alta de nuevo alumno:\n\n")
            nuevoAlumno["nombre"]?.let { append("Nombre: $it\n") }
            nuevoAlumno["email"]?.let { if (it.toString().isNotBlank()) append("Email: $it\n") }
            nuevoAlumno["dni"]?.let { if (it.toString().isNotBlank()) append("DNI: $it\n") }
            nuevoAlumno["telefono"]?.let { append("Teléfono: $it\n") }
            nuevoAlumno["fecha_nacimiento"]?.let { append("Fecha de nacimiento: $it\n") }
            nuevoAlumno["direccion"]?.let { if (it.toString().isNotBlank()) append("Dirección: $it\n") }
            nuevoAlumno["curso_id"]?.let { 
                val cursoId = (it as? Number)?.toInt()
                if (cursoId != null) {
                    val curso = MockData.getCurso(cursoId)
                    curso?.let { c -> append("Curso: ${c["nombre"]}\n") }
                }
            }
            append("\n✓ El alumno ha sido dado de alta correctamente con ID: $newId")
        }

        return Envelope(
            status = "success",
            message = mensajeExito,
            data = null,
            uiSuggestions = listOf(
                Suggestion(
                    id = "sug_ver_alumno",
                    displayText = "Ver detalle del alumno creado",
                    type = "Registro",
                    recordAction = "Consulta",
                    record = RecordRef(
                        resource = "alumnos",
                        id = newId.toString()
                    ),
                    pagination = null,
                    contextToken = null
                ),
                Suggestion(
                    id = "sug_volver_lista",
                    displayText = "Volver a lista de alumnos",
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
     * Genera respuesta filtrada: alumnos con pagos pendientes
     * @return Envelope con lista filtrada mostrando nombre y deuda
     */
    fun generateAlumnosPagosPendientesResponse(): Envelope<GenericItem> {
        val alumnosConDeuda = MockData.getAlumnosConPagosPendientes()
        
        val totalDeuda = alumnosConDeuda.sumOf { 
            (it["deuda_euros"] as? Number)?.toInt() ?: 0 
        }
        
        return Envelope(
            status = "success",
            message = "Alumnos con pagos pendientes\nTotal: ${alumnosConDeuda.size} alumnos deben $totalDeuda€",
            data = DataSection(
                type = "alumnos_deuda",
                items = alumnosConDeuda,
                summaryFields = listOf("nombre", "deuda_euros"),
                pagination = null
            ),
            uiSuggestions = listOf(
                Suggestion(
                    id = "sug_volver",
                    displayText = "Volver a lista completa",
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
     * Genera formulario de modificación de alumno con datos pre-cargados
     * @param alumnoId ID del alumno a modificar
     * @return Envelope con formulario y datos del alumno
     */
    fun generateModificacionAlumnoForm(alumnoId: Int): Envelope<GenericItem> {
        val alumno = MockData.getAlumno(alumnoId)
            ?: return generateErrorResponse("Alumno no encontrado")

        val cursosCombo = MockData.getCursos().map { curso ->
            mapOf(
                "id" to curso["id"],
                "display_text" to "${curso["nombre"]} (${curso["fecha_inicio"]} - ${curso["fecha_fin"]})"
            )
        }

        return Envelope(
            status = "success",
            message = "Modificación de alumno - ${alumno["nombre"]}",
            data = DataSection(
                type = "formulario_modificacion_alumno",
                items = listOf(
                    mapOf(
                        "field_type" to "form",
                        "alumno_data" to alumno,
                        "cursos_disponibles" to cursosCombo
                    )
                ),
                summaryFields = null,
                pagination = null
            ),
            uiSuggestions = null  // El formulario tendrá sus propios botones
        )
    }

    /**
     * Genera respuesta de confirmación de modificación de alumno
     * @param alumnoId ID del alumno modificado
     * @param nuevosDatos Datos actualizados del alumno
     * @return Envelope con confirmación de modificación
     */
    fun generateModificacionAlumnoSuccess(alumnoId: Int, nuevosDatos: Map<String, Any?>): Envelope<GenericItem> {
        val alumnoActual = MockData.getAlumno(alumnoId)
            ?: return generateErrorResponse("Alumno no encontrado")

        // Actualizar alumno en el mock
        MockData.updateAlumno(alumnoId, nuevosDatos)

        val mensajeExito = buildString {
            append("Modificación de alumno completada:\n\n")
            nuevosDatos["nombre"]?.let { append("Nombre: $it\n") }
            nuevosDatos["email"]?.let { if (it.toString().isNotBlank()) append("Email: $it\n") }
            nuevosDatos["dni"]?.let { if (it.toString().isNotBlank()) append("DNI: $it\n") }
            nuevosDatos["telefono"]?.let { append("Teléfono: $it\n") }
            nuevosDatos["fecha_nacimiento"]?.let { append("Fecha de nacimiento: $it\n") }
            nuevosDatos["direccion"]?.let { if (it.toString().isNotBlank()) append("Dirección: $it\n") }
            nuevosDatos["curso_id"]?.let {
                val cursoId = (it as? Number)?.toInt()
                if (cursoId != null) {
                    val curso = MockData.getCurso(cursoId)
                    curso?.let { c -> append("Curso: ${c["nombre"]}\n") }
                }
            }
            append("\n✓ Los datos del alumno han sido actualizados correctamente")
        }

        return Envelope(
            status = "success",
            message = mensajeExito,
            data = null,
            uiSuggestions = listOf(
                Suggestion(
                    id = "sug_ver_alumno",
                    displayText = "Ver detalle del alumno",
                    type = "Registro",
                    recordAction = "Consulta",
                    record = RecordRef(
                        resource = "alumnos",
                        id = alumnoId.toString()
                    ),
                    pagination = null,
                    contextToken = null
                ),
                Suggestion(
                    id = "sug_volver_lista",
                    displayText = "Volver a lista de alumnos",
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
     * Genera formulario de confirmación de baja de alumno
     * @param alumnoId ID del alumno a dar de baja
     * @return Envelope con datos del alumno para confirmación
     */
    fun generateBajaAlumnoForm(alumnoId: Int): Envelope<GenericItem> {
        val alumno = MockData.getAlumno(alumnoId)
            ?: return generateErrorResponse("Alumno no encontrado")

        return Envelope(
            status = "success",
            message = "⚠️ Confirmación de baja de alumno\n\n¿Estás seguro de dar de baja a este alumno?",
            data = DataSection(
                type = "formulario_baja_alumno",
                items = listOf(
                    mapOf(
                        "field_type" to "form_readonly",
                        "alumno_data" to alumno
                    )
                ),
                summaryFields = null,
                pagination = null
            ),
            uiSuggestions = null  // El formulario tendrá sus propios botones
        )
    }

    /**
     * Genera respuesta de confirmación de baja de alumno
     * @param alumnoNombre Nombre del alumno dado de baja
     * @param alumnoId ID del alumno dado de baja
     * @return Envelope con confirmación de baja
     * ✅ PASO 5: Actualizada para aceptar nombre como parámetro y mostrar mensaje personalizado
     */
    fun generateBajaAlumnoSuccess(alumnoNombre: String, alumnoId: Int): Envelope<GenericItem> {
        return Envelope(
            status = "success",
            message = "Baja procesada correctamente. El alumno $alumnoNombre (ID: $alumnoId) ha sido eliminado del sistema.",
            data = null,
            uiSuggestions = listOf(
                Suggestion(
                    id = "sug_listar_alumnos",
                    displayText = "Listar alumnos",
                    type = "Generica",
                    recordAction = null,
                    record = null,
                    pagination = null,
                    contextToken = null
                )
            )
        )
    }

    // ===============================================
    // CURSOS - Respuestas (sin paginación)
    // ===============================================
    
    /**
     * Genera respuesta con lista completa de cursos
     * @return Envelope con lista de cursos
     */
    fun generateCursosListResponse(): Envelope<GenericItem> {
        val cursos = MockData.getCursos()
        
        return Envelope(
            status = "success",
            message = "Cursos de tu Academia\nTotal: ${cursos.size} cursos activos",
            data = DataSection(
                type = "cursos",
                items = cursos,
                summaryFields = listOf("nombre", "alumnos_inscritos"),
                pagination = null
            ),
            uiSuggestions = listOf(
                Suggestion(
                    id = "sug_crear_curso",
                    displayText = "Crear nuevo curso",
                    type = "Registro",
                    recordAction = "Alta",
                    record = null,
                    pagination = null,
                    contextToken = null
                )
            )
        )
    }
    
    /**
     * Genera respuesta con detalle de un curso específico
     * @param cursoId ID del curso
     * @return Envelope con datos del curso
     */
    fun generateCursoDetailResponse(cursoId: Int): Envelope<GenericItem> {
        val curso = MockData.getCurso(cursoId) 
            ?: return generateErrorResponse("Curso no encontrado")
        
        return Envelope(
            status = "success",
            message = "Detalle del curso ${curso["nombre"]}",
            data = DataSection(
                type = "curso_detalle",
                items = listOf(curso),
                summaryFields = null,
                pagination = null
            ),
            uiSuggestions = listOf(
                Suggestion(
                    id = "sug_ver_alumnos",
                    displayText = "Ver alumnos inscritos",
                    type = "Generica",
                    recordAction = null,
                    record = null,
                    pagination = null,
                    contextToken = null
                ),
                Suggestion(
                    id = "sug_volver_lista",
                    displayText = "Volver a lista de cursos",
                    type = "Generica",
                    recordAction = null,
                    record = null,
                    pagination = null,
                    contextToken = null
                )
            )
        )
    }
    
    // ===============================================
    // RESPUESTAS GENÉRICAS
    // ===============================================
    
    /**
     * Genera respuesta genérica para mensajes no reconocidos
     * @param message Mensaje del usuario
     * @return Envelope con respuesta genérica
     */
    fun generateGenericResponse(message: String): Envelope<GenericItem> {
        return Envelope(
            status = "success",
            message = "He recibido tu mensaje: \"$message\"\n\nAún no tengo una respuesta específica para esto.",
            data = null,
            uiSuggestions = null
        )
    }
    
    /**
     * Genera respuesta de error
     * @param error Mensaje de error
     * @return Envelope con status error
     */
    fun generateErrorResponse(error: String): Envelope<GenericItem> {
        return Envelope(
            status = "error",
            message = "❌ $error",
            data = null,
            uiSuggestions = null
        )
    }
    
    // ===============================================
    // SESIONES - Respuestas de clases del profesor
    // ===============================================
    
    /**
     * Genera respuesta con las clases del día especificado
     * @param diaSemana Día de la semana ("Lunes", "Martes", etc.)
     * @param fecha Fecha en formato "YYYY-MM-DD" (para buscar sesiones)
     * @param fechaLegible Fecha legible para mostrar ("Miércoles 23 de Octubre de 2025")
     * @return Envelope con lista de clases con estados y acciones
     */
    fun generateMisClasesHoyResponse(
        diaSemana: String,
        fecha: String,
        fechaLegible: String,
        nombreProfesor: String = "María García"
    ): Envelope<GenericItem> {
        val horarios = MockData.getHorariosProfesor(diaSemana)
        
        if (horarios.isEmpty()) {
            return Envelope(
                status = "success",
                message = "$fechaLegible\n\nNo tienes clases programadas para este día.",
                data = null,
                uiSuggestions = listOf(
                    Suggestion(
                        id = "sug_ver_semana",
                        displayText = "Ver toda la semana",
                        type = "Generica",
                        recordAction = null,
                        record = null,
                        pagination = null,
                        contextToken = null
                    )
                )
            )
        }
        
        // Generar items enriquecidos con estado de sesión
        val clasesConEstado = horarios.map { horario ->
            val horarioId = horario["id"] as Int
            val cursoId = horario["curso_id"] as Int
            val aulaId = horario["aula_id"] as Int
            val horaInicio = horario["hora_inicio"] as String
            val horaFin = horario["hora_fin"] as String
            
            val curso = MockData.getCurso(cursoId)
            val aula = MockData.getAula(aulaId)
            val sesion = MockData.getSesionDinamica(horarioId, fecha)
            
            // Extraer datos de asistencia si existe sesión
            val asistieron = (sesion?.get("alumnos_asistieron") as? Number)?.toInt() ?: 0
            val total = (sesion?.get("total_alumnos") as? Number)?.toInt() ?: (curso?.get("alumnos_inscritos") as? Number)?.toInt() ?: 0
            
            // Determinar estado
            val (estado, icono, descripcionEstado, acciones) = when {
                sesion == null -> {
                    // 🟡 Programada - No se ha iniciado
                    Quadruple(
                        "programada",
                        "🟡",
                        "No iniciada",
                        emptyList()  // Sin acciones disponibles
                    )
                }
                sesion["timestamp_baja"] == null -> {
                    // 🟢 En curso - Iniciada pero no finalizada
                    val timestampAlta = sesion["timestamp_alta"] as String
                    val horaInicioReal = timestampAlta.substring(11, 16) // HH:MM
                    val listaPasada = sesion["lista_pasada"] as? Boolean ?: false
                    
                    val listaInfo = if (listaPasada) {
                        "✅ Lista pasada: $asistieron/$total alumnos"
                    } else {
                        "⚠️ Lista pendiente"
                    }
                    
                    Quadruple(
                        "en_curso",
                        "🟢",
                        "$nombreProfesor\nIniciada a las $horaInicioReal | $listaInfo",
                        listOf("Ver alumnos", "Ver anotaciones")
                    )
                }
                else -> {
                    // ✅ Completada
                    val timestampAlta = sesion["timestamp_alta"] as String
                    val timestampBaja = sesion["timestamp_baja"] as String
                    val horaInicioReal = timestampAlta.substring(11, 16)
                    val horaFinReal = timestampBaja.substring(11, 16)
                    val listaPasada = sesion["lista_pasada"] as? Boolean ?: false
                    
                    val listaInfo = if (listaPasada) {
                        "✅ Lista pasada: $asistieron/$total alumnos"
                    } else {
                        "⚠️ Lista no pasada"
                    }
                    
                    Quadruple(
                        "completada",
                        "✅",
                        "$nombreProfesor\nCompletada ($horaInicioReal - $horaFinReal) | $listaInfo",
                        listOf("Ver alumnos", "Ver anotaciones")
                    )
                }
            }
            
            mapOf(
                "id" to horarioId,
                "sesion_id" to (sesion?.get("id")),
                "estado" to estado,
                "icono" to icono,
                "hora_inicio" to horaInicio,
                "hora_fin" to horaFin,
                "curso" to (curso?.get("nombre") ?: "Curso $cursoId"),
                "curso_id" to cursoId,
                "aula" to (aula?.get("nombre") ?: "Aula $aulaId"),
                "alumnos" to total,  // Total de alumnos de la sesión
                "alumnos_asistieron" to asistieron,  // Alumnos que asistieron
                "descripcion_estado" to descripcionEstado,
                "acciones_disponibles" to acciones
            )
        }
        
        // Generar sugerencias según el día
        val suggestions = mutableListOf<Suggestion>()
        
        // Sugerencias de navegación temporal
        if (diaSemana != "Lunes") {
            suggestions.add(
                Suggestion(
                    id = "sug_clases_ayer",
                    displayText = "Ver clases de ayer",
                    type = "Generica",
                    recordAction = null,
                    record = null,
                    pagination = null,
                    contextToken = null
                )
            )
        }
        
        if (diaSemana != "Viernes") {
            suggestions.add(
                Suggestion(
                    id = "sug_clases_manana",
                    displayText = "Ver clases de mañana",
                    type = "Generica",
                    recordAction = null,
                    record = null,
                    pagination = null,
                    contextToken = null
                )
            )
        }
        
        suggestions.add(
            Suggestion(
                id = "sug_ver_semana",
                displayText = "Ver toda la semana",
                type = "Generica",
                recordAction = null,
                record = null,
                pagination = null,
                contextToken = null
            )
        )
        
        return Envelope(
            status = "success",
            message = "$fechaLegible\n\nTienes ${horarios.size} clase(s) programada(s)",
            data = DataSection(
                type = "sesiones_dia",
                items = clasesConEstado,
                summaryFields = null,
                pagination = null
            ),
            uiSuggestions = suggestions
        )
    }
    
    /**
     * Genera respuesta semanal con todas las clases de la semana
     * @param fechaInicio Fecha de inicio de semana (formato "YYYY-MM-DD")
     * @param nombreProfesor Nombre del profesor logueado
     * @return Envelope con calendario semanal
     */
    fun generateMisClasesSemanalesResponse(
        fechaInicio: String,
        nombreProfesor: String = "María García"
    ): Envelope<GenericItem> {
        val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")
        
        val clasesPorDia = diasSemana.map { dia ->
            val horarios = MockData.getHorariosProfesor(dia)
            mapOf(
                "dia" to dia,
                "cantidad" to horarios.size,
                "horarios" to horarios
            )
        }
        
        val totalClases = clasesPorDia.sumOf { it["cantidad"] as Int }
        
        // Calcular rango de fechas de la semana
        val fecha = java.time.LocalDate.parse(fechaInicio)
        val inicioSemana = fecha.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
        val finSemana = inicioSemana.plusDays(6)
        
        val meses = listOf("", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                           "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
        val mesInicio = meses[inicioSemana.monthValue]
        val mesFin = meses[finSemana.monthValue]
        
        val rangoFecha = if (inicioSemana.month == finSemana.month) {
            "Semana del ${inicioSemana.dayOfMonth} al ${finSemana.dayOfMonth} de $mesInicio"
        } else {
            "Semana del ${inicioSemana.dayOfMonth} de $mesInicio al ${finSemana.dayOfMonth} de $mesFin"
        }
        
        return Envelope(
            status = "success",
            message = "$rangoFecha\n\nTienes $totalClases clases esta semana",
            data = DataSection(
                type = "sesiones_semana",
                items = clasesPorDia,
                summaryFields = null,
                pagination = null
            ),
            uiSuggestions = listOf(
                Suggestion(
                    id = "sug_clases_hoy",
                    displayText = "Ver clases de hoy",
                    type = "Generica",
                    recordAction = null,
                    record = null,
                    pagination = null,
                    contextToken = null
                )
            )
        )
    }
}

/**
 * Helper para tuplas de 4 elementos (Kotlin no tiene Quadruple)
 */
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
