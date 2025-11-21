# Cambios en Colocación de Sugerencias

**Fecha:** 23 de octubre de 2025  
**Cambio:** Mover sugerencias tipo="Registro" DENTRO de las tablas de detalle

---

## 🎯 Resumen del Cambio

### ANTES (versión anterior):
Todas las sugerencias aparecían FUERA de las tablas, como chips al final del mensaje.

### AHORA (nueva especificación):
- **Sugerencias tipo="Registro"**: DENTRO de la tabla de detalle, en un footer de "Acciones"
- **Sugerencias tipo="Generica" y "Paginacion"**: FUERA de la tabla, como chips normales

---

## 📋 Clasificación de Sugerencias

### Tipo "Registro" (van DENTRO):
Acciones específicas sobre el registro mostrado:
- `[✏️ Editar]` - Modificar el registro actual
- `[🗑️ Eliminar]` - Borrar el registro actual  
- `[👁️ Cambiar Visibilidad]` - Toggle visibilidad
- `[📝 Pasar Lista]` - Acción sobre la sesión mostrada
- `[📋 Ver Anotaciones]` - Ver anotaciones de la sesión
- `[⚙️ Opciones de Sesión]` - Configuración de la sesión
- `[👥 Ver Alumnos]` - Ver alumnos del curso mostrado
- `[👨‍🏫 Ver Profesores]` - Ver profesores del curso
- `[📅 Ver Horarios]` - Ver horarios del curso
- `[➕ Nueva Anotación]` - Crear anotación (cuando ya hay una anotación mostrada)
- `[▶️ Iniciar Sesión]` - Iniciar la sesión mostrada
- `[⏹️ Finalizar Sesión]` - Finalizar la sesión mostrada

### Tipo "Generica" (van FUERA):
Acciones globales o de navegación:
- `"Ver todas mis sesiones"` - Navegar a lista completa
- `"Ver sesiones de la semana"` - Filtro temporal
- `"Volver a mis clases"` - Navegación hacia atrás
- `"Ver todos los cursos"` - Navegar a lista
- `"Crear nuevo curso"` - Alta global
- `"➕ Nueva Anotación"` - Crear anotación (cuando NO hay ninguna mostrada, desde lista)
- `"Ver todas las academias"` - Navegar a lista

### Tipo "Paginacion" (van FUERA):
Navegación entre páginas:
- `"Página anterior"`
- `"Página siguiente"`
- `"Ir a página X"`

---

## 🔄 Ejemplos de Transformación

### EJEMPLO 1: Detalle de Sesión

#### ❌ ANTES (Incorrecto):
```
┌─────────────────────────────────────────┐
│ 📅 Sesión: Lun 21 Oct - 10:00-11:30    │
│ 📚 Curso: Matemáticas Avanzadas         │
│ 👤 Profesor: Juan García                │
│ 🔴 Estado: NO_INICIADA                  │
└─────────────────────────────────────────┘

[📝 Pasar Lista]
[📋 Ver Anotaciones (0)]
[⚙️ Opciones de Sesión]

Sugerencias:
- "Ver todas mis sesiones"
- "Ver sesiones de la semana"
```

#### ✅ AHORA (Correcto):
```
┌─────────────────────────────────────────┐
│ 📅 Sesión: Lun 21 Oct - 10:00-11:30    │
│ 📚 Curso: Matemáticas Avanzadas         │
│ 👤 Profesor: Juan García                │
│ 🔴 Estado: NO_INICIADA                  │
├─────────────────────────────────────────┤
│ Acciones:                                │
│ [📝 Pasar Lista] [📋 Ver Anotaciones]   │
│ [⚙️ Opciones de Sesión]                 │
└──────────────────────────────────────────┘

Sugerencias:
- "Ver todas mis sesiones"
- "Ver sesiones de la semana"
```

---

### EJEMPLO 2: Detalle de Anotación

#### ❌ ANTES (Incorrecto):
```
┌─────────────────────────────────────────┐
│ 📋 Anotación #245                       │
├─────────────────────────────────────────┤
│ 📅 Fecha:         21 Oct 2024 - 10:30   │
│ 👤 Alumno:        María López           │
│ 📚 Curso:         Matemáticas Avanzadas │
│ 📝 Observación: Excelente participación │
└─────────────────────────────────────────┘

[✏️ Editar]
[🗑️ Eliminar]
[👁️ Cambiar Visibilidad]

Sugerencias:
- "Ver todas las anotaciones de María López"
- "Volver a mis anotaciones"
```

#### ✅ AHORA (Correcto):
```
┌─────────────────────────────────────────┐
│ 📋 Anotación #245                       │
├─────────────────────────────────────────┤
│ 📅 Fecha:         21 Oct 2024 - 10:30   │
│ 👤 Alumno:        María López           │
│ 📚 Curso:         Matemáticas Avanzadas │
│ 📝 Observación: Excelente participación │
├─────────────────────────────────────────┤
│ Acciones:                                │
│ [✏️ Editar] [🗑️ Eliminar]                │
│ [👁️ Cambiar Visibilidad]                │
└──────────────────────────────────────────┘

Sugerencias:
- "Ver todas las anotaciones de María López"
- "Volver a mis anotaciones"
```

---

### EJEMPLO 3: Detalle de Curso

#### ❌ ANTES (Incorrecto):
```
┌─────────────────────────────────────────┐
│ 📚 Curso: Inglés B1 - Mañanas          │
├─────────────────────────────────────────┤
│ 👥 Alumnos:       18/20                 │
│ 👨‍🏫 Profesores:    María García          │
│ 📊 Estado:        🟢 ACTIVO             │
└─────────────────────────────────────────┘

[👥 Ver Alumnos]
[👨‍🏫 Ver Profesores]
[📅 Ver Horarios Completos]
[✏️ Editar Curso]

Sugerencias:
- "Ver todos los cursos"
- "Crear nuevo curso"
```

#### ✅ AHORA (Correcto):
```
┌─────────────────────────────────────────┐
│ 📚 Curso: Inglés B1 - Mañanas          │
├─────────────────────────────────────────┤
│ 👥 Alumnos:       18/20                 │
│ 👨‍🏫 Profesores:    María García          │
│ 📊 Estado:        🟢 ACTIVO             │
├─────────────────────────────────────────┤
│ Acciones:                                │
│ [👥 Ver Alumnos] [👨‍🏫 Ver Profesores]   │
│ [📅 Ver Horarios] [✏️ Editar Curso]     │
└──────────────────────────────────────────┘

Sugerencias:
- "Ver todos los cursos"
- "Crear nuevo curso"
```

---

## 💻 Implementación en Código

### Estructura de Suggestion con type

```kotlin
data class Suggestion(
    val id: String,
    val displayText: String,
    val type: String,  // "Registro" | "Generica" | "Paginacion"
    val recordAction: String? = null,  // "Consulta" | "Modificacion" | "Baja" | "Alta"
    val record: RecordRef? = null,
    val pagination: PaginationSuggestion? = null,
    val contextToken: String? = null
)
```

### Renderizado en ChatScreen

```kotlin
// En el composable que muestra un mensaje con detalle de registro
@Composable
fun MessageWithDetail(message: ChatMessage) {
    // 1. Mostrar tabla/card con datos
    DetailCard(item = message.data.items.first())
    
    // 2. Filtrar y mostrar sugerencias de REGISTRO dentro
    val registroActions = message.suggestions.filter { it.type == "Registro" }
    if (registroActions.isNotEmpty()) {
        ActionsRow(suggestions = registroActions) // DENTRO del card
    }
    
    // 3. Filtrar y mostrar sugerencias GENERICAS y PAGINACION fuera
    val outsideSuggestions = message.suggestions.filter { 
        it.type == "Generica" || it.type == "Paginacion" 
    }
    if (outsideSuggestions.isNotEmpty()) {
        SuggestionsChips(suggestions = outsideSuggestions) // FUERA
    }
}

@Composable
fun ActionsRow(suggestions: List<Suggestion>) {
    Column {
        Divider()
        Text("Acciones:", style = MaterialTheme.typography.labelMedium)
        FlowRow(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            suggestions.forEach { suggestion ->
                Button(
                    onClick = { /* handle action */ },
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(suggestion.displayText)
                }
            }
        }
    }
}
```

---

## ✅ Beneficios de Este Cambio

1. **Contexto claro**: Las acciones sobre un registro están visualmente conectadas al registro
2. **Reducción de ruido**: Las sugerencias fuera de la tabla son solo navegación/filtros
3. **UX móvil mejorada**: Los botones de acción están cerca de la información relevante
4. **Semántica correcta**: Las sugerencias tipo="Registro" tienen significado claro
5. **Escalabilidad**: Al agregar más acciones sobre registros, la UI no se satura

---

## 📝 Checklist de Actualización

- [x] Actualizar GUIA_ESTILO_UX_TABLAS.md con sección de colocación de acciones
- [x] Actualizar todos los ejemplos en PLAN_AMPLIACION_MOCKS_Y_UX.md
- [x] Actualizar todos los flujos en EJEMPLOS_FLUJOS_COMPLETOS.md
- [ ] Actualizar MockChatRepository para generar suggestions con type correcto
- [ ] Implementar ActionsRow composable
- [ ] Actualizar ChatScreen para renderizar suggestions según type
- [ ] Actualizar MAPA_ARQUITECTURA_CHAT_DRIVEN.md con diagrama de renderizado

---

## 🎯 Próximos Pasos

1. **Implementar ActionsRow composable** en `ui/components/`
2. **Modificar renderizado en ChatScreen** para separar suggestions por type
3. **Actualizar MockChatRepository** para asignar type correcto a cada suggestion
4. **Probar flujos completos** para validar UX
