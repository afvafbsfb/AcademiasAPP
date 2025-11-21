# Guía de Estilo UX - Tablas y Presentación de Datos

**Fecha:** 23 de octubre de 2025  
**Propósito:** Definir el estilo visual consistente para todas las respuestas del chat con datos tabulares

---

## 🎨 PRINCIPIOS DE DISEÑO

### 1. **Tabla = Texto, NO ASCII Art**
✅ **CORRECTO**: Tabla con bordes simples de texto
❌ **INCORRECTO**: Formato de lista con viñetas o texto plano

### 2. **Iconos de Estado Integrados**
✅ **CORRECTO**: `🔴 08:00 - 10:00 | Matemáticas 1º ESO`
❌ **INCORRECTO**: `08:00 - 10:00 | Matemáticas 1º ESO | Estado: No iniciada`

### 3. **Iconos de Acción en Última Columna**
Siempre incluir columna final con icono 🔍 para ver detalle

### 4. **Navegación Temporal en Encabezados**
Para vistas de calendario: `<< Anterior | 🔵 Hoy | Siguiente >>`

---

## 📋 PATRONES DE TABLAS

### Patrón 1: Lista Simple (1 Fila = 1 Línea)

```
┌──────────────────────────────┬──────────┬────────┬────┐
│ Nombre del Curso             │ Alumnos  │ Estado │ 🔍 │
├──────────────────────────────┼──────────┼────────┼────┤
│ Inglés B1 - Mañanas          │ 18/20    │ Activo │ 🔍 │
│ Español A2 - Tardes          │ 12/15    │ Activo │ 🔍 │
└──────────────────────────────┴──────────┴────────┴────┘
```

**Uso:** Listados de entidades (cursos, alumnos, profesores, academias)

---

### Patrón 2: Lista con Detalle Multi-Línea (1 Fila = 2+ Líneas)

```
┌──────────────────────────────────────────────────────────────┬────┐
│ 🔴 08:00 - 10:00 | Matemáticas 1º ESO | Aula 3              │ 🔍 │
│    Alumnos: 18 previstos                                     │    │
├──────────────────────────────────────────────────────────────┼────┤
│ 🟢 11:00 - 12:30 | Física 2º ESO | Aula 5                   │ 🔍 │
│    Alumnos: 15 (14 presentes, 1 ausencia)                   │    │
└──────────────────────────────────────────────────────────────┴────┘
```

**Uso:** Cuando hay información secundaria importante (ej: sesiones con datos de asistencia)

---

### Patrón 3: Detalle en 2 Columnas (Clave-Valor)

```
┌─────────────────────────────┬─────────────────────────────┐
│ 📚 Curso                    │ Matemáticas 1º ESO          │
│ 🕐 Horario                  │ 08:00 - 10:00               │
│ 🏫 Aula                     │ Aula 3                      │
│ 📅 Fecha                    │ 23/10/2025 (Miércoles)      │
│ 👤 Profesor                 │ María García Ruiz           │
│ 👨‍🎓 Alumnos previstos       │ 18                          │
│ 🔴 Estado                   │ Sesión no iniciada          │
└─────────────────────────────┴─────────────────────────────┘
```

**Uso:** Detalles de un registro individual (detalle de sesión, curso, alumno, etc.)

---

### Patrón 4: Tabla Interactiva con Múltiples Acciones

```
┌────┬─────────────────────────┬───────────┬─────────────┬────┐
│ #  │ Alumno                  │ Asistencia│ Anotaciones │ 📝 │
├────┼─────────────────────────┼───────────┼─────────────┼────┤
│ 1  │ Juan Pérez García       │ ✅ Presente│ 0           │ 📝 │
│ 2  │ María López Sánchez     │ ✅ Presente│ 2           │ 📝 │
│ 3  │ Pedro Ruiz Martín       │ ❌ Ausente │ 1           │ 📝 │
└────┴─────────────────────────┴───────────┴─────────────┴────┘

💡 Al tocar el check ✅/❌ cambias el estado de asistencia
💡 Al tocar 📝 gestionas anotaciones del alumno
```

**Uso:** Pasar lista, gestionar anotaciones, acciones por fila

---

### Patrón 5: Tabla con Texto Multi-Línea en Celdas

```
┌────────────┬────────────────┬────────────────────────────────────┬────┐
│ Fecha      │ Tipo           │ Contenido                          │ ⚙️ │
├────────────┼────────────────┼────────────────────────────────────┼────┤
│ 20/10/2025 │ Comportamiento │ Participación activa. Resolvió 3   │ ⚙️ │
│            │                │ ejercicios en la pizarra ✅         │    │
├────────────┼────────────────┼────────────────────────────────────┼────┤
│ 18/10/2025 │ Evaluación     │ Examen tema 3: 8.5/10 ⭐           │ ⚙️ │
│            │                │ Bien en geometría, mejorar álgebra │    │
└────────────┴────────────────┴────────────────────────────────────┴────┘
```

**Uso:** Anotaciones, comentarios largos, observaciones

---

## 🎯 ICONOS ESTÁNDAR POR CONTEXTO

### Estados de Sesión:
- 🔴 **No iniciada** (aún no ha comenzado)
- 🟢 **En curso / Completada** (activa o finalizada con éxito)
- 🟡 **Pendiente** (programada para más tarde)

### Acciones de Usuario:
- 🔍 **Ver detalle** (abrir vista detallada)
- 📝 **Anotaciones** (gestionar anotaciones)
- ⚙️ **Opciones** (editar/eliminar)
- ✅ **Marcar presente**
- ❌ **Marcar ausente**
- ▶️ **Iniciar**
- ⏹️ **Finalizar**
- 💾 **Guardar**

### Tipos de Anotación:
- ⚠️ **Ausencia**
- 📊 **Evaluación**
- 🎭 **Comportamiento**
- 📝 **General**
- 🎯 **Observación pedagógica**

### Navegación:
- ↩️ **Volver atrás**
- 🏠 **Ir a inicio**
- ➕ **Crear nuevo**
- 🔎 **Buscar/Filtrar**

---

## 📐 ESTRUCTURA DE RESPUESTA ESTÁNDAR

### Template General:

```
🤖 Asistente:
"[EMOJI] [TÍTULO]
[Subtítulo opcional con navegación temporal o stats]

[TABLA PRINCIPAL]

[LEYENDA DE ICONOS/ESTADOS - si es necesario]

[NOTAS INFORMATIVAS con 💡 - si es necesario]

Sugerencias:
[Lista de sugerencias contextuales]
```

### Ejemplo Completo:

```
🤖 Asistente:
"📅 Tus clases de hoy
<< 24/10/2025 | 🔵 Miércoles, 23 de octubre de 2025 | 22/10/2025 >>

┌──────────────────────────────────────────────────────────────┬────┐
│ 🔴 08:00 - 10:00 | Matemáticas 1º ESO | Aula 3              │ 🔍 │
│    Alumnos: 18 previstos                                     │    │
├──────────────────────────────────────────────────────────────┼────┤
│ 🟢 11:00 - 12:30 | Física 2º ESO | Aula 5                   │ 🔍 │
│    Alumnos: 15 (14 presentes, 1 ausencia)                   │    │
└──────────────────────────────────────────────────────────────┴────┘

Estados: 🔴 No iniciada | 🟢 En curso/Completada | 🟡 Pendiente

Sugerencias:
📝 Ver todas mis anotaciones de hoy
📊 Resumen de ausencias del día
📅 Ver clases de mañana
```

---

## 🎯 COLOCACIÓN DE ACCIONES (CRÍTICO)

### Tipos de Sugerencias y su Ubicación

#### 1️⃣ Sugerencias tipo="Registro" → DENTRO de la tabla

**Qué son:** Acciones específicas sobre el registro mostrado en detalle

**Dónde:** Footer de la tabla, sección "Acciones:"

**Ejemplos:**
```
┌─────────────────────────────────────────┐
│ 📋 Anotación #245                       │
├─────────────────────────────────────────┤
│ 📅 Fecha:         21 Oct 2024 - 10:30   │
│ 👤 Alumno:        María López           │
├─────────────────────────────────────────┤
│ Acciones:                                │  ← Footer con acciones
│ [✏️ Editar] [🗑️ Eliminar]                │
│ [👁️ Cambiar Visibilidad]                │
└──────────────────────────────────────────┘
```

**Acciones tipo Registro:**
- `[✏️ Editar]` - Modificar registro
- `[🗑️ Eliminar]` - Borrar registro
- `[📝 Pasar Lista]` - Acción sobre sesión
- `[📋 Ver Anotaciones]` - Ver anotaciones de sesión
- `[⚙️ Opciones de Sesión]` - Configurar sesión
- `[👥 Ver Alumnos]` - Ver alumnos del curso
- `[👨‍🏫 Ver Profesores]` - Ver profesores del curso
- `[▶️ Iniciar Sesión]` - Iniciar sesión mostrada
- `[⏹️ Finalizar Sesión]` - Finalizar sesión

---

#### 2️⃣ Sugerencias tipo="Generica" y "Paginacion" → FUERA de la tabla

**Qué son:** Navegación global, filtros, acciones no relacionadas al registro específico

**Dónde:** Como SuggestionChips debajo de la tabla, sección "Sugerencias:"

**Ejemplo:**
```
┌─────────────────────────────────────────┐
│ 📋 Anotación #245                       │
│ ...datos...                              │
├─────────────────────────────────────────┤
│ Acciones:                                │
│ [✏️ Editar] [🗑️ Eliminar]                │  ← Tipo "Registro" DENTRO
└──────────────────────────────────────────┘

Sugerencias:                                   ← Tipo "Generica" FUERA
- "Ver todas las anotaciones de María López"
- "Volver a mis anotaciones"
- "Página anterior"                            ← Tipo "Paginacion" FUERA
```

**Acciones tipo Generica:**
- "Ver todas mis sesiones"
- "Ver sesiones de la semana"
- "Volver a mis clases"
- "Ver todos los cursos"
- "Crear nuevo curso"

**Acciones tipo Paginacion:**
- "Página anterior"
- "Página siguiente"
- "Ir a página X"

---

## ✅ VENTAJAS DE ESTE ESTILO

1. ✅ **Limpio y escaneable** - Fácil de leer en móvil
2. ✅ **Consistente** - Mismo patrón en toda la app
3. ✅ **Iconos visuales** - Información rápida sin leer texto
4. ✅ **Acciones claras** - Siempre sabes qué puedes hacer
5. ✅ **Responsivo** - Las tablas se adaptan al ancho de burbuja
6. ✅ **Navegación intuitiva** - Flechas temporales << >> integradas
7. ✅ **Contexto claro** - Acciones de registro dentro, navegación fuera

---

## 🚫 ANTI-PATRONES (Evitar)

### ❌ NO: Texto plano sin estructura
```
Matemáticas 1º ESO
Hora: 08:00 - 10:00
Aula: 3
Alumnos: 18
```

### ❌ NO: Listas con viñetas
```
• Matemáticas 1º ESO - 08:00-10:00
• Física 2º ESO - 11:00-12:30
```

### ❌ NO: Información redundante
```
🔴 08:00 - 10:00 | Matemáticas 1º ESO
Estado: Sesión no iniciada  ← REDUNDANTE con el icono 🔴
```

### ❌ NO: Tablas ASCII complejas
```
+----------------+----------+----------+
| Curso          | Alumnos  | Estado   |
+----------------+----------+----------+
| Inglés B1      | 18/20    | Activo   |
+----------------+----------+----------+
```

### ❌ NO: Acciones de registro FUERA de tabla de detalle
```
┌─────────────────────────────────────────┐
│ 📋 Anotación #245                       │
│ ...datos...                              │
└──────────────────────────────────────────┘

[✏️ Editar] [🗑️ Eliminar]  ← INCORRECTO: deben estar DENTRO
```

✅ **CORRECTO:**
```
┌─────────────────────────────────────────┐
│ 📋 Anotación #245                       │
│ ...datos...                              │
├─────────────────────────────────────────┤
│ Acciones:                                │
│ [✏️ Editar] [🗑️ Eliminar]                │  ← CORRECTO: dentro del footer
└──────────────────────────────────────────┘
```

---

## 🔄 FLUJO DE INTERACCIÓN CON TABLAS

### Paso 1: Lista con icono 🔍
```
┌──────────────────────────────┬────┐
│ 🔴 08:00 - 10:00 | Mate 1º  │ 🔍 │ ← Usuario toca 🔍
└──────────────────────────────┴────┘
```

### Paso 2: Detalle en 2 columnas + Sugerencias
```
┌─────────────────┬─────────────────┐
│ 📚 Curso        │ Matemáticas 1º  │
│ 🕐 Horario      │ 08:00 - 10:00   │
└─────────────────┴─────────────────┘

Sugerencias:
▶️ Iniciar sesión  ← Usuario toca esta
✅ Pasar lista
```

### Paso 3: Acción ejecutada + Nueva tabla
```
✅ Sesión iniciada

┌─────────────────┬─────────────────┐
│ 🔴 Estado       │ 🟢 En curso     │ ← Cambio de estado
└─────────────────┴─────────────────┘

Sugerencias:
✅ Pasar lista  ← Siguiente acción lógica
```

### Paso 4: Tabla interactiva
```
┌────┬───────────────┬──────────┬────┐
│ #  │ Alumno        │ Asistencia│ 📝 │
├────┼───────────────┼──────────┼────┤
│ 1  │ Juan Pérez    │ ✅        │ 📝 │ ← Tocar ✅ o 📝
└────┴───────────────┴──────────┴────┘
```

---

## 📊 RESUMEN DE CAMBIOS vs VERSIÓN ANTERIOR

| Aspecto | Antes | Ahora |
|---------|-------|-------|
| **Formato tabla** | Lista con viñetas | Tabla con bordes |
| **Estado sesión** | Texto: "Estado: No iniciada" | Icono: 🔴 integrado |
| **Acciones** | Sugerencias separadas | Icono 🔍 en cada fila |
| **Detalle** | Texto plano multi-línea | Tabla 2 columnas clave-valor |
| **Navegación temporal** | No había | << Ant \| Hoy \| Sig >> |
| **Interactividad** | Solo sugerencias | Checks ✅/❌ + iconos 📝 |
| **Multi-línea en celda** | No soportado | Celdas con salto de línea |

---

## ✅ CONCLUSIÓN

Este estilo unificado proporciona:
- ✅ **UX consistente** en toda la app
- ✅ **Información densa pero legible**
- ✅ **Acciones claras e intuitivas**
- ✅ **Navegación fluida** sin cambiar de pantalla
- ✅ **Visual atractivo** con iconos y estructura

**Aplicar este estilo a TODAS las respuestas del MockChatRepository** 🚀
