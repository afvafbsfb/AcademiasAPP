# Ejemplos de Flujos Completos - Chat Guiado

**Fecha:** 23 de octubre de 2025  
**Propósito:** Documentar ejemplos concretos de conversaciones mock para cada funcionalidad

---

## 👨‍🏫 PROFESOR - Flujo Completo: Pasar Lista

### 1️⃣ Inicio: Menú → "Mis Clases Hoy"

```
[Usuario toca en Navigation Drawer: "📅 Mis Clases Hoy"]

Auto-envía: "Muéstrame mis clases de hoy"
Context: { currentScreen: "mis_clases_hoy", role: "Profesor_academia", usuarioId: 12 }

🤖 Asistente Virtual (2.3s después):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📅 Tus clases de hoy - Miércoles, 23 de octubre de 2025

Tienes 3 sesiones programadas:

┌────────────────────────────────────────────────────────────┐
│ Hora        │ Curso                 │ Aula   │ Estado      │
├────────────────────────────────────────────────────────────┤
│ 08:00-10:00 │ Matemáticas 1º ESO    │ Aula 3 │ ⏳ Pendiente│
│ 11:00-12:30 │ Física 2º ESO         │ Aula 5 │ ✅ Completada│
│ 16:00-17:30 │ Matemáticas 3º ESO    │ Aula 3 │ ⏳ Pendiente│
└────────────────────────────────────────────────────────────┘

Sugerencias:
[🔍 Ver detalle sesión 08:00]
[🔍 Ver detalle sesión 11:00]
[🔍 Ver detalle sesión 16:00]
[📝 Ver todas mis anotaciones]
[📅 Ver clases de mañana]
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

### 2️⃣ Navega: Ver detalle sesión 08:00

```
[Usuario toca: "🔍 Ver detalle sesión 08:00"]

Usuario: "Ver detalle sesión 08:00"
Context: { currentScreen: "detalle_sesion", sesionId: "1", cursoId: "5", role: "Profesor_academia" }

🤖 Asistente Virtual (2.7s después):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
┌─────────────────────────────────────────┐
│ 📚 Detalle de la Sesión                 │
├─────────────────────────────────────────┤
│ 🕐 Horario:   08:00 - 10:00             │
│ 📅 Fecha:     Miércoles, 23/10/2025     │
│ 📖 Curso:     Matemáticas 1º ESO        │
│ 🏫 Aula:      3                          │
│ 👤 Profesor:  María García Ruiz         │
│ 👨‍🎓 Alumnos:  18 previstos              │
│ ⏳ Estado:    Sesión no iniciada        │
├─────────────────────────────────────────┤
│ ⚠️ La sesión debe iniciarse antes de    │
│    las 08:15 para registrar asistencias│
├─────────────────────────────────────────┤
│ Acciones:                                │
│ [▶️ Iniciar sesión ahora]               │
│ [👥 Ver lista de alumnos]               │
│ [📝 Ver anotaciones de esta clase]      │
└──────────────────────────────────────────┘

Sugerencias:
- "Ver historial de sesiones del curso"
- "Volver a mis clases de hoy"
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

### 3️⃣ Acción: Iniciar sesión

```
[Usuario toca: "▶️ Iniciar sesión ahora"]

Usuario: "Iniciar sesión ahora"
Context: { currentScreen: "detalle_sesion", sesionId: "1", actionType: "iniciar_sesion" }

🤖 Asistente Virtual (1.8s después):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ Sesión iniciada correctamente

┌─────────────────────────────────────────┐
│ 📚 Matemáticas 1º ESO - Aula 3          │
├─────────────────────────────────────────┤
│ 🕐 Iniciada:  08:02                     │
│ 🟢 Estado:    Sesión en curso           │
├─────────────────────────────────────────┤
│ Acciones:                                │
│ [✅ Pasar lista de asistencia]          │
│ [📝 Añadir anotación general]           │
│ [⏹️ Finalizar sesión anticipadamente]   │
└──────────────────────────────────────────┘

Sugerencias:
- "Ver alumnos del curso"
- "Volver a mis clases"
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

### 4️⃣ Acción: Pasar lista

```
[Usuario toca: "✅ Pasar lista de asistencia"]

Usuario: "Pasar lista de asistencia"
Context: { currentScreen: "pasar_lista", sesionId: "1", cursoId: "5", actionType: "pasar_lista" }

🤖 Asistente Virtual (2.2s después):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📋 Pasar Lista - Matemáticas 1º ESO
🕐 08:00 - 10:00 | 📅 23/10/2025 | 🏫 Aula 3

Marca la asistencia de cada alumno:

┌────────────────────────────────────────────────────────────────┐
│ # │ Alumno                   │ Estado      │ Anotaciones      │
├────────────────────────────────────────────────────────────────┤
│ 1 │ Juan Pérez García        │ ✅ Presente │ 0 📝             │
│ 2 │ María López Sánchez      │ ✅ Presente │ 2 [Ver]          │
│ 3 │ Pedro Ruiz Martín        │ ❌ Ausente  │ 1 [Ver]          │
│ 4 │ Ana García Rodríguez     │ ✅ Presente │ 0                │
│ 5 │ Luis Fernández Soto      │ ✅ Presente │ 0                │
│ 6 │ Carmen Jiménez López     │ ✅ Presente │ 3 [Ver]          │
│...│ ...                      │ ...         │ ...              │
│18 │ Roberto Sánchez Díaz     │ ✅ Presente │ 0                │
└────────────────────────────────────────────────────────────────┘

Resumen: 17 presentes | 1 ausente

💡 Sugerencias por alumno (toca en la fila):
- Ver anotaciones
- Nueva anotación
- Cambiar estado asistencia

Sugerencias:
- "Guardar lista"
- "Añadir anotación general de la clase"
- "Marcar todos presentes"
- "Volver a detalle de sesión"
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

### 5️⃣ Sub-acción: Ver anotaciones de un alumno

```
[Usuario toca en fila de "María López Sánchez" → "Ver anotaciones"]

Usuario: "Ver anotaciones de María López Sánchez"
Context: { 
    currentScreen: "ver_anotaciones_alumno", 
    alumnoId: "2", 
    cursoId: "5",
    sesionId: "1"
}

🤖 Asistente Virtual (2.5s después):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
┌────────────────────────────────────────────────────────────────┐
│ 📝 Anotaciones - María López Sánchez                          │
│ 📚 Curso: Matemáticas 1º ESO                                  │
├────────────────────────────────────────────────────────────────┤
│ Fecha      │ Tipo           │ Contenido                        │
├────────────────────────────────────────────────────────────────┤
│ 20/10/2025 │ Comportamiento │ Participación activa. Resolvió  │
│            │                │ 3 ejercicios en la pizarra ✅    │
├────────────────────────────────────────────────────────────────┤
│ 18/10/2025 │ Evaluación     │ Examen tema 3: 8.5/10 ⭐         │
│            │                │ Bien en geometría, mejorar      │
│            │                │ álgebra                          │
├────────────────────────────────────────────────────────────────┤
│ Total anotaciones: 2                                           │
├────────────────────────────────────────────────────────────────┤
│ Acciones:                                                       │
│ [➕ Nueva anotación para María]                                │
└─────────────────────────────────────────────────────────────────┘

Sugerencias:
- "Ver estadísticas de María"
- "Volver a lista de asistencia"
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

### 6️⃣ Sub-acción: Nueva anotación

```
[Usuario toca: "➕ Nueva anotación para María"]

Usuario: "Nueva anotación para María"
Context: { 
    currentScreen: "crear_anotacion",
    alumnoId: "2",
    cursoId: "5",
    sesionId: "1",
    actionType: "alta"
}

🤖 Asistente Virtual (1.5s después):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
➕ Crear Nueva Anotación

Alumno: María López Sánchez
Curso: Matemáticas 1º ESO
Sesión: 23/10/2025 08:00

Selecciona el tipo de anotación:

Sugerencias (tipo):
[⚠️ Ausencia]
[📊 Evaluación]
[🎭 Comportamiento]
[📝 General]
[🎯 Observación pedagógica]

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

```
[Usuario toca: "🎭 Comportamiento"]

Usuario: "Comportamiento"

🤖 Asistente Virtual (0.8s después):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ Tipo seleccionado: Comportamiento

Ahora escribe el contenido de la anotación:

💡 Ejemplo: "Excelente participación resolviendo ejercicios en pizarra"

[El usuario escribe en el input de texto normal del chat]
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

```
[Usuario escribe y envía: "Muy buena actitud. Ayudó a sus compañeros con dudas."]

Usuario: "Muy buena actitud. Ayudó a sus compañeros con dudas."
Context: { actionType: "confirmar_anotacion", tipoAnotacion: "Comportamiento" }

🤖 Asistente Virtual (1.9s después):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ Anotación guardada correctamente

┌─────────────────────────────────────────┐
│ 📝 Nueva anotación registrada           │
├─────────────────────────────────────────┤
│ 👤 Alumno:     María López Sánchez      │
│ 📚 Curso:      Matemáticas 1º ESO       │
│ 📅 Fecha:      23/10/2025 08:02         │
│ 🎭 Tipo:       Comportamiento           │
├─────────────────────────────────────────┤
│ 📝 Contenido:                           │
│ "Muy buena actitud. Ayudó a sus        │
│  compañeros con dudas."                 │
└─────────────────────────────────────────┘

Sugerencias:
- "Nueva anotación para otro alumno"
- "Ver todas las anotaciones de María"
- "Volver a lista de asistencia"
- "Ir a mis clases de hoy"
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## 🏢 ADMIN ACADEMIA - Flujo: Gestionar Cursos

### 1️⃣ Menú → "Cursos"

```
[Usuario toca: "📚 Cursos"]

Auto-envía: "Muéstrame todos los cursos de mi academia"
Context: { currentScreen: "cursos", role: "Admin_academia", academiaId: 5 }

🤖 Asistente Virtual (2.4s después):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📚 Cursos de tu Academia

Tienes 8 cursos activos:

┌────────────────────────────────────────────────────────────────┐
│ Curso                  │ Alumnos    │ Profesor         │ Estado│
├────────────────────────────────────────────────────────────────┤
│ Inglés B1 - Mañanas    │ 18/20      │ María García     │ Activo│
│ Español A2 - Tardes    │ 12/15      │ Carlos Martínez  │ Activo│
│ Francés Iniciación     │ 8/12       │ Ana Ruiz         │ Activo│
│ Matemáticas 1º ESO     │ 18/20      │ Pedro López      │ Activo│
│ Física 2º ESO          │ 15/18      │ Pedro López      │ Activo│
│ Inglés A1 - Infantil   │ 10/12      │ María García     │ Activo│
│ Español B2 - Intensivo │ 20/20      │ Carlos Martínez  │ Activo│
│ Alemán Básico          │ 5/15       │ Laura Sánchez    │ Activo│
└────────────────────────────────────────────────────────────────┘

Total: 8 cursos | 106 alumnos inscritos

Sugerencias:
- "Ver detalle Inglés B1 - Mañanas"
- "Ver detalle Español A2 - Tardes"
- "Crear nuevo curso"
- "Ver estadísticas de cursos"
- "Ver profesores disponibles"
- "Buscar curso"
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

### 2️⃣ Ver detalle de un curso

```
[Usuario toca: "🔍 Ver detalle 'Inglés B1 - Mañanas'"]

Usuario: "Ver detalle del curso Inglés B1 - Mañanas"
Context: { currentScreen: "detalle_curso", cursoId: "1", academiaId: 5 }

🤖 Asistente Virtual (2.6s después):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
┌─────────────────────────────────────────────────┐
│ 📚 Curso: Inglés B1 - Mañanas                  │
├─────────────────────────────────────────────────┤
│ 📖 Nombre:          Inglés B1 - Mañanas        │
│ 📅 Año académico:   2024-2025                   │
│ 📆 Inicio:          01/09/2024                  │
│ 📆 Fin:             30/06/2025                  │
│ 👥 Tipo alumnos:    Adultos                     │
│ ✅ Estado:          Activo                      │
│ ✅ Acepta alumnos:  Sí                          │
├─────────────────────────────────────────────────┤
│ 👨‍🎓 Alumnos:        18 inscritos / 20 máximo    │
│ 📈 Ocupación:       90%                         │
│ 👤 Profesor:        María García Ruiz          │
├─────────────────────────────────────────────────┤
│ 📅 Horarios:                                    │
│ • Lunes y Miércoles: 10:00 - 12:00             │
│ • Viernes: 10:00 - 11:30                       │
├─────────────────────────────────────────────────┤
│ 💰 Tarifa:          85€/mes                     │
├─────────────────────────────────────────────────┤
│ Acciones:                                        │
│ [👥 Ver alumnos (18)] [📅 Ver horarios]        │
│ [✏️ Editar curso] [👤 Cambiar profesor]         │
│ [➕ Inscribir alumno] [💰 Editar tarifas]       │
└──────────────────────────────────────────────────┘

Sugerencias:
- "Ver historial de clases"
- "Volver a lista de cursos"
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## 👑 ADMIN PLATAFORMA - Flujo: Gestionar Academias

### 1️⃣ Menú → "Academias"

```
[Usuario toca: "🏢 Academias"]

Auto-envía: "Muéstrame todas las academias de la plataforma"
Context: { currentScreen: "academias", role: "Admin_plataforma" }

🤖 Asistente Virtual (3.1s después):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🏢 Academias de la Plataforma

Total registradas: 15 academias

┌────────────────────────────────────────────────────────────────┐
│ Academia                │ Ciudad      │ Alumnos │ Cursos │ Estado│
├────────────────────────────────────────────────────────────────┤
│ Academia Madrid Centro  │ Madrid      │ 145     │ 12     │ Activa│
│ Idiomas Barcelona       │ Barcelona   │ 98      │ 8      │ Activa│
│ Centro Formación Sevilla│ Sevilla     │ 76      │ 6      │ Activa│
│ Academia Valencia Este  │ Valencia    │ 112     │ 10     │ Activa│
│ Idiomas Málaga          │ Málaga      │ 54      │ 5      │ Activa│
│ Centro Estudios Bilbao  │ Bilbao      │ 67      │ 7      │ Activa│
│ Academia Zaragoza Norte │ Zaragoza    │ 89      │ 9      │ Activa│
│ Formación Murcia        │ Murcia      │ 43      │ 4      │ Activa│
│ ...                     │ ...         │ ...     │ ...    │ ...   │
└────────────────────────────────────────────────────────────────┘

📊 Estadísticas globales:
- Total alumnos: 1,024
- Total cursos: 117
- Promedio alumnos/academia: 68

Sugerencias:
- "Ver detalle Academia Madrid Centro"
- "Crear nueva academia"
- "Ver estadísticas detalladas"
- "Buscar academia"
- "Ver ingresos totales"
- "Ver usuarios de la plataforma"
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## 📝 PROFESOR - Flujo: Gestionar Anotaciones

### 1️⃣ Menú → "Mis Anotaciones"

```
[Usuario toca: "📝 Mis Anotaciones"]

Auto-envía: "Muéstrame mis anotaciones recientes"
Context: { currentScreen: "mis_anotaciones", role: "Profesor_academia", usuarioId: 12 }

🤖 Asistente Virtual (2.8s después):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
┌────────────────────────────────────────────────────────────────┐
│ 📝 Tus Anotaciones Recientes                                  │
├────────────────────────────────────────────────────────────────┤
│ Fecha   │ Curso              │ Alumno           │ Tipo    │ 🔍 │
├────────────────────────────────────────────────────────────────┤
│23/10/25 │Matemáticas 1º ESO  │María López       │Comporta.│ 🔍 │
│20/10/25 │Física 2º ESO       │Juan Pérez        │Evaluaci.│ 🔍 │
│18/10/25 │Matemáticas 1º ESO  │Pedro Ruiz        │Ausencia │ 🔍 │
│17/10/25 │Matemáticas 3º ESO  │Ana García        │Comporta.│ 🔍 │
│16/10/25 │Física 2º ESO       │Luis Fernández    │Evaluaci.│ 🔍 │
│15/10/25 │Matemáticas 1º ESO  │Carmen Jiménez    │Comporta.│ 🔍 │
│13/10/25 │Matemáticas 3º ESO  │[Clase general]   │General  │ 🔍 │
│12/10/25 │Física 2º ESO       │Roberto Sánchez   │Comporta.│ 🔍 │
│11/10/25 │Matemáticas 1º ESO  │María López       │Evaluaci.│ 🔍 │
│10/10/25 │Matemáticas 3º ESO  │Pedro Martín      │Ausencia │ 🔍 │
├────────────────────────────────────────────────────────────────┤
│ Total: 47 anotaciones (mostrando últimas 10)                  │
├────────────────────────────────────────────────────────────────┤
│ Acciones:                                                       │
│ [➕ Nueva anotación]                                           │
└─────────────────────────────────────────────────────────────────┘

Sugerencias:
- "Filtrar por curso"
- "Filtrar por alumno"
- "Filtrar por tipo"
- "Ver anotaciones del mes"
- "Ver estadísticas"
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

### 2️⃣ Nueva anotación desde menú

```
[Usuario toca: "➕ Nueva anotación"]

Usuario: "Nueva anotación"
Context: { currentScreen: "crear_anotacion_menu", actionType: "alta" }

🤖 Asistente Virtual (1.3s después):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
➕ Crear Nueva Anotación

Paso 1 de 4: Selecciona el curso

Tus cursos:

Sugerencias (cursos):
[📚 Matemáticas 1º ESO (18 alumnos)]
[📚 Física 2º ESO (15 alumnos)]
[📚 Matemáticas 3º ESO (20 alumnos)]

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

```
[Usuario toca: "📚 Matemáticas 1º ESO"]

Usuario: "Matemáticas 1º ESO"

🤖 Asistente Virtual (1.1s después):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ Curso seleccionado: Matemáticas 1º ESO

Paso 2 de 4: ¿Es una anotación general o para un alumno específico?

Sugerencias:
[📝 Anotación general de la clase]
[👤 Anotación de alumno específico]

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

```
[Usuario toca: "👤 Anotación de alumno específico"]

Usuario: "Anotación de alumno específico"

🤖 Asistente Virtual (1.7s después):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ Tipo: Anotación de alumno

Paso 3 de 4: Selecciona el alumno

Alumnos de Matemáticas 1º ESO (18 alumnos):

┌────────────────────────────────────────────────────────────────┐
│ # │ Alumno                   │ Última anotación                │
├────────────────────────────────────────────────────────────────┤
│ 1 │ Juan Pérez García        │ Hace 3 días                     │
│ 2 │ María López Sánchez      │ Hoy                             │
│ 3 │ Pedro Ruiz Martín        │ Hace 5 días                     │
│...│ ...                      │ ...                             │
└────────────────────────────────────────────────────────────────┘

Sugerencias (por alumno - toca en la fila):
[👤 Seleccionar alumno]

💡 O escribe el nombre del alumno
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## 💡 PATRONES COMUNES EN TODOS LOS FLUJOS

### Patrón 1: Navegación con "Migas de pan"
```
Cada respuesta incluye:
[↩️ Volver a X]       ← Pantalla anterior
[🏠 Ir a inicio]      ← Reset al dashboard
```

### Patrón 2: Acciones CRUD consistentes
```
Lista:
- [🔍 Ver detalle]
- [➕ Crear nuevo]
- [🔎 Buscar/Filtrar]

Detalle:
- [✏️ Editar]
- [🗑️ Eliminar/Dar de baja]
- [📊 Ver estadísticas]
- [↩️ Volver a lista]
```

### Patrón 3: Confirmaciones de acciones destructivas
```
Usuario: "Eliminar anotación"

🤖: "⚠️ ¿Estás seguro de eliminar esta anotación?

Anotación a eliminar:
Fecha: 20/10/2025
Alumno: Juan Pérez
Tipo: Evaluación
Contenido: 'Examen: 8.5/10...'

Esta acción no se puede deshacer.

Sugerencias:
[✅ Sí, eliminar]
[❌ Cancelar]"
```

### Patrón 4: Feedback de éxito
```
🤖: "✅ [Acción] realizada correctamente

[Resumen de lo que se hizo]

Sugerencias:
[➕ Hacer otra vez]
[↩️ Volver a...]
[🏠 Ir a inicio]"
```

---

## ✅ CONCLUSIÓN

### Todos los flujos siguen el mismo patrón:
1. **Menú del drawer** → Pre-carga mensaje
2. **Respuesta con datos** → Tabla + sugerencias
3. **Sugerencias contextuales** → Navegación profunda
4. **Acciones CRUD** → Confirmación + feedback
5. **Vuelta atrás** → Siempre disponible

### MockChatRepository solo necesita:
```kotlin
when (context.currentScreen) {
    "mis_clases_hoy" -> generateMisClasesHoy()
    "detalle_sesion" -> generateDetalleSesion(context.sesionId)
    "pasar_lista" -> generatePasarLista(context.sesionId)
    "ver_anotaciones_alumno" -> generateAnotacionesAlumno(context.alumnoId)
    "crear_anotacion" -> generateCrearAnotacion(context)
    // ... etc
}
```

**Con esto cubrimos el 100% de la funcionalidad** 🚀
