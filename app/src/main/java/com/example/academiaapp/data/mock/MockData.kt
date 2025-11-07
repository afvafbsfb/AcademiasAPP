package com.example.academiaapp.data.mock

/**
 * Datos mock para desarrollo
 * Contiene listas estáticas de alumnos y cursos para simular respuestas del backend
 */
object MockData {
    
    // ===============================================
    // CURSOS - 8 cursos compartidos
    // ===============================================
    
    private val _cursos = mutableListOf(
        mapOf(
            "id" to 1,
            "nombre" to "Inglés B1 - Mañanas",
            "anio_academico" to "2024-2025",
            "fecha_inicio" to "2024-09-01",
            "fecha_fin" to "2025-06-30",
            "acepta_nuevos_alumnos" to true,
            "capacidad_maxima" to 20,
            "alumnos_inscritos" to 18,
            "tipo_alumno" to "Adultos",
            "estado" to "Activo"
        ),
        mapOf(
            "id" to 2,
            "nombre" to "Español A2 - Tardes",
            "anio_academico" to "2024-2025",
            "fecha_inicio" to "2024-09-01",
            "fecha_fin" to "2025-06-30",
            "acepta_nuevos_alumnos" to true,
            "capacidad_maxima" to 15,
            "alumnos_inscritos" to 12,
            "tipo_alumno" to "Juvenil",
            "estado" to "Activo"
        ),
        mapOf(
            "id" to 3,
            "nombre" to "Francés Iniciación",
            "anio_academico" to "2024-2025",
            "fecha_inicio" to "2024-10-01",
            "fecha_fin" to "2025-05-31",
            "acepta_nuevos_alumnos" to true,
            "capacidad_maxima" to 12,
            "alumnos_inscritos" to 8,
            "tipo_alumno" to "Adultos",
            "estado" to "Activo"
        ),
        mapOf(
            "id" to 4,
            "nombre" to "Matemáticas 1º ESO",
            "anio_academico" to "2024-2025",
            "fecha_inicio" to "2024-09-10",
            "fecha_fin" to "2025-06-20",
            "acepta_nuevos_alumnos" to true,
            "capacidad_maxima" to 20,
            "alumnos_inscritos" to 18,
            "tipo_alumno" to "Juvenil",
            "estado" to "Activo"
        ),
        mapOf(
            "id" to 5,
            "nombre" to "Física 2º ESO",
            "anio_academico" to "2024-2025",
            "fecha_inicio" to "2024-09-10",
            "fecha_fin" to "2025-06-20",
            "acepta_nuevos_alumnos" to true,
            "capacidad_maxima" to 18,
            "alumnos_inscritos" to 15,
            "tipo_alumno" to "Juvenil",
            "estado" to "Activo"
        ),
        mapOf(
            "id" to 6,
            "nombre" to "Inglés A1 - Infantil",
            "anio_academico" to "2024-2025",
            "fecha_inicio" to "2024-09-15",
            "fecha_fin" to "2025-06-15",
            "acepta_nuevos_alumnos" to true,
            "capacidad_maxima" to 12,
            "alumnos_inscritos" to 10,
            "tipo_alumno" to "Infantil",
            "estado" to "Activo"
        ),
        mapOf(
            "id" to 7,
            "nombre" to "Español B2 - Intensivo",
            "anio_academico" to "2024-2025",
            "fecha_inicio" to "2024-09-05",
            "fecha_fin" to "2025-05-30",
            "acepta_nuevos_alumnos" to false,
            "capacidad_maxima" to 20,
            "alumnos_inscritos" to 20,
            "tipo_alumno" to "Adultos",
            "estado" to "Activo"
        ),
        mapOf(
            "id" to 8,
            "nombre" to "Alemán Básico",
            "anio_academico" to "2024-2025",
            "fecha_inicio" to "2024-10-01",
            "fecha_fin" to "2025-06-30",
            "acepta_nuevos_alumnos" to true,
            "capacidad_maxima" to 15,
            "alumnos_inscritos" to 5,
            "tipo_alumno" to "Adultos",
            "estado" to "Activo"
        )
    )
    
    fun getCursos(): List<Map<String, Any?>> = _cursos.toList()
    
    fun getCurso(id: Int): Map<String, Any?>? {
        return _cursos.find { (it["id"] as? Int) == id }
    }
    
    // ===============================================
    // ALUMNOS - 54 alumnos (generados automáticamente)
    // ===============================================
    
    private val _alumnos = mutableListOf<Map<String, Any?>>()
    
    init {
        // Generar 54 alumnos automáticamente
        val nombresEjemplo = listOf(
            "Juan", "María", "Pedro", "Ana", "Luis", "Carmen", "Roberto", "Laura",
            "Diego", "Patricia", "Javier", "Isabel", "Francisco", "Elena", "Miguel",
            "Lucía", "Antonio", "Teresa", "Andrés", "Beatriz", "Alberto", "Cristina",
            "Fernando", "Raquel", "Sergio", "Marta", "Pablo", "Silvia", "Daniel",
            "Adriana", "Jorge", "Natalia", "Carlos", "Victoria", "Manuel", "Sofía",
            "Rubén", "Paula", "David", "Claudia", "Alejandro", "Eva", "Víctor",
            "Irene", "Ángel", "Sara", "Rafael", "Andrea", "Óscar", "Lorena",
            "Iván", "Verónica", "Guillermo", "Marina"
        )
        
        val apellidosEjemplo = listOf(
            "García", "Rodríguez", "Fernández", "López", "Martínez", "Sánchez",
            "Pérez", "Gómez", "Martín", "Jiménez", "Ruiz", "Hernández", "Díaz",
            "Moreno", "Álvarez", "Muñoz", "Romero", "Alonso", "Gutiérrez", "Navarro"
        )
        
        val cursosInfo = _cursos.map { Pair(it["id"] as Int, it["nombre"] as String) }
        
        for (i in 1..54) {
            val nombre = nombresEjemplo[(i - 1) % nombresEjemplo.size]
            val apellido1 = apellidosEjemplo[(i - 1) % apellidosEjemplo.size]
            val apellido2 = apellidosEjemplo[((i - 1) * 3) % apellidosEjemplo.size]
            val (cursoId, cursoNombre) = cursosInfo[(i - 1) % cursosInfo.size]
            val añoNacimiento = 1985 + (i % 30)
            val edad = 2024 - añoNacimiento
            val esJoven = edad < 18
            val tienePendiente = i % 7 == 0
            val estadoPago = if (tienePendiente) "Pendiente" else "Al día"
            
            // Deudas realistas en cifras redondas (30, 60, 100, 120 euros)
            val deudasPosibles = listOf(30, 60, 100, 120, 90, 150, 180)
            val deudaEuros = if (tienePendiente) deudasPosibles[i % deudasPosibles.size] else 0
            
            _alumnos.add(
                mapOf(
                    "id" to i,
                    "nombre" to "$nombre $apellido1 $apellido2",
                    "email" to "${nombre.lowercase()}.${apellido1.lowercase()}$i@example.com",
                    "dni" to "${10000000 + i}${('A'..'Z').random()}",
                    "telefono" to "6${String.format("%08d", 100000000 + i)}",
                    "fecha_nacimiento" to "$añoNacimiento-${String.format("%02d", (i % 12) + 1)}-${String.format("%02d", (i % 28) + 1)}",
                    "direccion" to "Calle Ejemplo $i, Madrid",
                    "nombre_tutor" to if (esJoven) "Tutor de $nombre" else null,
                    "relaccion_tutor_alumno" to if (esJoven) if (i % 2 == 0) "Padre" else "Madre" else null,
                    "telefono_tutor" to if (esJoven) "6${String.format("%08d", 200000000 + i)}" else null,
                    "email_tutor" to if (esJoven) "tutor.$i@example.com" else null,
                    "curso" to cursoNombre,
                    "curso_id" to cursoId,
                    "estado_pago" to estadoPago,
                    "deuda_euros" to deudaEuros
                )
            )
        }
    }
    
    fun getAlumnos(): List<Map<String, Any?>> = _alumnos.toList()
    
    fun getAlumno(id: Int): Map<String, Any?>? {
        return _alumnos.find { (it["id"] as? Int) == id }
    }
    
    /**
     * Obtiene una página de alumnos
     * @param page Número de página (empezando en 1)
     * @param size Tamaño de página (por defecto 50)
     * @return Triple con (lista de alumnos, cantidad devuelta, hay más páginas)
     * ✅ FIXED: Devuelve copia inmutable para evitar ConcurrentModificationException
     */
    fun getAlumnosPagina(page: Int, size: Int = 50): Triple<List<Map<String, Any?>>, Int, Boolean> {
        val totalItems = _alumnos.size
        val totalPages = (totalItems + size - 1) / size
        val validPage = page.coerceIn(1, totalPages)
        val startIndex = (validPage - 1) * size
        val endIndex = (startIndex + size).coerceAtMost(totalItems)
        
        // ✅ Usar toList() para crear una copia inmutable en lugar de subList()
        val items = _alumnos.subList(startIndex, endIndex).toList()
        val hasMore = validPage < totalPages
        
        return Triple(items, items.size, hasMore)
    }
    
    /**
     * Añade un nuevo alumno a la lista mock (al principio para que aparezca primero)
     * @param alumno Datos del alumno (sin ID)
     * @return ID del nuevo alumno creado
     */
    fun addAlumno(alumno: Map<String, Any?>): Int {
        val newId = (_alumnos.maxOfOrNull { (it["id"] as? Int) ?: 0 } ?: 0) + 1
        val alumnoConId = alumno.toMutableMap().apply {
            put("id", newId)
            // Si no tiene deuda asignada, ponerlo sin deuda (Al día)
            if (!containsKey("deuda_euros")) {
                put("deuda_euros", 0)
            }
            // ✅ CORREGIDO: Si tiene curso_id, buscar el curso y añadir el campo "curso" con el nombre
            val cursoId = (get("curso_id") as? Number)?.toInt()
            if (cursoId != null && !containsKey("curso")) {
                val curso = getCurso(cursoId)
                if (curso != null) {
                    put("curso", curso["nombre"])
                }
            }
        }
        // Insertar al PRINCIPIO para que aparezca primero en las búsquedas
        _alumnos.add(0, alumnoConId)
        return newId
    }
    
    /**
     * Filtra alumnos con pagos pendientes (deuda > 0)
     * @return Lista de alumnos con deuda pendiente
     */
    fun getAlumnosConPagosPendientes(): List<Map<String, Any?>> {
        return _alumnos.filter { 
            val deuda = (it["deuda_euros"] as? Number)?.toInt() ?: 0
            deuda > 0
        }
    }
    
    /**
     * Actualiza los datos de un alumno existente
     * @param alumnoId ID del alumno a actualizar
     * @param nuevosDatos Nuevos datos del alumno
     * @return true si se actualizó correctamente, false si no se encontró
     */
    fun updateAlumno(alumnoId: Int, nuevosDatos: Map<String, Any?>): Boolean {
        val index = _alumnos.indexOfFirst { (it["id"] as? Int) == alumnoId }
        if (index == -1) return false

        val alumnoActual = _alumnos[index].toMutableMap()

        // Actualizar campos (mantener el ID)
        alumnoActual.putAll(nuevosDatos)
        alumnoActual["id"] = alumnoId

        // Si cambió el curso_id, actualizar el campo "curso" con el nombre
        val cursoId = (nuevosDatos["curso_id"] as? Number)?.toInt()
        if (cursoId != null) {
            val curso = getCurso(cursoId)
            if (curso != null) {
                alumnoActual["curso"] = curso["nombre"]
            }
        }

        _alumnos[index] = alumnoActual
        return true
    }

    /**
     * Elimina un alumno (simula baja)
     * @param alumnoId ID del alumno a eliminar
     * @return true si se eliminó correctamente, false si no se encontró
     */
    fun deleteAlumno(alumnoId: Int): Boolean {
        val index = _alumnos.indexOfFirst { (it["id"] as? Int) == alumnoId }
        if (index == -1) return false

        _alumnos.removeAt(index)
        return true
    }

    // ===============================================
    // AULAS - 5 aulas de la academia
    // ===============================================
    
    private val _aulas = listOf(
        mapOf("id" to 1, "nombre" to "Aula 1", "capacidad_maxima" to 15),
        mapOf("id" to 2, "nombre" to "Aula 2", "capacidad_maxima" to 12),
        mapOf("id" to 3, "nombre" to "Aula 3", "capacidad_maxima" to 25),
        mapOf("id" to 4, "nombre" to "Aula 4", "capacidad_maxima" to 18),
        mapOf("id" to 5, "nombre" to "Aula 5", "capacidad_maxima" to 20)
    )
    
    fun getAula(id: Int): Map<String, Any?>? {
        return _aulas.find { (it["id"] as? Int) == id }
    }
    
    // ===============================================
    // HORARIOS - Horario semanal de María García
    // Matemáticas 1º ESO (curso_id=4) + Física 2º ESO (curso_id=5)
    // ===============================================
    
    private val _horariosProfesor = listOf(
        // Lunes
        mapOf(
            "id" to 1,
            "curso_id" to 5,  // Física 2º ESO
            "aula_id" to 5,   // Aula 5
            "dia_semana" to "Lunes",
            "hora_inicio" to "09:00",
            "hora_fin" to "11:00"
        ),
        mapOf(
            "id" to 2,
            "curso_id" to 4,  // Matemáticas 1º ESO
            "aula_id" to 3,   // Aula 3
            "dia_semana" to "Lunes",
            "hora_inicio" to "16:00",
            "hora_fin" to "18:00"
        ),
        
        // Martes
        mapOf(
            "id" to 3,
            "curso_id" to 4,  // Matemáticas 1º ESO
            "aula_id" to 3,   // Aula 3
            "dia_semana" to "Martes",
            "hora_inicio" to "08:00",
            "hora_fin" to "10:00"
        ),
        mapOf(
            "id" to 4,
            "curso_id" to 5,  // Física 2º ESO
            "aula_id" to 5,   // Aula 5
            "dia_semana" to "Martes",
            "hora_inicio" to "15:30",
            "hora_fin" to "17:30"
        ),
        
        // Miércoles (HOY - 23 octubre 2025)
        mapOf(
            "id" to 5,
            "curso_id" to 4,  // Matemáticas 1º ESO
            "aula_id" to 3,   // Aula 3
            "dia_semana" to "Miércoles",
            "hora_inicio" to "08:00",
            "hora_fin" to "10:00"
        ),
        mapOf(
            "id" to 6,
            "curso_id" to 5,  // Física 2º ESO
            "aula_id" to 5,   // Aula 5
            "dia_semana" to "Miércoles",
            "hora_inicio" to "11:00",
            "hora_fin" to "13:00"
        ),
        mapOf(
            "id" to 7,
            "curso_id" to 4,  // Matemáticas 1º ESO
            "aula_id" to 3,   // Aula 3
            "dia_semana" to "Miércoles",
            "hora_inicio" to "15:30",
            "hora_fin" to "17:30"
        ),
        mapOf(
            "id" to 11,
            "curso_id" to 4,  // Matemáticas 1º ESO (clase nocturna)
            "aula_id" to 3,   // Aula 3
            "dia_semana" to "Miércoles",
            "hora_inicio" to "21:00",
            "hora_fin" to "23:00"
        ),
        
        // Jueves
        mapOf(
            "id" to 8,
            "curso_id" to 5,  // Física 2º ESO
            "aula_id" to 5,   // Aula 5
            "dia_semana" to "Jueves",
            "hora_inicio" to "10:00",
            "hora_fin" to "12:00"
        ),
        
        // Viernes
        mapOf(
            "id" to 9,
            "curso_id" to 4,  // Matemáticas 1º ESO
            "aula_id" to 3,   // Aula 3
            "dia_semana" to "Viernes",
            "hora_inicio" to "09:00",
            "hora_fin" to "11:00"
        ),
        mapOf(
            "id" to 10,
            "curso_id" to 5,  // Física 2º ESO
            "aula_id" to 5,   // Aula 5
            "dia_semana" to "Viernes",
            "hora_inicio" to "14:00",
            "hora_fin" to "16:00"
        )
    )
    
    /**
     * Obtiene horarios del profesor para un día específico
     * @param diaSemana Día de la semana ("Lunes", "Martes", etc.) o null para todos
     * @return Lista de horarios ordenados por hora_inicio
     */
    fun getHorariosProfesor(diaSemana: String? = null): List<Map<String, Any?>> {
        val horarios = if (diaSemana != null) {
            _horariosProfesor.filter { (it["dia_semana"] as String) == diaSemana }
        } else {
            _horariosProfesor
        }
        
        return horarios.sortedBy { it["hora_inicio"] as String }
    }
    
    // ===============================================
    // SESIONES - Clases ya realizadas
    // curso_profesor_id = 1 (María García con sus cursos)
    // ===============================================
    
    private val _sesiones: MutableList<Map<String, Any?>> = mutableListOf(
        // ========== AYER (22 Oct - Martes) - TODAS COMPLETADAS ==========
        
        // 1. Martes 08:00-10:00 Matemáticas (horario_id=3)
        mapOf(
            "id" to 1,
            "horario_curso_id" to 3,
            "aula_id" to 3,
            "curso_profesor_id" to 1,
            "timestamp_alta" to "2025-10-22 08:02:00",
            "hora_inicio" to "08:00",
            "hora_fin" to "10:00",
            "timestamp_baja" to "2025-10-22 10:05:00",
            "notas_sesion" to "Clase sobre ecuaciones de segundo grado. Buen aprovechamiento general.",
            "notas_materia" to "Completado tema 3.2",
            "lista_pasada" to true,
            "alumnos_asistieron" to 23,
            "total_alumnos" to 25
        ),
        
        // 2. Martes 15:30-17:30 Física (horario_id=4)
        mapOf(
            "id" to 2,
            "horario_curso_id" to 4,
            "aula_id" to 5,
            "curso_profesor_id" to 1,
            "timestamp_alta" to "2025-10-22 15:32:00",
            "hora_inicio" to "15:30",
            "hora_fin" to "17:30",
            "timestamp_baja" to "2025-10-22 17:35:00",
            "notas_sesion" to "Prácticas de laboratorio: ley de Ohm.",
            "notas_materia" to "Tema 5 finalizado",
            "lista_pasada" to true,
            "alumnos_asistieron" to 18,
            "total_alumnos" to 20
        ),
        
        // ========== HOY (23 Oct - Miércoles) ==========
        
        // 3. Miércoles 08:00-10:00 Matemáticas (horario_id=5) - COMPLETADA
        mapOf(
            "id" to 3,
            "horario_curso_id" to 5,
            "aula_id" to 3,
            "curso_profesor_id" to 1,
            "timestamp_alta" to "2025-10-23 08:01:00",
            "hora_inicio" to "08:00",
            "hora_fin" to "10:00",
            "timestamp_baja" to "2025-10-23 10:08:00",
            "notas_sesion" to "Repaso general antes del examen.",
            "notas_materia" to "Tema 3 revisado",
            "lista_pasada" to true,
            "alumnos_asistieron" to 24,
            "total_alumnos" to 25
        ),
        
        // 4. Miércoles 11:00-13:00 Física (horario_id=6) - EN CURSO
        mapOf(
            "id" to 4,
            "horario_curso_id" to 6,
            "aula_id" to 5,
            "curso_profesor_id" to 1,
            "timestamp_alta" to "2025-10-23 11:03:00",
            "hora_inicio" to "11:00",
            "hora_fin" to "13:00",
            "timestamp_baja" to null,  // Sin finalizar
            "notas_sesion" to null,
            "notas_materia" to null,
            "lista_pasada" to true,
            "alumnos_asistieron" to 19,
            "total_alumnos" to 20
        )
        
        // 5. Miércoles 21:00-23:00 Matemáticas (horario_id=7) - SIN INICIAR (no tiene sesión)
        // Esta clase NO tiene entrada en _sesiones, solo existe en HorarioCurso
    )
    
    /**
     * Obtiene una sesión por horario_curso_id y fecha
     * @param horarioCursoId ID del horario
     * @param fecha Fecha en formato "YYYY-MM-DD"
     * @return Sesión encontrada o null
     */
    fun getSesion(horarioCursoId: Int, fecha: String): Map<String, Any?>? {
        return _sesiones.find { sesion ->
            val sesionHorarioId = sesion["horario_curso_id"] as? Int
            val timestampAlta = sesion["timestamp_alta"] as? String
            val sesionFecha = timestampAlta?.substring(0, 10) // Extraer YYYY-MM-DD
            
            sesionHorarioId == horarioCursoId && sesionFecha == fecha
        }
    }
    
    /**
     * Crea una nueva sesión para un horario específico
     * @param horarioCursoId ID del horario
     * @param timestampAlta Timestamp de inicio (formato "YYYY-MM-DD HH:MM:SS")
     * @param horaInicio Hora de inicio (formato "HH:MM")
     * @param horaFin Hora de fin (formato "HH:MM")
     * @return ID de la nueva sesión creada
     */
    fun crearSesion(
        horarioCursoId: Int,
        timestampAlta: String,
        horaInicio: String,
        horaFin: String
    ): Int {
        val horario = _horariosProfesor.find { (it["id"] as Int) == horarioCursoId }
            ?: throw IllegalArgumentException("Horario $horarioCursoId no encontrado")
        
        val newId = (_sesiones.maxOfOrNull { (it["id"] as? Int) ?: 0 } ?: 0) + 1
        val aulaId = horario["aula_id"] as Int
        
        val nuevaSesion = mapOf(
            "id" to newId,
            "horario_curso_id" to horarioCursoId,
            "aula_id" to aulaId,
            "curso_profesor_id" to 1,  // María García
            "timestamp_alta" to timestampAlta,
            "hora_inicio" to horaInicio,
            "hora_fin" to horaFin,
            "timestamp_baja" to null,
            "notas_sesion" to null,
            "notas_materia" to null,
            "lista_pasada" to false
        )
        
        _sesiones.add(nuevaSesion)
        return newId
    }
    
    /**
     * Finaliza una sesión (marca timestamp_baja)
     * @param sesionId ID de la sesión
     * @param timestampBaja Timestamp de finalización
     * @param notasSesion Notas opcionales de la sesión
     */
    fun finalizarSesion(sesionId: Int, timestampBaja: String, notasSesion: String? = null) {
        val index = _sesiones.indexOfFirst { (it["id"] as? Int) == sesionId }
        if (index != -1) {
            val sesionActualizada: MutableMap<String, Any?> = _sesiones[index].toMutableMap()
            sesionActualizada["timestamp_baja"] = timestampBaja
            if (notasSesion != null) {
                sesionActualizada["notas_sesion"] = notasSesion
            }
            _sesiones[index] = sesionActualizada
        }
    }
    
    /**
     * Marca la lista como pasada en una sesión
     * @param sesionId ID de la sesión
     */
    fun marcarListaPasada(sesionId: Int) {
        val index = _sesiones.indexOfFirst { (it["id"] as? Int) == sesionId }
        if (index != -1) {
            val sesionActualizada: MutableMap<String, Any?> = _sesiones[index].toMutableMap()
            sesionActualizada["lista_pasada"] = true
            _sesiones[index] = sesionActualizada
        }
    }
    
    // ===============================================
    // GENERACIÓN DINÁMICA BASADA EN FECHA/HORA ACTUAL
    // ===============================================
    
    /**
     * Genera sesiones dinámicamente basándose en fecha/hora actual del sistema
     * - Pasado (ayer y clases anteriores a hora actual): Completadas con lista pasada
     * - Presente (clase en curso): En curso con lista pasada
     * - Futuro: No existe sesión (aparecerá como programada)
     */
    fun generarSesionesDinamicas(): List<Map<String, Any?>> {
        val sesiones = mutableListOf<Map<String, Any?>>()
        val ahora = java.time.LocalDateTime.now()
        val hoy = ahora.toLocalDate()
        val horaActual = ahora.toLocalTime()
        
        val horarios = _horariosProfesor
        val diasSemanaMap = mapOf(
            "Lunes" to java.time.DayOfWeek.MONDAY,
            "Martes" to java.time.DayOfWeek.TUESDAY,
            "Miércoles" to java.time.DayOfWeek.WEDNESDAY,
            "Jueves" to java.time.DayOfWeek.THURSDAY,
            "Viernes" to java.time.DayOfWeek.FRIDAY
        )
        
        var sesionId = 1
        
        // Procesar últimos 7 días para generar historial
        for (diasAtras in 7 downTo 0) {
            val fecha = hoy.minusDays(diasAtras.toLong())
            val diaSemana = when (fecha.dayOfWeek) {
                java.time.DayOfWeek.MONDAY -> "Lunes"
                java.time.DayOfWeek.TUESDAY -> "Martes"
                java.time.DayOfWeek.WEDNESDAY -> "Miércoles"
                java.time.DayOfWeek.THURSDAY -> "Jueves"
                java.time.DayOfWeek.FRIDAY -> "Viernes"
                java.time.DayOfWeek.SATURDAY -> "Sábado"
                java.time.DayOfWeek.SUNDAY -> "Domingo"
            }
            
            // Obtener horarios de ese día
            val horariosDelDia = horarios.filter { it["dia_semana"] == diaSemana }
            
            horariosDelDia.forEach { horario ->
                val horaInicio = java.time.LocalTime.parse(horario["hora_inicio"] as String)
                val horaFin = java.time.LocalTime.parse(horario["hora_fin"] as String)
                val horarioId = horario["id"] as Int
                
                // Determinar si debe existir sesión y su estado
                val debeCrearSesion = when {
                    // Días pasados: siempre crear sesión completada
                    fecha.isBefore(hoy) -> true
                    // Hoy: solo si la hora de inicio ya pasó o está muy próxima (±15 min)
                    fecha.isEqual(hoy) -> horaInicio.isBefore(horaActual.plusMinutes(15))
                    // Días futuros: no crear sesión
                    else -> false
                }
                
                if (debeCrearSesion) {
                    val estaEnCurso = fecha.isEqual(hoy) && 
                                      horaInicio.isBefore(horaActual.plusMinutes(15)) && 
                                      horaFin.isAfter(horaActual)
                    
                    val totalAlumnos = kotlin.random.Random.nextInt(18, 26)
                    val asistieron = kotlin.random.Random.nextInt((totalAlumnos * 0.85).toInt(), totalAlumnos + 1)
                    
                    // Timestamp de inicio: fecha + hora_inicio con variación ±5 minutos
                    val variacionMinutos = kotlin.random.Random.nextInt(-5, 6)
                    val horaInicioReal = horaInicio.plusMinutes(variacionMinutos.toLong())
                    val timestampAlta = String.format("%sT%02d:%02d:00", 
                        fecha, horaInicioReal.hour, horaInicioReal.minute)
                    
                    // Timestamp de fin: null si en curso, sino fecha + hora_fin con variación
                    val timestampBaja = if (estaEnCurso) {
                        null
                    } else {
                        val variacionFin = kotlin.random.Random.nextInt(-3, 8)
                        val horaFinReal = horaFin.plusMinutes(variacionFin.toLong())
                        String.format("%sT%02d:%02d:00", 
                            fecha, horaFinReal.hour, horaFinReal.minute)
                    }
                    
                    sesiones.add(mapOf(
                        "id" to sesionId++,
                        "horario_curso_id" to horarioId,
                        "aula_id" to (horario["aula_id"] as Int),
                        "curso_profesor_id" to 1,
                        "timestamp_alta" to timestampAlta,
                        "timestamp_baja" to timestampBaja,
                        "lista_pasada" to true,  // Siempre pasada en mocks
                        "alumnos_asistieron" to asistieron,
                        "total_alumnos" to totalAlumnos
                    ))
                }
            }
        }
        
        return sesiones
    }
    
    /**
     * Obtiene sesión dinámica para un horario y fecha específicos
     * Usa la generación dinámica en lugar de los datos estáticos
     */
    fun getSesionDinamica(horarioId: Int, fecha: String): Map<String, Any?>? {
        val sesionesDinamicas = generarSesionesDinamicas()
        return sesionesDinamicas.firstOrNull { 
            it["horario_curso_id"] == horarioId && 
            (it["timestamp_alta"] as String).startsWith(fecha)
        }
    }
}
