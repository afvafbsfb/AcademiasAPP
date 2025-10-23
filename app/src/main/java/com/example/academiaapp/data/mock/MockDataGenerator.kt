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
     * @param includeNewAlumno Si true, cuenta el nuevo alumno en el total (55 en vez de 54)
     * @return Envelope con lista de alumnos y sugerencias de paginación
     */
    fun generateAlumnosListResponse(page: Int = 1, includeNewAlumno: Boolean = false): Envelope<GenericItem> {
        val totalAlumnos = if (includeNewAlumno) 55 else 54
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
}
