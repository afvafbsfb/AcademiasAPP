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
    fun generateBajaAlumnoForm(): Envelope<GenericItem> {
        val alumno = MockData.getAlumnos().firstOrNull()
            ?: return generateErrorResponse("No hay alumnos disponibles para dar de baja")

        val alumnoId = (alumno["id"] as? Int) ?: 0
        val alumnoNombre = alumno["nombre"] as? String ?: "Alumno"
        val alumnoEmail = alumno["email"] as? String ?: ""
        val alumnoDni = alumno["dni"] as? String ?: ""
        val alumnoTelefono = alumno["telefono"] as? String ?: ""
        val alumnoCurso = alumno["curso"] as? String ?: ""

        val detallesAlumno = buildString {
            append("Baja de alumno:\n\n")
            append("ID: $alumnoId\n")
            append("Nombre: $alumnoNombre\n")
            if (alumnoDni.isNotBlank()) append("DNI: $alumnoDni\n")
            if (alumnoEmail.isNotBlank()) append("Email: $alumnoEmail\n")
            if (alumnoTelefono.isNotBlank()) append("Teléfono: $alumnoTelefono\n")
            if (alumnoCurso.isNotBlank()) append("Curso: $alumnoCurso\n")
        }

        return Envelope(
            status = "success",
            message = "Confirmación de baja Alumno",
            data = DataSection(
                type = "formulario_baja_alumno",
                items = listOf(
                    mapOf(
                        "field_type" to "form_readonly",
                        "alumno_data" to alumno,
                        "texto_confirmacion" to detallesAlumno
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
            message = "Baja del alumno $alumnoNombre realizada correctamente.",
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
        nombreProfesor: String = "Profesor"  // ✅ Default genérico, se sobrescribe con usuario logueado
    ): Envelope<GenericItem> {
        val horarios = MockData.getHorariosProfesor(diaSemana)
        
        if (horarios.isEmpty()) {
            return Envelope(
                status = "success",
                message = "$fechaLegible\n\nNo tienes clases programadas para este día.",
                data = null,
                uiSuggestions = listOf(
                    Suggestion(
                        id = "sug_clases_manana",
                        displayText = "Ver clases de mañana",
                        type = "Generica",
                        recordAction = null,
                        record = null,
                        pagination = null,
                        contextToken = null
                    ),
                    Suggestion(
                        id = "sug_proxima_semana",
                        displayText = "Ver clases de la próxima semana",
                        type = "Generica",
                        recordAction = null,
                        record = null,
                        pagination = null,
                        contextToken = null
                    ),
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
            
            // 🔧 DEBUG: Ver qué sesión se obtuvo
            println("🔧 DEBUG generateMisClasesHoyResponse - horarioId=$horarioId, fecha=$fecha")
            println("🔧 DEBUG - sesion obtenida: ${sesion?.get("id")}, timestamp_alta=${sesion?.get("timestamp_alta")}")
            
            // Extraer datos de asistencia si existe sesión
            val asistieron = (sesion?.get("alumnos_asistieron") as? Number)?.toInt() ?: 0
            val total = (sesion?.get("total_alumnos") as? Number)?.toInt() ?: (curso?.get("alumnos_inscritos") as? Number)?.toInt() ?: 0
            
            // ✅ USAR HORAS DEMO si existen (sesión testeable con hora actual)
            val horaInicioStr = (sesion?.get("hora_inicio_demo") as? String) ?: horaInicio
            val horaFinStr = (sesion?.get("hora_fin_demo") as? String) ?: horaFin
            
            // ✅ NUEVO: Calcular si estamos dentro de la ventana de ±1h para poder iniciar
            val ahora = java.time.LocalDateTime.now()
            val fechaHoy = ahora.toLocalDate()
            val horaActual = ahora.toLocalTime()
            val fechaBuscada = java.time.LocalDate.parse(fecha)
            
            val horaInicioTime = java.time.LocalTime.parse(horaInicioStr)
            val horaFinTime = java.time.LocalTime.parse(horaFinStr)
            
            // Ventana de inicio: 1h antes de inicio hasta 1h después de fin
            val esHoy = fechaBuscada.isEqual(fechaHoy)
            val dentroVentana = esHoy && 
                horaActual.isAfter(horaInicioTime.minusHours(1)) && 
                horaActual.isBefore(horaFinTime.plusHours(1))
            
            // Determinar estado
            val (estado, icono, descripcionEstado, acciones) = when {
                sesion == null || sesion["timestamp_alta"] == null -> {
                    // 🟡 Programada - No se ha iniciado (incluye sesión DEMO)
                    // Mostrar botón "Iniciar" SOLO si está dentro de la ventana ±1h
                    val accionesDisponibles = if (dentroVentana) {
                        listOf("Iniciar", "Ver alumnos", "Ver anotaciones")
                    } else {
                        listOf("Ver alumnos", "Ver anotaciones") // Futura: sin iniciar
                    }
                    
                    Quadruple(
                        "programada",
                        "🟡",
                        "No iniciada",
                        accionesDisponibles
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
                "fecha" to fecha,  // ✅ Fecha de la sesión
                "hora_inicio" to horaInicioStr,  // ✅ Usar hora demo si existe
                "hora_fin" to horaFinStr,        // ✅ Usar hora demo si existe
                "curso" to (curso?.get("nombre") ?: "Curso $cursoId"),
                "curso_id" to cursoId,
                "aula" to (aula?.get("nombre") ?: "Aula $aulaId"),
                "alumnos" to total,  // Total de alumnos de la sesión
                "alumnos_asistieron" to asistieron,  // Alumnos que asistieron
                "descripcion_estado" to descripcionEstado,
                "acciones_disponibles" to acciones
            ).also { item ->
                // 🔧 DEBUG: Ver qué se devuelve en cada item
                println("🔧 DEBUG - Item generado: id=${item["id"]}, sesion_id=${item["sesion_id"]}, curso=${item["curso"]}, estado=${item["estado"]}")
            }
        }
        
        // Generar sugerencias según el día
        val suggestions = mutableListOf<Suggestion>()
        
        // Sugerencias de navegación temporal - SIEMPRE mostrar opciones de ayer/mañana
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
        
        suggestions.add(
            Suggestion(
                id = "sug_proxima_semana",
                displayText = "Ver clases de la próxima semana",
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
     * @param nombreProfesor Nombre del profesor logueado (obtenido de SessionStore en runtime)
     * @return Envelope con calendario semanal
     */
    fun generateMisClasesSemanalesResponse(
        fechaInicio: String,
        nombreProfesor: String = "Profesor"  // ✅ Default genérico, se sobrescribe con usuario logueado
    ): Envelope<GenericItem> {
        val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")
        
        val clasesPorDia = diasSemana.map { dia ->
            val horarios = MockData.getHorariosProfesor(dia)
            
            // Enriquecer horarios con nombres de curso y aula
            val horariosEnriquecidos = horarios.map { horario ->
                val cursoId = horario["curso_id"] as Int
                val aulaId = horario["aula_id"] as Int
                val curso = MockData.getCurso(cursoId)
                val aula = MockData.getAula(aulaId)
                
                horario.toMutableMap().apply {
                    put("curso", curso?.get("nombre") ?: "Curso $cursoId")
                    put("aula", aula?.get("nombre") ?: "Aula $aulaId")
                }
            }
            
            mapOf(
                "dia" to dia,
                "cantidad" to horariosEnriquecidos.size,
                "horarios" to horariosEnriquecidos
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
                ),
                Suggestion(
                    id = "sug_proxima_semana",
                    displayText = "Ver clases de la próxima semana",
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
     * Genera respuesta semanal con todas las clases de la PRÓXIMA semana
     * @param fechaInicio Fecha de referencia (formato "YYYY-MM-DD")
     * @param nombreProfesor Nombre del profesor logueado (obtenido de SessionStore en runtime)
     * @return Envelope con calendario de la próxima semana
     */
    fun generateMisClasesProximaSemanaResponse(
        fechaInicio: String,
        nombreProfesor: String = "Profesor"  // ✅ Default genérico, se sobrescribe con usuario logueado
    ): Envelope<GenericItem> {
        val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")
        
        val clasesPorDia = diasSemana.map { dia ->
            val horarios = MockData.getHorariosProfesor(dia)
            
            // Enriquecer horarios con nombres de curso y aula
            val horariosEnriquecidos = horarios.map { horario ->
                val cursoId = horario["curso_id"] as Int
                val aulaId = horario["aula_id"] as Int
                val curso = MockData.getCurso(cursoId)
                val aula = MockData.getAula(aulaId)
                
                horario.toMutableMap().apply {
                    put("curso", curso?.get("nombre") ?: "Curso $cursoId")
                    put("aula", aula?.get("nombre") ?: "Aula $aulaId")
                }
            }
            
            mapOf(
                "dia" to dia,
                "cantidad" to horariosEnriquecidos.size,
                "horarios" to horariosEnriquecidos
            )
        }
        
        val totalClases = clasesPorDia.sumOf { it["cantidad"] as Int }
        
        // Calcular rango de fechas de la PRÓXIMA semana
        val fecha = java.time.LocalDate.parse(fechaInicio)
        val inicioSemanaActual = fecha.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
        val inicioProximaSemana = inicioSemanaActual.plusWeeks(1) // +7 días desde el lunes de esta semana
        val finProximaSemana = inicioProximaSemana.plusDays(6)
        
        val meses = listOf("", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                           "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
        val mesInicio = meses[inicioProximaSemana.monthValue]
        val mesFin = meses[finProximaSemana.monthValue]
        
        val rangoFecha = if (inicioProximaSemana.month == finProximaSemana.month) {
            "Próxima semana del ${inicioProximaSemana.dayOfMonth} al ${finProximaSemana.dayOfMonth} de $mesInicio"
        } else {
            "Próxima semana del ${inicioProximaSemana.dayOfMonth} de $mesInicio al ${finProximaSemana.dayOfMonth} de $mesFin"
        }
        
        return Envelope(
            status = "success",
            message = "$rangoFecha\n\nTienes $totalClases clases programadas",
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
                ),
                Suggestion(
                    id = "sug_semana_actual",
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
    
    // ===============================================
    // ALUMNOS DE SESIÓN - Lista de asistencias
    // ===============================================
    
    /**
     * Genera respuesta con los alumnos de una sesión específica
     * Incluye información de asistencia y anotaciones por alumno
     * 
     * @param sesionId ID de la sesión
     * @param nombreProfesor Nombre del profesor logueado
     * @return Envelope con cabecera de sesión y lista de alumnos con asistencias
     */
    /**
     * Genera respuesta de alumnos de una sesión.
     * Acepta sesionId (sesión ya creada) O horarioCursoId (sesión futura).
     * 
     * @param sesionId ID de sesión completada/en curso (opcional)
     * @param horarioCursoId ID de horario para sesión futura (opcional)
     * @param nombreProfesor Nombre del profesor
     * @return Envelope con lista de alumnos y datos de sesión/horario
     */
    fun generateAlumnosSesionResponse(
        sesionId: Int? = null,
        horarioCursoId: Int? = null,
        nombreProfesor: String = "Profesor"
    ): Envelope<GenericItem> {
        
        // VALIDACIÓN: Debe recibir sesionId O horarioCursoId (no ambos, no ninguno)
        if (sesionId == null && horarioCursoId == null) {
            return Envelope(
                status = "error",
                message = "Debe proporcionar sesionId o horarioCursoId",
                data = null,
                uiSuggestions = emptyList()
            )
        }
        
        // ========== CASO 1: SESIÓN EXISTENTE (completada o en curso) ==========
        if (sesionId != null) {
            val sesion = MockData.getSesionById(sesionId)
                ?: return Envelope(
                    status = "error",
                    message = "No se encontró la sesión con ID $sesionId",
                    data = null,
                    uiSuggestions = emptyList()
                )
            
            val horarioId = sesion["horario_curso_id"] as Int
            val horario = MockData.getHorarioById(horarioId)
                ?: return Envelope(
                    status = "error",
                    message = "No se encontró el horario de la sesión",
                    data = null,
                    uiSuggestions = emptyList()
                )
            
            val cursoId = horario["curso_id"] as Int
            val aulaId = horario["aula_id"] as Int
            val curso = MockData.getCurso(cursoId)
            val aula = MockData.getAula(aulaId)
            val alumnosDelCurso = MockData.getAlumnosByCursoId(cursoId)
            
            // Determinar estado de la sesión
            val timestampAlta = sesion["timestamp_alta"] as? String
            val timestampBaja = sesion["timestamp_baja"] as? String
            val listaPasada = sesion["lista_pasada"] as? Boolean ?: false
            
            // ✅ CALCULAR asistencia desde anotaciones (en lugar de usar campos precalculados)
            val asistenciaInfo = MockData.calcularAsistenciaSesion(sesionId)
            val (asistieron, ausentes, totalAlumnos) = asistenciaInfo ?: Triple(0, 0, alumnosDelCurso.size)
            
            // ✅ Extraer fecha: de timestamp_alta (si iniciada) o de fecha_sesion (si programada)
            val fecha = when {
                timestampAlta != null -> timestampAlta.substring(0, 10)  // Sesión iniciada: usar timestamp
                else -> sesion["fecha_sesion"] as? String ?: ""  // Sesión programada: usar fecha_sesion
            }
            
            // Determinar estado textual
            val estado = when {
                timestampBaja != null -> "completada"
                timestampAlta != null -> "en_curso"
                else -> "programada"
            }
            
            // ✅ GENERAR lista de alumnos usando anotaciones reales de tipo 'Ausencia'
            val alumnosConAsistencia = when {
                // SESIÓN FUTURA o SIN LISTA PASADA
                !listaPasada -> {
                    alumnosDelCurso.map { alumno ->
                        mapOf(
                            "id" to alumno["id"],
                            "nombre" to alumno["nombre"],
                            "asistio" to null,  // Sin marcar aún
                            "tiene_anotaciones" to false
                        )
                    }
                }
                
                // SESIÓN CON LISTA PASADA (en curso o completada)
                else -> {
                    // Obtener anotaciones de esta sesión
                    val anotacionesSesion = MockData.getAnotacionesBySesion(sesionId)
                    
                    // IDs de alumnos con anotaciones de tipo 'Ausencia'
                    val alumnosAusentes = anotacionesSesion
                        .filter { (it["tipo_anotacion"] as? String) == "Ausencia" }
                        .map { it["alumno_id"] as Int }
                        .toSet()
                    
                    // IDs de alumnos con otras anotaciones (Evaluacion, Comportamiento)
                    val alumnosConOtrasAnotaciones = anotacionesSesion
                        .filter { (it["tipo_anotacion"] as? String) != "Ausencia" }
                        .map { it["alumno_id"] as Int }
                        .toSet()
                    
                    alumnosDelCurso.map { alumno ->
                        val alumnoId = alumno["id"] as Int
                        val asistio = alumnoId !in alumnosAusentes  // ✅ Presente = NO tiene ausencia
                        val tieneAnotaciones = alumnoId in alumnosConOtrasAnotaciones
                        
                        mapOf(
                            "id" to alumnoId,
                            "nombre" to alumno["nombre"],
                            "asistio" to asistio,
                            "tiene_anotaciones" to tieneAnotaciones
                        )
                    }
                }
            }
            
            // Construir información de cabecera de sesión
            // ✅ Usar horas demo si existen (sesión demo programada), sino usar horas del horario
            val horaInicio = (sesion["hora_inicio_demo"] as? String) ?: (horario["hora_inicio"] as? String ?: "")
            val horaFin = (sesion["hora_fin_demo"] as? String) ?: (horario["hora_fin"] as? String ?: "")
            
            val sesionInfo = mapOf(
                "sesion_id" to sesionId,
                "hora_inicio" to horaInicio,
                "hora_fin" to horaFin,
                "curso" to (curso?.get("nombre") ?: "Curso $cursoId"),
                "curso_id" to cursoId,
                "aula" to (aula?.get("nombre") ?: "Aula $aulaId"),
                "aula_id" to aulaId,
                "profesor" to nombreProfesor,
                "estado" to estado,
                "fecha" to fecha,
                "alumnos_total" to totalAlumnos,
                "alumnos_asistieron" to asistieron,
                "lista_pasada" to listaPasada,
                "editable" to (estado == "en_curso"),  // ✅ Solo editable si está EN CURSO (iniciada pero no completada)
                "alumnos_inscritos" to (curso?.get("alumnos_inscritos") as? Int ?: totalAlumnos)  // ✅ Total inscritos
            )
            
            // Construir item completo (sesion_info + alumnos)
            val dataItem = mapOf(
                "sesion_info" to sesionInfo,
                "alumnos" to alumnosConAsistencia
            )
            
            // Generar sugerencias
            val suggestions = mutableListOf<Suggestion>()
            
            suggestions.add(
                Suggestion(
                    id = "sug_volver_clases_hoy",
                    displayText = "Volver a clases de hoy",
                    type = "Generica",
                    recordAction = null,
                    record = null,
                    pagination = null,
                    contextToken = null
                )
            )
            
            suggestions.add(
                Suggestion(
                    id = "sug_ver_clases_semana",
                    displayText = "Ver toda la semana",
                    type = "Generica",
                    recordAction = null,
                    record = null,
                    pagination = null,
                    contextToken = null
                )
            )
            
            val message = """
                Alumnos de la sesión
                
                Curso: ${curso?.get("nombre")}
                Fecha: $fecha
                Horario: $horaInicio - $horaFin
                Aula: ${aula?.get("nombre")}
                Profesor: $nombreProfesor
                Estado: ${when(estado) {
                    "completada" -> "Completada"
                    "en_curso" -> "En curso"
                    else -> "Programada"
                }}
            """.trimIndent()
            
            return Envelope(
                status = "success",
                message = message,
                data = DataSection(
                    type = "alumnos_sesion",
                    items = listOf(dataItem),
                    summaryFields = null,
                    pagination = null
                ),
                uiSuggestions = suggestions
            )
        }
        
        // ========== CASO 2: SESIÓN FUTURA (solo horarioCursoId) ==========
        else {
            val horarioIdFutura = horarioCursoId!!  // Ya validamos que no es null
            val horario = MockData.getHorarioById(horarioIdFutura)
                ?: return Envelope(
                    status = "error",
                    message = "No se encontró el horario con ID $horarioIdFutura",
                    data = null,
                    uiSuggestions = emptyList()
                )
            
            val cursoId = horario["curso_id"] as Int
            val aulaId = horario["aula_id"] as Int
            val curso = MockData.getCurso(cursoId)
            val aula = MockData.getAula(aulaId)
            val alumnosDelCurso = MockData.getAlumnosByCursoId(cursoId)
            
            // Sesión futura: todos los alumnos sin marcar asistencia
            val alumnosConAsistencia = alumnosDelCurso.map { alumno ->
                mapOf(
                    "id" to alumno["id"],
                    "nombre" to alumno["nombre"],
                    "asistio" to null,  // Sin marcar aún
                    "tiene_anotaciones" to false
                )
            }
            
            // Construir información de cabecera (sin sesion_id)
            val sesionInfo = mapOf(
                "sesion_id" to null,  // ✅ No existe sesión aún
                "horario_curso_id" to horarioIdFutura,
                "hora_inicio" to (horario["hora_inicio"] as? String ?: ""),
                "hora_fin" to (horario["hora_fin"] as? String ?: ""),
                "curso" to (curso?.get("nombre") ?: "Curso $cursoId"),
                "curso_id" to cursoId,
                "aula" to (aula?.get("nombre") ?: "Aula $aulaId"),
                "aula_id" to aulaId,
                "profesor" to nombreProfesor,
                "estado" to "programada",
                "fecha" to "",  // Sin fecha específica (es recurrente)
                "alumnos_total" to alumnosDelCurso.size,
                "alumnos_asistieron" to 0,
                "lista_pasada" to false,
                "editable" to true,  // ✅ Sesión programada es editable (se puede iniciar)
                "alumnos_inscritos" to (curso?.get("alumnos_inscritos") as? Int ?: alumnosDelCurso.size)  // ✅ Total inscritos
            )
            
            val dataItem = mapOf(
                "sesion_info" to sesionInfo,
                "alumnos" to alumnosConAsistencia
            )
            
            val suggestions = mutableListOf<Suggestion>()
            suggestions.add(
                Suggestion(
                    id = "sug_volver_clases_hoy",
                    displayText = "Volver a clases de hoy",
                    type = "Generica",
                    recordAction = null,
                    record = null,
                    pagination = null,
                    contextToken = null
                )
            )
            
            val message = """
                📋 Alumnos inscritos en el curso
                
                Curso: ${curso?.get("nombre")}
                Horario: ${horario["hora_inicio"]} - ${horario["hora_fin"]}
                Aula: ${aula?.get("nombre")}
                Profesor: $nombreProfesor
                
                Total de alumnos: ${alumnosDelCurso.size}
            """.trimIndent()
            
            return Envelope(
                status = "success",
                message = message,
                data = DataSection(
                    type = "alumnos_sesion",
                    items = listOf(dataItem),
                    summaryFields = null,
                    pagination = null
                ),
                uiSuggestions = suggestions
            )
        }
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

