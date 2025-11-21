# Plan de Ampliación - Mocks y UX Completa para Presentación

**Fecha:** 23 de octubre de 2025  
**Objetivo:** Implementar mocks completos en Android para simular toda la funcionalidad del sistema mientras se desarrolla el backend, permitiendo una demo funcional para presentación.

---

## 🎯 Contexto y Estrategia

### ✅ Lo que YA tenemos funcionando:
- ✅ **Login completo** con autenticación JWT
- ✅ **Chat funcional** como centro de la aplicación
- ✅ **Contrato de API bien definido** con `Envelope<T>`, sugerencias tipadas y paginación
- ✅ **Navigation Drawer** con menú lateral
- ✅ **Roles implementados:** Admin_plataforma, Admin_academia, Profesor_academia

### 🎯 Lo que vamos a implementar:
1. **Ampliar el menú del Navigation Drawer** según el rol del usuario
2. **Crear pantallas con datos mockeados** para todas las funcionalidades
3. **Simular latencia realista** (2-3.5 segundos) en respuestas de chat
4. **Implementar mocks inteligentes** que respondan según el contexto
5. **Permitir navegación contextual al chat** desde cualquier pantalla

---

## 📐 Arquitectura del Contrato Actual

### Contrato de Chat (Backend ↔ Android)
```kotlin
// Request
ChatPayload(
    messages: List<ChatMessageDto>,
    context: Map<String, Any?>?  // ✅ CLAVE: contexto para preguntas situadas
)

// Response
Envelope<GenericItem>(
    status: String,
    message: String?,
    data: DataSection<GenericItem>?(
        type: String?,              // "usuarios" | "cursos" | "alumnos"...
        items: List<GenericItem>?,  // Lista de registros dinámicos
        summaryFields: List<String>?,
        pagination: PaginationInfo?
    ),
    uiSuggestions: List<Suggestion>?  // Sugerencias tipadas
)

// Sugerencias tipadas
Suggestion(
    id: String,
    displayText: String,
    type: String,  // "Paginacion" | "Registro" | "Generica"
    recordAction: String?,  // "Consulta" | "Modificacion" | "Baja" | "Alta"
    record: RecordRef?,
    pagination: PaginationSuggestion?,
    contextToken: String?
)
```

### ✅ Fortalezas del contrato actual:
1. **Genérico y extensible**: `GenericItem` permite cualquier tipo de datos sin cambiar el contrato
2. **Sugerencias inteligentes**: El backend puede guiar la navegación
3. **Contexto dinámico**: El `context` del `ChatPayload` permite preguntas situadas
4. **Paginación integrada**: Navegación entre páginas de resultados

### 🔧 Ajustes menores recomendados para futuro:
1. **Añadir `actionType` al context**: Para distinguir consultas, altas, modificaciones
2. **Incluir `currentScreen` en el context**: El backend sabrá desde dónde pregunta el usuario
3. **Token de sesión contextual**: Mantener el contexto entre múltiples preguntas relacionadas

**✅ CONCLUSIÓN:** El contrato actual es **PERFECTO** para el MVP. Solo haremos ajustes cuando tengamos el backend listo.

---

## 🎨 DISEÑO UX: TODO ES CHAT GUIADO

### 🔑 CONCEPTO CLAVE:
**No hay "pantallas" separadas. Solo hay UN ChatScreen donde TODO sucede.**

El Navigation Drawer **no navega a pantallas**, sino que:
1. **Pre-carga un mensaje contextual** en el chat
2. **Simula una respuesta automática** del asistente
3. **Muestra sugerencias interactivas** para navegar más profundo

**Flujo:**
```
Menú "Mis Clases" → Chat con mensaje auto-enviado: "Muéstrame mis clases de hoy"
                  ↓
                  Respuesta mock inmediata con lista
                  ↓
                  Sugerencias: "Ver detalle clase 08:00" | "Pasar lista" | "Ver anotaciones"
```

---

## 📱 Navigation Drawer - ESTRUCTURA DEFINITIVA

### 🎯 Opciones del Drawer (para TODOS los roles)

```
📱 Navigation Drawer
├── 👤 [Header]
│   ├── Nombre del usuario
│   └── Rol / Academia
│
├── 🏠 Inicio
│   → Chat: Resumen personalizado según rol
│
├── 💬 Chat Libre
│   → ChatScreen limpio (sin mensaje pre-cargado)
│
├── [OPCIONES DINÁMICAS SEGÚN ROL - Ver abajo]
│
└── ⚙️ Configuración
    ├── Mi perfil
    └── Cerrar sesión
```

---

## 👥 OPCIONES POR ROL (dinámicas en el menú)

### 1️⃣ Admin_plataforma (Acceso Global)
```
📱 Opciones específicas:
├── 🏢 Academias
│   → Chat: "Muéstrame todas las academias"
│   → Sugerencias: "Ver detalle Academia X" | "Crear nueva academia"
│
├── 👥 Usuarios Plataforma
│   → Chat: "Muéstrame usuarios del sistema"
│   → Sugerencias: "Ver detalle usuario X" | "Crear nuevo usuario"

```

**Mock de respuesta ejemplo - "Academias":**
```
🤖 Asistente:
"🏢 Academias de la Plataforma
Total: 15 academias registradas

┌────────────────────────────┬────────────┬─────────┬────────┬────────┬────┐
│ Nombre                     │ Ciudad     │ Alumnos │ Cursos │ Estado │ 🔍 │
├────────────────────────────┼────────────┼─────────┼────────┼────────┼────┤
│ Academia Madrid Centro     │ Madrid     │ 145     │ 12     │ Activa │ 🔍 │
│ Idiomas Barcelona          │ Barcelona  │ 98      │ 8      │ Activa │ 🔍 │
│ Centro Formación Sevilla   │ Sevilla    │ 76      │ 6      │ Activa │ 🔍 │
│ Academia Valencia Este     │ Valencia   │ 112     │ 10     │ Activa │ 🔍 │
│ Idiomas Málaga             │ Málaga     │ 54      │ 5      │ Activa │ 🔍 │
│ Centro Estudios Bilbao     │ Bilbao     │ 67      │ 7      │ Activa │ 🔍 │
│ Academia Zaragoza Norte    │ Zaragoza   │ 89      │ 9      │ Activa │ 🔍 │
│ Formación Murcia           │ Murcia     │ 43      │ 4      │ Activa │ 🔍 │
│ ...                        │ ...        │ ...     │ ...    │ ...    │ 🔍 │
└────────────────────────────┴────────────┴─────────┴────────┴────────┴────┘

📊 Estadísticas globales:
• Total alumnos: 1,024
• Total cursos: 117
• Promedio alumnos/academia: 68

Sugerencias:
➕ Crear nueva academia
📊 Ver estadísticas detalladas
🔎 Buscar academia
📈 Ver ingresos totales
👥 Ver usuarios de la plataforma
```

---

### 2️⃣ Admin_academia (Gestión Completa de su Academia)
```
📱 Opciones específicas:
├── 📚 Cursos
│   → Chat: "Muéstrame mis cursos activos"
│   → Sugerencias: "Ver detalle Curso X" | "Crear nuevo curso" | "Ver horarios"
│   → Sugerencias desde detalle: "Ver alumnos inscritos" | "Historial clases" | "Editar curso"
│
├── 👨‍🎓 Alumnos
│   → Chat: "Muéstrame todos los alumnos de mi academia"
│   → Sugerencias: "Ver detalle alumno X" | "Alta nuevo alumno" | "Alumnos con pagos pendientes"
│   → Sugerencias desde detalle: "Ver inscripciones" | "Ver pagos" | "Editar datos"
│
├── 👥 Profesores
│   → Chat: "Muéstrame los profesores de mi academia"
│   → Sugerencias: "Ver detalle profesor X" | "Asignar a curso" | "Ver horarios"
│
├── 🗓️ Sesiones (Clases)
│   → Chat: "Muéstrame las clases de hoy"
│   → Sugerencias: "Ver clases de mañana" | "Ver semana completa" | "Ver detalle clase X"
│
├── 💰 Gestión Económica
│   → Chat: "Muéstrame extractos pendientes"
│   → Sugerencias: "Movimientos octubre" | "Informe de ingresos" | "Alumnos con deuda"
│
└── 🏫 Mi Academia
    → Chat: "Muéstrame datos de mi academia"
    → Sugerencias: "Editar datos" | "Ver configuración" | "Ver estadísticas"
```

**Mock de respuesta ejemplo - "Cursos":**
```
🤖 Asistente:
"📚 Cursos de tu Academia
Total: 8 cursos activos | 106 alumnos inscritos

┌──────────────────────────┬──────────┬──────────────────┬────────┬────┐
│ Nombre del Curso         │ Alumnos  │ Profesor         │ Estado │ 🔍 │
├──────────────────────────┼──────────┼──────────────────┼────────┼────┤
│ Inglés B1 - Mañanas      │ 18/20    │ María García     │ Activo │ 🔍 │
│ Español A2 - Tardes      │ 12/15    │ Carlos Martínez  │ Activo │ 🔍 │
│ Francés Iniciación       │ 8/12     │ Ana Ruiz         │ Activo │ 🔍 │
│ Matemáticas 1º ESO       │ 18/20    │ Pedro López      │ Activo │ 🔍 │
│ Física 2º ESO            │ 15/18    │ Pedro López      │ Activo │ 🔍 │
│ Inglés A1 - Infantil     │ 10/12    │ María García     │ Activo │ 🔍 │
│ Español B2 - Intensivo   │ 20/20    │ Carlos Martínez  │ Activo │ 🔍 │
│ Alemán Básico            │ 5/15     │ Laura Sánchez    │ Activo │ 🔍 │
└──────────────────────────┴──────────┴──────────────────┴────────┴────┘

Sugerencias:
➕ Crear nuevo curso
📊 Ver estadísticas de cursos
👥 Ver profesores disponibles
🔎 Buscar curso
```

---

### 3️⃣ Profesor_academia (Operaciones Pedagógicas)

```
📱 Opciones específicas:
├── 📅 Mis Clases Hoy
│   → Chat: "Muéstrame mis clases de hoy [23/10/2025 - Miércoles]"
│   → Sugerencias: "Ver detalle clase 08:00" | "Ver clases de mañana"
│
├── 🗓️ Mis Clases (Semana)
│   → Chat: "Muéstrame mis clases de esta semana"
│   → Sugerencias: "Filtrar por día" | "Ver calendario completo"
│
├── 📚 Mis Cursos
│   → Chat: "Muéstrame los cursos que imparto"
│   → Sugerencias: "Ver detalle Curso X" | "Ver alumnos inscritos" | "Historial de clases"
│
└── 📝 Mis Anotaciones
    → Chat: "Muéstrame mis anotaciones recientes"
    → Sugerencias: "Nueva anotación" | "Filtrar por curso" | "Editar anotación X"
```

**Mock de respuesta ejemplo - "Mis Clases Hoy":**
```
🤖 Asistente:
"📅 Tus clases de hoy
<< 24/10/2025 | 🔵 Miércoles, 23 de octubre de 2025 | 22/10/2025 >>

📋 Clases programadas (3):

┌──────────────────────────────────────────────────────────────┬────┐
│ � 08:00 - 10:00 | Matemáticas 1º ESO | Aula 3              │ 🔍 │
│    Alumnos: 18 previstos                                     │    │
├──────────────────────────────────────────────────────────────┼────┤
│ � 11:00 - 12:30 | Física 2º ESO | Aula 5                   │ 🔍 │
│    Alumnos: 15 (14 presentes, 1 ausencia)                   │    │
├──────────────────────────────────────────────────────────────┼────┤
│ 🟡 16:00 - 17:30 | Matemáticas 3º ESO | Aula 3              │ 🔍 │
│    Alumnos: 20 previstos                                     │    │
└──────────────────────────────────────────────────────────────┴────┘

Estados: 🔴 No iniciada | 🟢 En curso/Completada | 🟡 Pendiente

Sugerencias:
� Ver todas mis anotaciones de hoy
📊 Resumen de ausencias del día
📅 Ver clases de mañana
```

---

## 🎯 FLUJO DETALLADO: Caso de Uso Profesor

### **Menú: "Mis Clases Hoy"** → Chat pre-cargado

#### 1️⃣ Respuesta inicial (auto-generada)
```
Usuario: [mensaje invisible auto-enviado] "Muéstrame mis clases de hoy"

🤖 Asistente:
"� Tus clases de hoy - Miércoles, 23/10/2025

[Tabla de clases con: hora, curso, aula, estado, alumnos]

Sugerencias:
🔍 Ver detalle clase 08:00 Matemáticas
▶️ Iniciar sesión 08:00
📝 Ver anotaciones de hoy
```
```

#### 2️⃣ Usuario toca: "Ver detalle clase 08:00"
```
#### 2️⃣ Usuario toca: "🔍 Ver detalle" (clase 08:00)
```
Usuario: [al tocar 🔍] "Ver detalle de la clase de 08:00"

🤖 Asistente:
"📚 Detalle de la Sesión

┌─────────────────────────────┬─────────────────────────────┐
│ 📚 Curso                    │ Matemáticas 1º ESO          │
│ 🕐 Horario                  │ 08:00 - 10:00               │
│ 🏫 Aula                     │ Aula 3                      │
│ 📅 Fecha                    │ 23/10/2025 (Miércoles)      │
│ 👤 Profesor                 │ María García Ruiz           │
│ 👨‍🎓 Alumnos previstos       │ 18                          │
│ 🔴 Estado                   │ Sesión no iniciada          │
├─────────────────────────────┴─────────────────────────────┤
│ Acciones:                                                  │
│ [▶️ Iniciar sesión] [✅ Ver alumnos]                      │
│ [📝 Ver anotaciones]                                      │
└────────────────────────────────────────────────────────────┘

Sugerencias:
- "Historial de sesiones del curso"
- "Volver a mis clases de hoy"
```
```

#### 3️⃣ Usuario toca: "▶️ Iniciar sesión ahora"
```
Usuario: [al tocar] "Iniciar sesión ahora"

🤖 Asistente:
"✅ Sesión iniciada correctamente a las 08:02

┌─────────────────────────────┬─────────────────────────────┐
│ 📚 Curso                    │ Matemáticas 1º ESO          │
│ 🕐 Horario                  │ 08:00 - 10:00               │
│ 🔴 Estado                   │ 🟢 Sesión en curso          │
│ ⏰ Iniciada a las           │ 08:02                       │
├─────────────────────────────┴─────────────────────────────┤
│ Acciones:                                                  │
│ [✅ Pasar lista] [📝 Añadir anotación]                    │
│ [⏹️ Finalizar sesión]                                     │
└────────────────────────────────────────────────────────────┘

Sugerencias:
- "Volver a mis clases"
```
```

#### 4️⃣ Usuario toca: "✅ Pasar lista / Ver alumnos"
```
Usuario: "Pasar lista"

🤖 Asistente:
"📋 Lista de alumnos - Matemáticas 1º ESO
🕐 08:00 - 10:00 | 📅 23/10/2025 | 🏫 Aula 3

┌────┬─────────────────────────┬───────────┬─────────────┬────┐
│ #  │ Alumno                  │ Asistencia│ Anotaciones │ 📝 │
├────┼─────────────────────────┼───────────┼─────────────┼────┤
│ 1  │ Juan Pérez García       │ ✅ Presente│ 0           │ 📝 │
│ 2  │ María López Sánchez     │ ✅ Presente│ 2           │ 📝 │
│ 3  │ Pedro Ruiz Martín       │ ❌ Ausente │ 1           │ 📝 │
│ 4  │ Ana García Rodríguez    │ ✅ Presente│ 0           │ 📝 │
│ 5  │ Luis Fernández Soto     │ ✅ Presente│ 0           │ 📝 │
│ 6  │ Carmen Jiménez López    │ ✅ Presente│ 3           │ 📝 │
│... │ ...                     │ ...       │ ...         │ 📝 │
│ 18 │ Roberto Sánchez Díaz    │ ✅ Presente│ 0           │ 📝 │
└────┴─────────────────────────┴───────────┴─────────────┴────┘

📊 Resumen: 17 presentes | 1 ausente

💡 Al tocar el check ✅/❌ cambias el estado de asistencia
💡 Al tocar 📝 gestionas anotaciones del alumno

Sugerencias:
- "Guardar y finalizar lista"
- "Añadir anotación general de clase"
- "Marcar todos presentes"
- "Volver a detalle de sesión"
```
```

🤖 Asistente:
"� Clase: Matemáticas 1º ESO
🕐 Hora: 08:00 - 10:00
🏫 Aula: 3
📅 Fecha: 23/10/2025
👤 Profesor: María García
👨‍🎓 Alumnos previstos: 18

Estado: ⏳ Sesión no iniciada

Sugerencias:
▶️ Iniciar sesión
👥 Ver lista de alumnos
📝 Ver anotaciones de esta clase
📊 Historial de esta clase
```
```

#### 3️⃣ Usuario toca: "Iniciar sesión"
```
Usuario: [al tocar] "Iniciar sesión de 08:00"

🤖 Asistente:
"✅ Sesión iniciada correctamente

📚 Matemáticas 1º ESO - 08:00
Estado: 🟢 Sesión en curso

Sugerencias:
✅ Pasar lista
📝 Añadir anotación general de clase
👥 Ver alumnos presentes
⏹️ Finalizar sesión
```
```

#### 4️⃣ Usuario toca: "Pasar lista"
```
Usuario: "Pasar lista"

🤖 Asistente:
"📋 Lista de alumnos - Matemáticas 1º ESO

[Tabla interactiva con checkboxes:]
ID | Nombre                | Estado   | Anotaciones
1  | Juan Pérez García     | ✅ Presente | 0
2  | María López Sánchez   | ✅ Presente | 2 📝
3  | Pedro Ruiz Martín     | ❌ Ausente  | 1
4  | Ana García Rodríguez  | ✅ Presente | 0
... (18 alumnos)

Sugerencias POR ALUMNO (al tocar):
📝 Ver anotaciones de [Nombre]
➕ Nueva anotación para [Nombre]
✏️ Cambiar estado asistencia

Sugerencias GENERALES:
💾 Guardar lista
📝 Añadir anotación general
↩️ Volver a detalle de clase
```
```

#### 5️⃣ Usuario toca: "📝" (icono de anotaciones de María López)
```
Usuario: [toca 📝 en fila de María López] "Ver anotaciones de María López"

🤖 Asistente:
"📝 Anotaciones de María López Sánchez
📚 Matemáticas 1º ESO

┌────────────┬────────────────┬────────────────────────────────────┬────┐
│ Fecha      │ Tipo           │ Contenido                          │ ⚙️ │
├────────────┼────────────────┼────────────────────────────────────┼────┤
│ 20/10/2025 │ Comportamiento │ Participación activa. Resolvió 3   │ ⚙️ │
│            │                │ ejercicios en la pizarra ✅         │    │
├────────────┼────────────────┼────────────────────────────────────┼────┤
│ 18/10/2025 │ Evaluación     │ Examen tema 3: 8.5/10 ⭐           │ ⚙️ │
│            │                │ Bien en geometría, mejorar álgebra │    │
└────────────┴────────────────┴────────────────────────────────────┴────┘

📊 Total anotaciones: 2

💡 Al tocar ⚙️ puedes editar o eliminar la anotación

Sugerencias:
➕ Nueva anotación para María
↩️ Volver a lista de asistencia
📊 Ver estadísticas de María
```
```

#### 6️⃣ Usuario toca: "➕ Nueva anotación para María"
```
Usuario: "Nueva anotación para María"

🤖 Asistente:
"➕ Crear nueva anotación

┌─────────────────────────────┬─────────────────────────────┐
│ 👤 Alumno                   │ María López Sánchez         │
│ 📚 Curso                    │ Matemáticas 1º ESO          │
│ 📅 Sesión                   │ 23/10/2025 08:00            │
└─────────────────────────────┴─────────────────────────────┘

Selecciona tipo de anotación:

Sugerencias:
⚠️ Ausencia
📊 Evaluación
🎭 Comportamiento
📝 General
🎯 Observación pedagógica
```

```
[Usuario toca: "🎭 Comportamiento"]

🤖 Asistente:
"✅ Tipo seleccionado: 🎭 Comportamiento

Ahora escribe el contenido de la anotación:

💡 Ejemplo: 'Excelente participación resolviendo ejercicios en pizarra'

[El usuario escribe en el input de texto normal del chat]
```

```
[Usuario escribe: "Muy buena actitud. Ayudó a sus compañeros con dudas."]

Usuario: "Muy buena actitud. Ayudó a sus compañeros con dudas."

🤖 Asistente:
"✅ Anotación guardada correctamente

┌─────────────────────────────────────────────────────────────┐
│ 📝 Nueva anotación registrada                               │
├─────────────────────────────┬───────────────────────────────┤
│ 👤 Alumno                   │ María López Sánchez           │
│ 📚 Curso                    │ Matemáticas 1º ESO            │
│ 📅 Fecha                    │ 23/10/2025 08:02              │
│ 🎭 Tipo                     │ Comportamiento                │
│ 📄 Contenido                │ Muy buena actitud. Ayudó a    │
│                             │ sus compañeros con dudas.     │
└─────────────────────────────┴───────────────────────────────┘

La anotación ya está visible en el historial de la alumna.

Sugerencias:
➕ Nueva anotación para otro alumno
📝 Ver todas las anotaciones de María (3)
↩️ Volver a lista de asistencia
```

---

## 🎯 FLUJO: Menú "Mis Anotaciones" (Profesor)

```
Usuario: [selecciona menú] "Mis Anotaciones"

🤖 Asistente:
"📝 Tus anotaciones recientes
Mostrando últimas 10 de 47 anotaciones

┌──────────┬──────────────────┬─────────────────────┬──────────────┬────────────────┬────┐
│ Fecha    │ Curso            │ Alumno              │ Tipo         │ Preview        │ 🔍 │
├──────────┼──────────────────┼─────────────────────┼──────────────┼────────────────┼────┤
│23/10/2025│Matemáticas 1ºESO │María López          │Comportam.    │Muy buena act...│ 🔍 │
│20/10/2025│Física 2º ESO     │Juan Pérez           │Evaluación    │Examen: 8.5/10  │ 🔍 │
│18/10/2025│Matemáticas 1ºESO │Pedro Ruiz           │Ausencia      │Justificada     │ 🔍 │
│17/10/2025│Matemáticas 3ºESO │Ana García           │Comportam.    │Excelente part..│ 🔍 │
│16/10/2025│Física 2º ESO     │Luis Fernández       │Evaluación    │Práctica lab: 9 │ 🔍 │
│15/10/2025│Matemáticas 1ºESO │Carmen Jiménez       │Comportam.    │Participa activ.│ 🔍 │
│13/10/2025│Matemáticas 3ºESO │[Clase general]      │General       │Tema 4 completo │ 🔍 │
│12/10/2025│Física 2º ESO     │Roberto Sánchez      │Comportam.    │Distraído en...│ 🔍 │
│11/10/2025│Matemáticas 1ºESO │María López          │Evaluación    │Ejercicios: 10  │ 🔍 │
│10/10/2025│Matemáticas 3ºESO │Pedro Martín         │Ausencia      │Sin justificar  │ 🔍 │
└──────────┴──────────────────┴─────────────────────┴──────────────┴────────────────┴────┘

Sugerencias:
➕ Nueva anotación
🔎 Filtrar por curso
🔎 Filtrar por alumno
🔎 Filtrar por tipo
📅 Ver anotaciones del mes
📊 Ver estadísticas
```

### Subopción: "Nueva anotación"
```
Usuario: "Nueva anotación"

🤖 Asistente:
"➕ Crear nueva anotación

Paso 1 - Selecciona el curso:

Sugerencias (mis cursos):
📚 Matemáticas 1º ESO
📚 Física 2º ESO
📚 Matemáticas 3º ESO

[Usuario elige curso]

🤖: "Curso seleccionado: Matemáticas 1º ESO

¿Es una anotación general del curso o para un alumno específico?

Sugerencias:
👥 Anotación general de la clase
👤 Anotación de alumno específico"

[Si elige alumno específico]

🤖: "Selecciona el alumno:

[Lista de alumnos inscritos en ese curso]

[Usuario elige alumno y sigue el flujo anterior]
```

---

## ✅ COBERTURA 100% DE LA FUNCIONALIDAD

### Entidades del Modelo de Datos Cubiertas:

| Entidad | Rol que la gestiona | Flujo en Chat |
|---------|-------------------|---------------|
| **Academia** | Admin_plataforma, Admin_academia | ✅ Lista, detalle, crear, editar |
| **Usuario** | Admin_plataforma, Admin_academia | ✅ Lista, detalle, crear (por rol) |
| **Curso** | Admin_academia, Profesor (ver) | ✅ Lista, detalle, crear, editar, ver horarios |
| **HorarioCurso** | Admin_academia | ✅ Incluido en detalle de curso |
| **Alumno** | Admin_academia, Profesor (ver) | ✅ Lista, detalle, crear, ver inscripciones |
| **Inscripcion** | Admin_academia | ✅ Incluido en detalle de alumno/curso |
| **Curso_Profesores** | Admin_academia | ✅ Asignar/desasignar profesor a curso |
| **Sesion (Clase)** | Profesor, Admin_academia (ver) | ✅ Listar, abrir, cerrar, detalle completo |
| **AnotacionesAlumnoSesion** | Profesor | ✅ Ver, crear, editar, eliminar |
| **Tarifa** | Admin_academia | ✅ Ver en detalle de curso/inscripción |
| **Extractos** | Admin_academia | ✅ Lista pendientes, detalle |
| **Movimientos_Extracto** | Admin_academia | ✅ Ver movimientos, registrar pagos |

### ✅ Operaciones CRUD Completas por Chat:

#### **Create (Alta):**
- ✅ Nueva academia (Admin_plataforma)
- ✅ Nuevo usuario (Admin_plataforma, Admin_academia según rol)
- ✅ Nuevo curso (Admin_academia)
- ✅ Nuevo alumno (Admin_academia)
- ✅ Nueva inscripción (Admin_academia)
- ✅ Nueva anotación (Profesor)
- ✅ Nueva sesión/clase (Profesor)
- ✅ Nuevo movimiento de pago (Admin_academia)

#### **Read (Consulta):**
- ✅ Listar cualquier entidad con paginación
- ✅ Ver detalle de cualquier registro
- ✅ Filtros y búsquedas contextuales
- ✅ Estadísticas y resúmenes

#### **Update (Modificación):**
- ✅ Editar datos de academias/usuarios/cursos/alumnos
- ✅ Modificar anotaciones
- ✅ Actualizar estado de sesiones
- ✅ Cambiar asistencias

#### **Delete (Baja):**
- ✅ Baja lógica de entidades
- ✅ Eliminar anotaciones
- ✅ Desasignar profesores de cursos

---

## 🎭 Implementación de Mocks

### 1. Mock del servicio de Chat (MockChatRepository)

```kotlin
class MockChatRepository : ChatRepository {
    
    private val random = Random.Default
    
    override suspend fun sendMessage(
        messages: List<ChatMessageDto>,
        context: Map<String, Any?>?
    ): Result<Envelope<GenericItem>> {
        // Simular latencia realista
        delay(random.nextLong(2000, 3500))
        
        val lastMessage = messages.lastOrNull()?.content ?: ""
        val currentScreen = context?.get("currentScreen") as? String
        val currentRole = context?.get("role") as? String
        val recordId = context?.get("recordId") as? String
        
        // Generar respuesta según contexto
        val response = when {
            // Respuestas contextuales por pantalla
            currentScreen == "detalle_curso" && recordId != null -> 
                generateCursoDetailResponse(lastMessage, recordId)
            
            currentScreen == "detalle_alumno" && recordId != null ->
                generateAlumnoDetailResponse(lastMessage, recordId)
            
            // Preguntas generales
            lastMessage.contains("cuántos alumnos", ignoreCase = true) ->
                generateAlumnosCountResponse(currentRole)
            
            lastMessage.contains("sesiones de hoy", ignoreCase = true) ->
                generateSesionesHoyResponse(currentRole)
            
            lastMessage.contains("qué clases tengo", ignoreCase = true) ->
                generateMisClasesResponse()
            
            else -> generateGenericResponse(lastMessage)
        }
        
        return Result.Success(response)
    }
    
    private fun generateCursoDetailResponse(message: String, cursoId: String): Envelope<GenericItem> {
        return when {
            message.contains("cuántos alumnos", ignoreCase = true) -> {
                Envelope(
                    status = "success",
                    message = "El curso Inglés B1 tiene 18 alumnos inscritos.",
                    data = DataSection(
                        type = "alumnos",
                        items = mockAlumnosList(18),
                        summaryFields = listOf("nombre", "email"),
                        pagination = PaginationInfo(page = 1, size = 18, returned = 18, hasMore = false)
                    ),
                    uiSuggestions = listOf(
                        Suggestion(
                            id = "sug_1",
                            displayText = "Ver asistencias del curso",
                            type = "Generica"
                        ),
                        Suggestion(
                            id = "sug_2",
                            displayText = "Ver horarios del curso",
                            type = "Generica"
                        )
                    )
                )
            }
            else -> generateGenericResponse(message)
        }
    }
    
    private fun generateSesionesHoyResponse(role: String?): Envelope<GenericItem> {
        val sesiones = if (role == "Profesor_academia") {
            listOf(
                mapOf(
                    "id" to 1,
                    "curso" to "Inglés B1",
                    "hora_inicio" to "10:00",
                    "hora_fin" to "11:30",
                    "aula" to "Aula 3",
                    "alumnos" to 18
                ),
                mapOf(
                    "id" to 2,
                    "curso" to "Español A2",
                    "hora_inicio" to "16:00",
                    "hora_fin" to "17:30",
                    "aula" to "Aula 1",
                    "alumnos" to 12
                )
            )
        } else {
            listOf(
                mapOf(
                    "id" to 1,
                    "curso" to "Inglés B1",
                    "profesor" to "María García",
                    "hora_inicio" to "10:00",
                    "hora_fin" to "11:30",
                    "aula" to "Aula 3"
                ),
                // ... más sesiones
            )
        }
        
        return Envelope(
            status = "success",
            message = "Hoy tienes ${sesiones.size} sesiones programadas:",
            data = DataSection(
                type = "sesiones",
                items = sesiones,
                summaryFields = listOf("curso", "hora_inicio")
            ),
            uiSuggestions = listOf(
                Suggestion(
                    id = "sug_1",
                    displayText = "Ver sesiones de mañana",
                    type = "Generica"
                )
            )
        )
    }
    
    private fun mockAlumnosList(count: Int): List<GenericItem> {
        return (1..count).map { i ->
            mapOf(
                "id" to i,
                "nombre" to "Alumno ${i}",
                "email" to "alumno${i}@example.com",
                "telefono" to "600${String.format("%06d", i)}",
                "estado" to if (i % 3 == 0) "Baja" else "Activo"
            )
        }
    }
    
    // ... más funciones de generación de mocks
}
```

### 2. Datos Mock para Listas

```kotlin
object MockData {
    
    fun getCursos(academiaId: Int? = null): List<GenericItem> {
        return listOf(
            mapOf(
                "id" to 1,
                "nombre" to "Inglés B1 - Mañanas",
                "anio_academico" to "2024-2025",
                "fecha_inicio" to "2024-09-01",
                "fecha_fin" to "2025-06-30",
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
                "capacidad_maxima" to 12,
                "alumnos_inscritos" to 8,
                "tipo_alumno" to "Adultos",
                "estado" to "Activo"
            ),
            // ... más cursos
        )
    }
    
    fun getAlumnos(cursoId: Int? = null): List<GenericItem> {
        return listOf(
            mapOf(
                "id" to 1,
                "nombre" to "Juan Pérez García",
                "email" to "juan.perez@example.com",
                "telefono" to "600123456",
                "fecha_nacimiento" to "1990-05-15",
                "cursos_inscritos" to 2,
                "estado_pago" to "Al día"
            ),
            mapOf(
                "id" to 2,
                "nombre" to "María López Sánchez",
                "email" to "maria.lopez@example.com",
                "telefono" to "600234567",
                "fecha_nacimiento" to "1992-08-22",
                "cursos_inscritos" to 1,
                "estado_pago" to "Pendiente"
            ),
            // ... más alumnos
        )
    }
    
    fun getProfesores(): List<GenericItem> {
        return listOf(
            mapOf(
                "id" to 1,
                "nombre" to "María García Ruiz",
                "email" to "maria.garcia@academia.com",
                "rol" to "Profesor_academia",
                "cursos_asignados" to 3,
                "especialidad" to "Inglés"
            ),
            mapOf(
                "id" to 2,
                "nombre" to "Carlos Martínez López",
                "email" to "carlos.martinez@academia.com",
                "rol" to "Profesor_academia",
                "cursos_asignados" to 2,
                "especialidad" to "Español"
            ),
            // ... más profesores
        )
    }
    
    fun getSesionesHoy(): List<GenericItem> {
        return listOf(
            mapOf(
                "id" to 1,
                "curso" to "Inglés B1",
                "hora_inicio" to "10:00",
                "hora_fin" to "11:30",
                "aula" to "Aula 3",
                "profesor" to "María García",
                "alumnos_previstos" to 18,
                "estado" to "Pendiente"
            ),
            mapOf(
                "id" to 2,
                "curso" to "Español A2",
                "hora_inicio" to "16:00",
                "hora_fin" to "17:30",
                "aula" to "Aula 1",
                "profesor" to "Carlos Martínez",
                "alumnos_previstos" to 12,
                "estado" to "Pendiente"
            ),
            // ... más sesiones
        )
    }
}
```

---

## 🚀 Plan de Implementación (Fases)

### **FASE 1: Infraestructura Mock** (1-2 días)
- [ ] Crear `MockChatRepository` con latencia simulada
- [ ] Crear `MockData` con datos realistas de todas las entidades
- [ ] Configurar flag de modo mock en `EnvConfig`
- [ ] Actualizar `AppContainer` para usar mocks cuando esté activado

### **FASE 2: Ampliar Navigation Drawer** (1 día)
- [ ] Modificar `ChatScreen` para menú dinámico según rol
- [ ] Añadir todas las opciones del menú por rol
- [ ] Implementar navegación a pantallas mock (inicialmente placeholders)

### **FASE 3: Pantallas de Listado con Chat Contextual** (3-4 días)
- [ ] `CursosScreen` (lista + detalle + chat contextual)
- [ ] `AlumnosScreen` (lista + detalle + chat contextual)
- [ ] `ProfesoresScreen` (lista + chat contextual)
- [ ] `SesionesScreen` (lista del día + detalle)

Cada pantalla tendrá:
- ✅ Lista de registros mockeados
- ✅ Búsqueda local (filtrado en memoria)
- ✅ Botón flotante de chat contextual
- ✅ Al tocar un registro, pasar contexto al chat

### **FASE 4: Acciones desde Chat** (2-3 días)
- [ ] Implementar sugerencias de acciones en mocks
- [ ] Navegación desde sugerencia a detalle de registro
- [ ] Formularios mock para altas/modificaciones
- [ ] Confirmaciones visuales de acciones simuladas

### **FASE 5: Dashboard y Estadísticas** (2 días)
- [ ] Dashboard de inicio según rol
- [ ] Gráficos mockeados (con librerías de charts)
- [ ] Resúmenes numéricos

### **FASE 6: Pulido para Demo** (1-2 días)
- [ ] Animaciones y transiciones suaves
- [ ] Feedback visual consistente
- [ ] Toast/Snackbar para confirmaciones
- [ ] Modo presentación (ocultar errores técnicos)

---

## 🔧 Ajustes al Contrato (Para futuro, NO urgente)

### Propuesta de mejora en `ChatPayload.context`:
```kotlin
// Actual
context: Map<String, Any?>?

// Propuesta futura (cuando tengamos backend listo)
context: ChatContext?

data class ChatContext(
    val currentScreen: String?,      // "detalle_curso" | "lista_alumnos"...
    val actionType: String?,          // "consulta" | "alta" | "modificacion" | "baja"
    val recordId: String?,            // ID del registro en contexto
    val recordType: String?,          // "curso" | "alumno" | "sesion"...
    val sessionToken: String?,        // Token para mantener contexto entre mensajes
    val filters: Map<String, Any?>?   // Filtros activos en la pantalla
)
```

**Beneficios:**
- El backend sabrá exactamente dónde está el usuario
- Respuestas más contextuales y precisas
- Menos ambigüedad en las preguntas
- Continuidad entre múltiples preguntas relacionadas

**⚠️ Esto NO es bloqueante para los mocks.** Lo implementamos después.

---

## 📊 Alcance del MVP (Recordatorio)

### Entidades MVP (6 entidades):
1. ✅ **Academias** (ya está)
2. ✅ **Usuarios** (ya está)
3. ❌ **Cursos** (nuevo - mock)
4. ❌ **HorarioCurso** (nuevo - mock, incluido en detalle de curso)
5. ❌ **Alumnos** (nuevo - mock)
6. ❌ **Curso_Profesores** (nuevo - mock, relación profesor-curso)
7. ❌ **Sesion** (nuevo - mock, para abrir/cerrar sesiones)

### ✂️ Simplificaciones inteligentes:
- **Sin Aulas** en MVP: HorarioCurso no tiene `aula_id` (se puede añadir después)
- **Sin facturación** en MVP: Extractos, movimientos, descuentos quedan fuera
- **Sin anotaciones** en MVP: AnotacionesAlumnoSesion opcional (o versión simple)

---

## ✅ Conclusión

**Este plan permite:**
1. ✅ Tener una **demo completamente funcional** en 10-14 días
2. ✅ **Validar toda la UX** antes de tocar el backend
3. ✅ **Desarrollo paralelo**: Backend se puede hacer mientras se usa la demo
4. ✅ **Presentación impactante** con flujos completos end-to-end
5. ✅ **El contrato actual es perfecto** - solo ajustes menores futuros

**Próximo paso:**
¿Empezamos con la FASE 1 (Infraestructura Mock) o prefieres que primero te muestre ejemplos concretos de código de alguna pantalla?
