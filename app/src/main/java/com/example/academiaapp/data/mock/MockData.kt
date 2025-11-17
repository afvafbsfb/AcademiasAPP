package com.example.academiaapp.data.mock

/**
 * Datos mock para desarrollo
 * Contiene listas estáticas de alumnos y cursos para simular respuestas del backend
 */
object MockData {
    
    // ===============================================
    // CURSOS - 8 cursos compartidos
    // ===============================================
    
    // ✅ COHERENCIA: 54 alumnos distribuidos en 8 cursos (7+7+7+7+7+7+6+6=54)
    private val _cursos = mutableListOf(
        mapOf(
            "id" to 1,
            "nombre" to "Inglés B1 - Mañanas",
            "anio_academico" to "2024-2025",
            "fecha_inicio" to "2024-09-01",
            "fecha_fin" to "2025-06-30",
            "acepta_nuevos_alumnos" to true,
            "capacidad_maxima" to 20,
            "alumnos_inscritos" to 7,  // ✅ Ajustado: alumnos 1,9,17,25,33,41,49
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
            "alumnos_inscritos" to 7,  // ✅ Ajustado: alumnos 2,10,18,26,34,42,50
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
            "alumnos_inscritos" to 7,  // ✅ Ajustado: alumnos 3,11,19,27,35,43,51
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
            "alumnos_inscritos" to 7,  // ✅ Ajustado: alumnos 4,12,20,28,36,44,52
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
            "alumnos_inscritos" to 7,  // ✅ Ajustado: alumnos 5,13,21,29,37,45,53
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
            "alumnos_inscritos" to 7,  // ✅ Ajustado: alumnos 6,14,22,30,38,46,54
            "tipo_alumno" to "Infantil",
            "estado" to "Activo"
        ),
        mapOf(
            "id" to 7,
            "nombre" to "Español B2 - Intensivo",
            "anio_academico" to "2024-2025",
            "fecha_inicio" to "2024-09-05",
            "fecha_fin" to "2025-05-30",
            "acepta_nuevos_alumnos" to true,  // ✅ Cambiado a true (tiene espacio)
            "capacidad_maxima" to 20,
            "alumnos_inscritos" to 6,  // ✅ Ajustado: alumnos 7,15,23,31,39,47
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
            "alumnos_inscritos" to 6,  // ✅ Ajustado: alumnos 8,16,24,32,40,48
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
            
            // ✅ Para el alumno ID=1, usar letra fija 'A' en DNI (coincide con el mock de baja)
            val letraDNI = if (i == 1) "A" else "${('A'..'Z').random()}"
            
            _alumnos.add(
                mapOf(
                    "id" to i,
                    "nombre" to "$nombre $apellido1 $apellido2",
                    "email" to "${nombre.lowercase()}.${apellido1.lowercase()}$i@example.com",
                    "dni" to "${10000000 + i}$letraDNI",
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
            "curso_profesor_id" to 1,  // Relación Curso-Profesor
            "dia_semana" to "Lunes",
            "hora_inicio" to "09:00",
            "hora_fin" to "11:00"
        ),
        mapOf(
            "id" to 2,
            "curso_id" to 4,  // Matemáticas 1º ESO
            "aula_id" to 3,   // Aula 3
            "curso_profesor_id" to 2,  // Relación Curso-Profesor
            "dia_semana" to "Lunes",
            "hora_inicio" to "16:00",
            "hora_fin" to "18:00"
        ),
        
        // Martes
        mapOf(
            "id" to 3,
            "curso_id" to 4,  // Matemáticas 1º ESO
            "aula_id" to 3,   // Aula 3
            "curso_profesor_id" to 2,  // Relación Curso-Profesor
            "dia_semana" to "Martes",
            "hora_inicio" to "08:00",
            "hora_fin" to "10:00"
        ),
        mapOf(
            "id" to 4,
            "curso_id" to 5,  // Física 2º ESO
            "aula_id" to 5,   // Aula 5
            "curso_profesor_id" to 1,  // Relación Curso-Profesor
            "dia_semana" to "Martes",
            "hora_inicio" to "15:30",
            "hora_fin" to "17:30"
        ),
        
        // Miércoles (HOY - 23 octubre 2025)
        mapOf(
            "id" to 5,
            "curso_id" to 4,  // Matemáticas 1º ESO
            "aula_id" to 3,   // Aula 3
            "curso_profesor_id" to 2,  // Relación Curso-Profesor
            "dia_semana" to "Miércoles",
            "hora_inicio" to "08:00",
            "hora_fin" to "10:00"
        ),
        mapOf(
            "id" to 6,
            "curso_id" to 5,  // Física 2º ESO
            "aula_id" to 5,   // Aula 5
            "curso_profesor_id" to 1,  // Relación Curso-Profesor
            "dia_semana" to "Miércoles",
            "hora_inicio" to "11:00",
            "hora_fin" to "13:00"
        ),
        mapOf(
            "id" to 7,
            "curso_id" to 4,  // Matemáticas 1º ESO
            "aula_id" to 3,   // Aula 3
            "curso_profesor_id" to 2,  // Relación Curso-Profesor
            "dia_semana" to "Miércoles",
            "hora_inicio" to "15:30",
            "hora_fin" to "17:30"
        ),
        mapOf(
            "id" to 11,
            "curso_id" to 4,  // Matemáticas 1º ESO (clase nocturna)
            "aula_id" to 3,   // Aula 3
            "curso_profesor_id" to 2,  // Relación Curso-Profesor
            "dia_semana" to "Miércoles",
            "hora_inicio" to "21:00",
            "hora_fin" to "23:00"
        ),
        
        // Jueves
        mapOf(
            "id" to 8,
            "curso_id" to 5,  // Física 2º ESO
            "aula_id" to 5,   // Aula 5
            "curso_profesor_id" to 1,  // Relación Curso-Profesor
            "dia_semana" to "Jueves",
            "hora_inicio" to "10:00",
            "hora_fin" to "12:00"
        ),
        
        // Viernes
        mapOf(
            "id" to 9,
            "curso_id" to 4,  // Matemáticas 1º ESO
            "aula_id" to 3,   // Aula 3
            "curso_profesor_id" to 2,  // Relación Curso-Profesor
            "dia_semana" to "Viernes",
            "hora_inicio" to "09:00",
            "hora_fin" to "11:00"
        ),
        mapOf(
            "id" to 10,
            "curso_id" to 5,  // Física 2º ESO
            "aula_id" to 5,   // Aula 5
            "curso_profesor_id" to 1,  // Relación Curso-Profesor
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
    
    /**
     * Obtiene un horario específico por su ID
     * @param id ID del horario
     * @return Horario encontrado o null
     */
    fun getHorarioById(id: Int): Map<String, Any?>? {
        return _horariosProfesor.find { (it["id"] as? Int) == id }
    }
    
    // ===============================================
    // SESIONES - Clases ya realizadas
    // ✅ IMPORTANTE: curso_profesor_id = 1 representa al profesor LOGUEADO
    // El nombre del profesor se obtiene de SessionStore en runtime
    // ===============================================
    
    private val _sesiones: MutableList<Map<String, Any?>> = mutableListOf(
        // ========== AYER (22 Oct - Martes) - TODAS COMPLETADAS ==========
        
        // 1. Martes 08:00-10:00 Matemáticas 1º ESO (horario_id=3)
        // ✅ Curso 4 tiene 7 alumnos inscritos (IDs: 4,12,20,28,36,44,52)
        mapOf(
            "id" to 1,
            "horario_curso_id" to 3,
            "aula_id" to 3,
            "curso_profesor_id" to 1,  // ✅ Profesor logueado
            "timestamp_alta" to "2025-10-22 08:02:00",
            "hora_inicio" to "08:00",
            "hora_fin" to "10:00",
            "timestamp_baja" to "2025-10-22 10:05:00",
            "notas_sesion" to "Clase sobre ecuaciones de segundo grado. Buen aprovechamiento general.",
            "notas_materia" to "Completado tema 3.2",
            "lista_pasada" to true,
            "alumnos_asistieron" to 6,  // ✅ AJUSTADO: 6 de 7 alumnos
            "total_alumnos" to 7        // ✅ AJUSTADO: total real del curso
        ),
        
        // 2. Martes 15:30-17:30 Física 2º ESO (horario_id=4)
        // ✅ Curso 5 tiene 7 alumnos inscritos (IDs: 5,13,21,29,37,45,53)
        mapOf(
            "id" to 2,
            "horario_curso_id" to 4,
            "aula_id" to 5,
            "curso_profesor_id" to 1,  // ✅ Profesor logueado
            "timestamp_alta" to "2025-10-22 15:32:00",
            "hora_inicio" to "15:30",
            "hora_fin" to "17:30",
            "timestamp_baja" to "2025-10-22 17:35:00",
            "notas_sesion" to "Prácticas de laboratorio: ley de Ohm.",
            "notas_materia" to "Tema 5 finalizado",
            "lista_pasada" to true,
            "alumnos_asistieron" to 7,  // ✅ AJUSTADO: 7 de 7 alumnos (todos asistieron)
            "total_alumnos" to 7        // ✅ AJUSTADO: total real del curso
        ),
        
        // ========== HOY (23 Oct - Miércoles) ==========
        
        // 3. Miércoles 08:00-10:00 Matemáticas 1º ESO (horario_id=5) - COMPLETADA
        // ✅ Curso 4 tiene 7 alumnos inscritos
        mapOf(
            "id" to 3,
            "horario_curso_id" to 5,
            "aula_id" to 3,
            "curso_profesor_id" to 1,  // ✅ Profesor logueado
            "timestamp_alta" to "2025-10-23 08:01:00",
            "hora_inicio" to "08:00",
            "hora_fin" to "10:00",
            "timestamp_baja" to "2025-10-23 10:08:00",
            "notas_sesion" to "Repaso general antes del examen.",
            "notas_materia" to "Tema 3 revisado",
            "lista_pasada" to true,
            "alumnos_asistieron" to 7,  // ✅ AJUSTADO: 7 de 7 alumnos (todos asistieron)
            "total_alumnos" to 7        // ✅ AJUSTADO: total real del curso
        ),
        
        // 4. Miércoles 11:00-13:00 Física 2º ESO (horario_id=6) - EN CURSO
        // ✅ Curso 5 tiene 7 alumnos inscritos
        mapOf(
            "id" to 4,
            "horario_curso_id" to 6,
            "aula_id" to 5,
            "curso_profesor_id" to 1,  // ✅ Profesor logueado
            "timestamp_alta" to "2025-10-23 11:03:00",
            "hora_inicio" to "11:00",
            "hora_fin" to "13:00",
            "timestamp_baja" to null,  // Sin finalizar
            "notas_sesion" to null,
            "notas_materia" to null,
            "lista_pasada" to true,
            "alumnos_asistieron" to 6,  // ✅ AJUSTADO: 6 de 7 alumnos (1 ausente)
            "total_alumnos" to 7        // ✅ AJUSTADO: total real del curso
        )
        
        // 5. Miércoles 21:00-23:00 Matemáticas (horario_id=7) - SIN INICIAR (no tiene sesión)
        // Esta clase NO tiene entrada en _sesiones, solo existe en HorarioCurso
    )
    
    // ===============================================
    // CACHÉ DE SESIONES DINÁMICAS (para consistencia)
    // ===============================================
    
    /**
     * Fecha de referencia para generar sesiones relativas.
     * Se inicializa la primera vez que se llama a generarSesionesDinamicas()
     * y NO cambia durante toda la sesión de la app.
     */
    private var fechaReferenciaApp: java.time.LocalDateTime? = null
    
    /**
     * Caché de sesiones dinámicas generadas.
     * Se genera una sola vez al inicio y se mantiene constante durante toda la sesión de la app.
     */
    private var sesionesCacheadas: MutableList<Map<String, Any?>> = mutableListOf()
    
    /**
     * Resetea la caché de sesiones dinámicas.
     * Debe llamarse en logout para que el siguiente login regenere la sesión demo.
     */
    fun resetSesionesDinamicas() {
        fechaReferenciaApp = null
        sesionesCacheadas.clear()
    }
    
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
            "curso_profesor_id" to 1,  // ✅ Profesor logueado
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
     * Inicia una sesión programada (marca timestamp_alta)
     * Busca en sesionesCacheadas y actualiza su estado
     * ✅ NUEVO: Limpia las anotaciones previas de la sesión al iniciarla
     * @param sesionId ID de la sesión a iniciar
     * @param timestampAlta Timestamp de inicio
     */
    fun iniciarSesion(sesionId: Int, timestampAlta: String) {
        // Buscar en sesiones cacheadas (dinámicas)
        val index = sesionesCacheadas.indexOfFirst { (it["id"] as? Number)?.toInt() == sesionId }
        if (index != -1) {
            val sesionActual = sesionesCacheadas[index]
            
            // ✅ VALIDACIÓN: No permitir re-inicio de sesión ya iniciada
            if (sesionActual["timestamp_alta"] != null) {
                println("⚠️ WARN: Intento de re-iniciar sesión $sesionId que ya está iniciada")
                return
            }
            
            val sesionActualizada: MutableMap<String, Any?> = sesionActual.toMutableMap()
            sesionActualizada["timestamp_alta"] = timestampAlta
            sesionesCacheadas[index] = sesionActualizada
            
            // ✅ NUEVO: Limpiar anotaciones previas de esta sesión
            val anotacionesOriginales = _anotaciones.size
            _anotaciones.removeAll { (it["sesion_id"] as? Int) == sesionId }
            val anotacionesEliminadas = anotacionesOriginales - _anotaciones.size
            
            println("🔧 DEBUG MockData.iniciarSesion: Sesión $sesionId iniciada en sesionesCacheadas, $anotacionesEliminadas anotaciones eliminadas")
        } else {
            // Si no está en cache, buscar en sesiones estáticas
            val indexEstatica = _sesiones.indexOfFirst { (it["id"] as? Int) == sesionId }
            if (indexEstatica != -1) {
                val sesionActual = _sesiones[indexEstatica]
                
                // ✅ VALIDACIÓN: No permitir re-inicio de sesión ya iniciada
                if (sesionActual["timestamp_alta"] != null) {
                    println("⚠️ WARN: Intento de re-iniciar sesión estática $sesionId que ya está iniciada")
                    return
                }
                
                val sesionActualizada: MutableMap<String, Any?> = sesionActual.toMutableMap()
                sesionActualizada["timestamp_alta"] = timestampAlta
                _sesiones[indexEstatica] = sesionActualizada
                
                // ✅ NUEVO: Limpiar anotaciones previas de esta sesión
                val anotacionesOriginales = _anotaciones.size
                _anotaciones.removeAll { (it["sesion_id"] as? Int) == sesionId }
                val anotacionesEliminadas = anotacionesOriginales - _anotaciones.size
                
                println("🔧 DEBUG MockData.iniciarSesion: Sesión estática $sesionId iniciada, $anotacionesEliminadas anotaciones eliminadas")
            }
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
    
    /**
     * Pasar lista de una sesión: crea anotaciones de ausencia para los alumnos marcados
     * @param sesionId ID de la sesión
     * @param alumnosAusentes Lista de IDs de alumnos ausentes
     * @return Número de anotaciones creadas
     */
    fun pasarListaSesion(sesionId: Int, alumnosAusentes: List<Int>): Int {
        // 1. Obtener la sesión (buscar en estáticas y dinámicas)
        val sesion = getSesionById(sesionId) ?: return 0
        
        // 2. Marcar lista como pasada
        marcarListaPasada(sesionId)
        
        // 3. Eliminar anotaciones de ausencia previas de esta sesión (por si se vuelve a pasar)
        _anotaciones.removeAll { 
            (it["sesion_id"] as? Int) == sesionId && 
            (it["tipo_anotacion"] as? String) == "Ausencia" 
        }
        
        // 4. Crear nuevas anotaciones de ausencia
        val timestamp = java.time.LocalDateTime.now().toString().replace("T", " ").substring(0, 19)
        val nuevasAnotaciones = alumnosAusentes.map { alumnoId ->
            mapOf(
                "id" to (_anotaciones.maxOfOrNull { (it["id"] as? Int) ?: 0 } ?: 0) + 1,
                "sesion_id" to sesionId,
                "alumno_id" to alumnoId,
                "timestamp" to timestamp,
                "tipo_anotacion" to "Ausencia",
                "descripcion" to "Ausencia registrada al pasar lista",
                "profesor_id" to 1
            )
        }
        
        _anotaciones.addAll(nuevasAnotaciones)
        
        // 5. Actualizar contador de asistencia en la sesión (si es dinámica)
        val sesionDinamicaIndex = sesionesCacheadas.indexOfFirst { (it["id"] as? Int) == sesionId }
        if (sesionDinamicaIndex != -1) {
            val sesionActualizada = sesionesCacheadas[sesionDinamicaIndex].toMutableMap()
            val totalAlumnos = sesionActualizada["total_alumnos"] as? Int ?: 0
            sesionActualizada["alumnos_asistieron"] = totalAlumnos - alumnosAusentes.size
            sesionActualizada["lista_pasada"] = true
            sesionesCacheadas[sesionDinamicaIndex] = sesionActualizada
        }
        
        return nuevasAnotaciones.size
    }
    
    // ===============================================
    // GENERACIÓN DINÁMICA BASADA EN FECHA/HORA ACTUAL
    // ===============================================
    
    /**
     * Genera sesiones dinámicamente basándose en fecha/hora actual del sistema.
     * ✅ MODIFICADO v2: 
     * - Crea sesiones para HOY con 3 estados: completada (pasada), programada ±1h, programada futura
     * - Genera anotaciones de ausencia coherentes con alumnos_asistieron
     * - Mantiene datos consistentes con Random(seed) fijo
     * 
     * Estados para HOY:
     * 1. Sesión pasada (completada): timestamp_alta y timestamp_baja seteados
     * 2. Sesión dentro de ±1h (programada): sin timestamps, botón "Iniciar" visible
     * 3. Sesión futura +3h (programada): sin timestamps, sin botón "Iniciar"
     */
    fun generarSesionesDinamicas(): List<Map<String, Any?>> {
        // Si ya están generadas, devolver caché
        if (fechaReferenciaApp != null) {
            return sesionesCacheadas
        }
        
        // Primera ejecución: capturar fecha/hora de referencia
        val ahora = java.time.LocalDateTime.now()
        fechaReferenciaApp = ahora
        
        val hoy = ahora.toLocalDate()
        val horaActual = ahora.toLocalTime()
        
        // Usar Random con seed fijo para datos deterministas
        val random = kotlin.random.Random(12345)
        
        val horarios = _horariosProfesor
        
        var sesionId = 100 // Empezar desde 100 para no colisionar con IDs estáticos
        var anotacionId = _anotaciones.size + 1
        
        // ✅ SESIÓN DEMO: Crear sesión testeable HOY con hora actual redondeada
        // Hora redondeada al cuarto de hora más cercano (ej: 14:37 → 14:45)
        val minutoRedondeado = ((horaActual.minute + 7) / 15) * 15
        val horaRedondeada = if (minutoRedondeado >= 60) {
            horaActual.plusHours(1).withMinute(0)
        } else {
            horaActual.withMinute(minutoRedondeado)
        }
        
        val horaDemoInicio = horaRedondeada.minusMinutes(30)
        val horaDemoFin = horaRedondeada.plusMinutes(90)
        
        // Usar primer curso disponible para la sesión demo
        val horarioDemo = horarios.firstOrNull { it["dia_semana"] == when (hoy.dayOfWeek) {
            java.time.DayOfWeek.MONDAY -> "Lunes"
            java.time.DayOfWeek.TUESDAY -> "Martes"
            java.time.DayOfWeek.WEDNESDAY -> "Miércoles"
            java.time.DayOfWeek.THURSDAY -> "Jueves"
            java.time.DayOfWeek.FRIDAY -> "Viernes"
            java.time.DayOfWeek.SATURDAY -> "Sábado"
            java.time.DayOfWeek.SUNDAY -> "Domingo"
        } }
        
        if (horarioDemo != null) {
            val cursoDemoId = horarioDemo["curso_id"] as Int
            val alumnosDemoCurso = getAlumnosByCursoId(cursoDemoId)
            
            sesionesCacheadas.add(mapOf(
                "id" to sesionId++,
                "horario_curso_id" to (horarioDemo["id"] as Int),
                "aula_id" to (horarioDemo["aula_id"] as Int),
                "curso_profesor_id" to 1,
                "timestamp_alta" to null,  // ← PROGRAMADA (sin iniciar)
                "timestamp_baja" to null,
                "lista_pasada" to false,
                "alumnos_asistieron" to 0,
                "total_alumnos" to alumnosDemoCurso.size,
                "fecha_sesion" to hoy.toString(),  // ✅ Fecha de HOY para búsqueda
                "hora_inicio_demo" to String.format("%02d:%02d", horaDemoInicio.hour, horaDemoInicio.minute),
                "hora_fin_demo" to String.format("%02d:%02d", horaDemoFin.hour, horaDemoFin.minute),
                "es_sesion_demo" to true  // ✅ Flag para identificarla
            ))
        }
        
        // ✅ Procesar: últimos 3 días, hoy, mañana y próximos 5 días
        for (diasOffset in -3..6) {
            val fecha = hoy.plusDays(diasOffset.toLong())
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
                val cursoId = horario["curso_id"] as Int
                
                // ✅ DETERMINAR ESTADO DE LA SESIÓN
                val esPasado = fecha.isBefore(hoy)
                val esHoy = fecha.isEqual(hoy)
                val esFuturo = fecha.isAfter(hoy)
                
                val yaTermino = esHoy && horaFin.isBefore(horaActual)
                val estaEnCurso = esHoy && horaInicio.isBefore(horaActual) && horaFin.isAfter(horaActual)
                val estaEnVentana = esHoy && 
                    horaActual.isAfter(horaInicio.minusHours(1)) && 
                    horaActual.isBefore(horaFin.plusHours(1))
                val esFuturaHoy = esHoy && horaInicio.isAfter(horaActual)
                
                // ✅ DECISIÓN: ¿Crear sesión?
                val debeCrearSesion = when {
                    esPasado -> true           // Ayer: SIEMPRE
                    esHoy -> true              // Hoy: SIEMPRE (3 estados diferentes)
                    esFuturo && diasOffset <= 1 -> true  // Mañana: SIEMPRE
                    else -> false              // Resto de futuro: NO crear aún
                }
                
                if (debeCrearSesion) {
                    // ✅ FIX: Obtener alumnos REALES del curso (no usar alumnos_inscritos)
                    val alumnosRealesDelCurso = getAlumnosByCursoId(cursoId)
                    val totalAlumnos = alumnosRealesDelCurso.size
                    
                    // ✅ ESTADO 1: Sesión COMPLETADA (pasado o ya terminó hoy)
                    val esCompletada = esPasado || yaTermino || estaEnCurso
                    
                    val (timestampAlta, timestampBaja, asistieron) = if (esCompletada) {
                        // Asistencia: entre 70% y 100%
                        val asist = random.nextInt(
                            (totalAlumnos * 0.7).toInt().coerceAtLeast(totalAlumnos - 2), 
                            totalAlumnos + 1
                        )
                        
                        // Timestamp de inicio
                        val variacionMinutos = random.nextInt(-5, 6)
                        val horaInicioReal = horaInicio.plusMinutes(variacionMinutos.toLong())
                        val tsAlta = String.format("%sT%02d:%02d:00", 
                            fecha, horaInicioReal.hour, horaInicioReal.minute)
                        
                        // Timestamp de fin (solo si ya terminó)
                        val tsBaja = if (estaEnCurso) {
                            null  // En curso: sin timestamp_baja
                        } else {
                            val variacionFin = random.nextInt(-3, 8)
                            val horaFinReal = horaFin.plusMinutes(variacionFin.toLong())
                            String.format("%sT%02d:%02d:00", 
                                fecha, horaFinReal.hour, horaFinReal.minute)
                        }
                        
                        Triple(tsAlta, tsBaja, asist)
                    } else {
                        // ✅ ESTADO 2 y 3: Sesión PROGRAMADA (sin iniciar)
                        Triple(null, null, 0)
                    }
                    
                    // Crear sesión
                    val nuevaSesion = mapOf(
                        "id" to sesionId,
                        "horario_curso_id" to horarioId,
                        "aula_id" to (horario["aula_id"] as Int),
                        "curso_profesor_id" to 1,
                        "timestamp_alta" to timestampAlta,
                        "timestamp_baja" to timestampBaja,
                        "lista_pasada" to esCompletada,
                        "alumnos_asistieron" to asistieron,
                        "total_alumnos" to totalAlumnos,
                        "fecha_sesion" to fecha.toString()  // ✅ Añadir fecha para búsqueda
                    )
                    
                    sesionesCacheadas.add(nuevaSesion)
                    
                    // ✅ FIX 2: Crear anotaciones de ausencia coherentes USANDO alumnos REALES
                    if (esCompletada && asistieron < totalAlumnos) {
                        val numAusentes = totalAlumnos - asistieron
                        
                        // Usar seed para seleccionar siempre los mismos ausentes (de la lista REAL)
                        val alumnosAusentes = alumnosRealesDelCurso
                            .shuffled(random)
                            .take(numAusentes)
                        
                        alumnosAusentes.forEach { alumno ->
                            _anotaciones.add(mapOf(
                                "id" to anotacionId++,
                                "sesion_id" to sesionId,
                                "alumno_id" to (alumno["id"] as Int),
                                "timestamp" to (timestampAlta ?: ""),
                                "tipo_anotacion" to "Ausencia",
                                "descripcion" to "Ausencia registrada",
                                "profesor_id" to 1
                            ))
                        }
                    }
                    
                    sesionId++
                }
            }
        }
        
        return sesionesCacheadas
    }
    
    /**
     * Obtiene sesión dinámica para un horario y fecha específicos.
     * MODIFICADO: Ahora busca primero en caché de sesiones dinámicas para mantener consistencia.
     * 
     * - Busca primero en _sesiones (memoria estática)
     * - Luego busca en sesionesCacheadas (generadas dinámicamente pero consistentes)
     * - Si no encuentra, devuelve null (no genera nueva sesión on-the-fly)
     */
    fun getSesionDinamica(horarioId: Int, fecha: String): Map<String, Any?>? {
        // 1. Buscar primero en sesiones estáticas (en memoria)
        val sesionEstatica = _sesiones.firstOrNull {
            it["horario_curso_id"] == horarioId &&
            (it["timestamp_alta"] as? String)?.startsWith(fecha) == true
        }

        if (sesionEstatica != null) return sesionEstatica

        // 2. Asegurar que las sesiones dinámicas estén generadas
        if (sesionesCacheadas.isEmpty()) {
            generarSesionesDinamicas()
        }

        // 3. Buscar en sesiones cacheadas (dinámicas pero consistentes)
        // ✅ FIX: Buscar por timestamp_alta O por fecha_sesion (para sesiones programadas)
        val sesionDinamica = sesionesCacheadas.firstOrNull {
            it["horario_curso_id"] == horarioId &&
            (
                (it["timestamp_alta"] as? String)?.startsWith(fecha) == true ||  // Sesiones completadas/en curso
                (it["fecha_sesion"] as? String) == fecha  // Sesiones programadas (sin timestamp_alta)
            )
        }

        // 4. Devolver sesión encontrada o null (NO generar nuevas on-the-fly)
        return sesionDinamica
    }

    /**
     * OBSOLETO: Ya no se usa - mantenido por compatibilidad.
     * La generación de sesiones ahora se hace en generarSesionesDinamicas() con caché.
     * 
     * Genera una sesión completada dinámica para clases pasadas
     * @param horarioId ID del horario
     * @param fecha Fecha en formato "YYYY-MM-DD"
     * @param horario Mapa del horario (para evitar búsqueda duplicada)
     * @return Mapa de sesión completada con datos simulados
     */
    @Deprecated("Ya no se usa - las sesiones se generan en generarSesionesDinamicas()")
    private fun generarSesionCompletadaDinamica(
        horarioId: Int,
        fecha: String,
        horario: Map<String, Any?>
    ): Map<String, Any?> {
        val horaInicio = horario["hora_inicio"] as String
        val horaFin = horario["hora_fin"] as String
        val cursoId = horario["curso_id"] as Int
        val aulaId = horario["aula_id"] as Int

        // Obtener número real de alumnos del curso
        val curso = getCurso(cursoId)
        val totalAlumnos = (curso?.get("alumnos_inscritos") as? Int) ?: 7

        // Asistencia simulada: entre 70% y 100%
        val asistieron = kotlin.random.Random.nextInt(
            (totalAlumnos * 0.7).toInt().coerceAtLeast(totalAlumnos - 2),
            totalAlumnos + 1
        )

        // Timestamps simulados con variación ±5 minutos
        val variacionInicio = kotlin.random.Random.nextInt(-5, 6)
        val variacionFin = kotlin.random.Random.nextInt(-3, 8)

        val horaInicioTime = java.time.LocalTime.parse(horaInicio)
        val horaFinTime = java.time.LocalTime.parse(horaFin)
        val horaInicioReal = horaInicioTime.plusMinutes(variacionInicio.toLong())
        val horaFinReal = horaFinTime.plusMinutes(variacionFin.toLong())

        val timestampAlta = "$fecha ${String.format("%02d:%02d:00", horaInicioReal.hour, horaInicioReal.minute)}"
        val timestampBaja = "$fecha ${String.format("%02d:%02d:00", horaFinReal.hour, horaFinReal.minute)}"

        return mapOf(
            "id" to -horarioId, // ID negativo para identificar sesiones dinámicas
            "horario_curso_id" to horarioId,
            "aula_id" to aulaId,
            "curso_profesor_id" to 1,
            "timestamp_alta" to timestampAlta,
            "timestamp_baja" to timestampBaja,
            "hora_inicio" to horaInicio,
            "hora_fin" to horaFin,
            "lista_pasada" to true,
            "alumnos_asistieron" to asistieron,
            "total_alumnos" to totalAlumnos,
            "notas_sesion" to null,
            "notas_materia" to null
        )
    }
    
    // ===============================================
    // ANOTACIONES DE SESIONES
    // ===============================================
    
    /**
     * Anotaciones de alumnos en sesiones específicas
     * Tipos: 'Ausencia', 'Evaluacion', 'Comportamiento' (según modelo BD)
     */
    private val _anotaciones: MutableList<Map<String, Any?>> = mutableListOf(
        // Ejemplo: Anotación en sesión 1 (Matemáticas completada)
        mapOf(
            "id" to 1,
            "sesion_id" to 1,
            "alumno_id" to 12,  // Carmen García García
            "timestamp" to "2025-10-22 09:30:00",
            "tipo_anotacion" to "Evaluacion",  // ← CAMBIADO: "academico" → "Evaluacion"
            "descripcion" to "Excelente participación en clase. Resolvió correctamente todos los ejercicios.",
            "profesor_id" to 1
        ),
        mapOf(
            "id" to 2,
            "sesion_id" to 2,
            "alumno_id" to 5,  // Luis García García
            "timestamp" to "2025-10-22 16:45:00",
            "tipo_anotacion" to "Comportamiento",  // ← CAMBIADO: "comportamiento" → "Comportamiento"
            "descripcion" to "Llegó 10 minutos tarde. Justificó con cita médica.",
            "profesor_id" to 1
        ),
        mapOf(
            "id" to 3,
            "sesion_id" to 3,
            "alumno_id" to 28,  // Andrés García García
            "timestamp" to "2025-10-23 09:15:00",
            "tipo_anotacion" to "Evaluacion",  // ← CAMBIADO: "academico" → "Evaluacion"
            "descripcion" to "Mostró dificultades con las ecuaciones cuadráticas. Programar refuerzo.",
            "profesor_id" to 1
        ),
        
        // ========== AUSENCIAS ==========
        // Sesión 1: 1 ausente (6/7 asistieron)
        mapOf(
            "id" to 4,
            "sesion_id" to 1,
            "alumno_id" to 4,  // Ana López Pérez (curso_id=4)
            "timestamp" to "2025-10-22 08:15:00",
            "tipo_anotacion" to "Ausencia",
            "descripcion" to "Ausencia sin justificar",
            "profesor_id" to 1
        ),
        
        // Sesión 4: 1 ausente (6/7 asistieron)
        mapOf(
            "id" to 5,
            "sesion_id" to 4,
            "alumno_id" to 53,  // Último alumno curso_id=5
            "timestamp" to "2025-10-23 11:10:00",
            "tipo_anotacion" to "Ausencia",
            "descripcion" to "Ausencia justificada por enfermedad",
            "profesor_id" to 1
        )
    )
    
    fun getAnotaciones(): List<Map<String, Any?>> = _anotaciones.toList()
    
    fun getAnotacionesBySesion(sesionId: Int): List<Map<String, Any?>> {
        return _anotaciones.filter { (it["sesion_id"] as? Int) == sesionId }
    }
    
    fun getAnotacionesBySesionAndAlumno(sesionId: Int, alumnoId: Int): List<Map<String, Any?>> {
        return _anotaciones.filter {
            (it["sesion_id"] as? Int) == sesionId && 
            (it["alumno_id"] as? Int) == alumnoId
        }
    }
    
    /**
     * Crea una nueva anotación para un alumno en una sesión
     * @param sesionId ID de la sesión
     * @param alumnoId ID del alumno
     * @param tipoAnotacion Tipo de anotación (Evaluacion, Comportamiento, etc.)
     * @param descripcion Texto de la anotación
     * @param profesorId ID del profesor (por defecto 1)
     * @return ID de la anotación creada
     */
    fun crearAnotacion(
        sesionId: Int,
        alumnoId: Int,
        tipoAnotacion: String,
        descripcion: String,
        profesorId: Int = 1
    ): Int {
        val newId = (_anotaciones.maxOfOrNull { (it["id"] as? Int) ?: 0 } ?: 0) + 1
        val timestamp = java.time.LocalDateTime.now().toString().replace("T", " ").substring(0, 19)
        
        val nuevaAnotacion = mapOf(
            "id" to newId,
            "sesion_id" to sesionId,
            "alumno_id" to alumnoId,
            "timestamp" to timestamp,
            "tipo_anotacion" to tipoAnotacion,
            "descripcion" to descripcion,
            "profesor_id" to profesorId
        )
        
        _anotaciones.add(nuevaAnotacion)
        
        println("🔧 DEBUG crearAnotacion - id=$newId, sesion=$sesionId, alumno=$alumnoId, tipo=$tipoAnotacion")
        
        return newId
    }
    
    /**
     * Calcula asistencia de una sesión.
     * ✅ MODIFICADO: Usa los datos de alumnos_asistieron y total_alumnos de la sesión directamente
     *                para mantener coherencia con lo que muestra la card.
     * 
     * @param sesionId ID de la sesión
     * @return Triple(presentes, ausentes, total) o null si no se encuentra la sesión
     */
    fun calcularAsistenciaSesion(sesionId: Int): Triple<Int, Int, Int>? {
        // Obtener la sesión
        val sesion = getSesionById(sesionId) ?: return null
        
        // ✅ Usar datos directamente de la sesión (coherente con la card)
        val asistieron = (sesion["alumnos_asistieron"] as? Int) ?: 0
        val total = (sesion["total_alumnos"] as? Int) ?: 0
        val ausentes = total - asistieron
        
        return Triple(asistieron, ausentes, total)
    }
    
    /**
     * Obtiene una sesión por su ID (busca en estáticas Y dinámicas)
     */
    fun getSesionById(id: Int): Map<String, Any?>? {
        // Primero buscar en sesiones estáticas (1-4)
        val sesionEstatica = _sesiones.find { (it["id"] as? Int) == id }
        if (sesionEstatica != null) return sesionEstatica
        
        // Si no se encuentra, buscar en sesiones dinámicas (5+)
        val sesionesDinamicas = generarSesionesDinamicas()
        return sesionesDinamicas.find { (it["id"] as? Int) == id }
    }
    
    /**
     * Obtiene todos los alumnos matriculados en un curso específico
     */
    fun getAlumnosByCursoId(cursoId: Int): List<Map<String, Any?>> {
        return _alumnos.filter { (it["curso_id"] as? Int) == cursoId }
    }
    
    /**
     * Obtiene un alumno por su ID
     */
    fun getAlumnoById(id: Int): Map<String, Any?>? {
        return _alumnos.find { (it["id"] as? Int) == id }
    }
}
