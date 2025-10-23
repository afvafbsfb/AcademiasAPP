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
     */
    fun getAlumnosPagina(page: Int, size: Int = 50): Triple<List<Map<String, Any?>>, Int, Boolean> {
        val totalItems = _alumnos.size
        val totalPages = (totalItems + size - 1) / size
        val validPage = page.coerceIn(1, totalPages)
        val startIndex = (validPage - 1) * size
        val endIndex = (startIndex + size).coerceAtMost(totalItems)
        
        val items = _alumnos.subList(startIndex, endIndex)
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
}
