# Mapa Conceptual - Arquitectura Chat-Driven UI

```
┌─────────────────────────────────────────────────────────────────────┐
│                         APLICACIÓN ANDROID                          │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                    NavigationDrawer                          │  │
│  │                                                              │  │
│  │  ┌────────────────────────────────────────────────────┐     │  │
│  │  │  Header: Nombre Usuario + Rol/Academia            │     │  │
│  │  └────────────────────────────────────────────────────┘     │  │
│  │                                                              │  │
│  │  🏠 Inicio                                                   │  │
│  │  💬 Chat Libre                                               │  │
│  │  ──────────────────────────────                             │  │
│  │  [OPCIONES DINÁMICAS SEGÚN ROL]                             │  │
│  │  ──────────────────────────────                             │  │
│  │  ⚙️ Configuración                                            │  │
│  │  🚪 Cerrar sesión                                            │  │
│  │                                                              │  │
│  │  ┌──────────────────────────────────────┐                   │  │
│  │  │ Al tocar una opción:                │                   │  │
│  │  │ 1. Cerrar drawer                    │                   │  │
│  │  │ 2. Auto-enviar mensaje al chat      │                   │  │
│  │  │ 3. Simular respuesta (2-3.5s)       │                   │  │
│  │  │ 4. Mostrar sugerencias              │                   │  │
│  │  └──────────────────────────────────────┘                   │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                       ChatScreen                             │  │
│  │                  *** ÚNICA PANTALLA ***                      │  │
│  │                                                              │  │
│  │  ┌────────────────────────────────────────────────────┐     │  │
│  │  │  AppTopBar: "Chat asistente virtual"              │     │  │
│  │  └────────────────────────────────────────────────────┘     │  │
│  │                                                              │  │
│  │  ┌────────────────────────────────────────────────────┐     │  │
│  │  │          Lista de mensajes (LazyColumn)           │     │  │
│  │  │                                                    │     │  │
│  │  │  👤 Usuario: "Muéstrame mis clases de hoy"        │     │  │
│  │  │                                                    │     │  │
│  │  │  🤖 Asistente: "📅 Tus clases de hoy..."          │     │  │
│  │  │     [Tabla con datos mock]                        │     │  │
│  │  │     Sugerencias:                                  │     │  │
│  │  │     🔍 Ver detalle clase 08:00                    │     │  │
│  │  │     📝 Ver anotaciones                            │     │  │
│  │  │     ▶️ Iniciar sesión                             │     │  │
│  │  │                                                    │     │  │
│  │  │  [Al tocar sugerencia → nuevo mensaje]           │     │  │
│  │  │                                                    │     │  │
│  │  └────────────────────────────────────────────────────┘     │  │
│  │                                                              │  │
│  │  ┌────────────────────────────────────────────────────┐     │  │
│  │  │  [Input] "Escribe un mensaje..."  [Enviar 📤]    │     │  │
│  │  └────────────────────────────────────────────────────┘     │  │
│  └──────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘

         ▼                                           ▼
         
┌──────────────────────┐              ┌──────────────────────┐
│   ChatViewModel      │              │  SessionStore        │
│                      │              │                      │
│  - messages: Flow    │              │  - name: Flow        │
│  - loading: Flow     │              │  - role: Flow        │
│  - sendMessage()     │              │  - academiaId: Flow  │
│  - reset()           │              │  - tokens: Flow      │
└──────────────────────┘              └──────────────────────┘
         ▼
         
┌──────────────────────────────────────────────────────────────┐
│              ChatRepository (Interface)                      │
│                                                              │
│  suspend fun sendMessage(                                    │
│      messages: List<ChatMessageDto>,                         │
│      context: Map<String, Any?>?                             │
│  ): Result<Envelope<GenericItem>>                            │
└──────────────────────────────────────────────────────────────┘
         ▼                              ▼
         
┌──────────────────────┐    ┌───────────────────────────────┐
│ RealChatRepository   │    │  MockChatRepository           │
│ (Retrofit)           │    │  (Para demo/presentación)     │
│                      │    │                               │
│ - Llama al backend   │    │ - Detecta currentScreen       │
│ - Usa tokens JWT     │    │ - Genera respuestas mock      │
│                      │    │ - Simula latencia 2-3.5s      │
│                      │    │ - Incluye sugerencias         │
└──────────────────────┘    └───────────────────────────────┘
                                         ▼
                                         
                            ┌───────────────────────────────┐
                            │      MockData                 │
                            │                               │
                            │  - getCursos()                │
                            │  - getAlumnos()               │
                            │  - getSesiones()              │
                            │  - getProfesores()            │
                            │  - getAnotaciones()           │
                            │  - etc.                       │
                            └───────────────────────────────┘
```

---

## 🎯 FLUJO DE NAVEGACIÓN DETALLADO

### Ejemplo: Profesor → "Mis Clases Hoy" → "Ver detalle" → "Pasar lista"

```
1. Usuario abre drawer
   ↓
2. Toca "📅 Mis Clases Hoy"
   ↓
3. handleMenuClick() detecta:
   - option.id = "mis_clases_hoy"
   - option.chatMessage = "Muéstrame mis clases de hoy"
   - context = { currentScreen: "mis_clases_hoy", role: "Profesor_academia" }
   ↓
4. Drawer se cierra
   ↓
5. ChatViewModel.sendMessageWithContext()
   - Añade mensaje del usuario al estado
   - Muestra "loading" (burbuja con puntos animados)
   ↓
6. MockChatRepository.sendMessage()
   - delay(random(2000, 3500))  // Simular latencia
   - Detecta context.currentScreen == "mis_clases_hoy"
   - Genera respuesta:
     * message: "📅 Tus clases de hoy - Miércoles, 23/10/2025"
     * data.items: [clase1, clase2, ...]
     * uiSuggestions: ["Ver detalle 08:00", "Ver anotaciones", ...]
   ↓
7. ChatScreen renderiza respuesta:
   - Texto del asistente
   - Tabla con CompactList (hora, curso, aula, estado)
   - Chips de sugerencias
   ↓
8. Usuario toca sugerencia "🔍 Ver detalle clase 08:00"
   ↓
9. Suggestion.onClick():
   - vm.disableSuggestionsForMessage(messageIndex)
   - vm.sendMessage("Ver detalle de la clase de 08:00")
   - context actualizado: { 
       currentScreen: "detalle_sesion",
       sesionId: "1",
       cursoId: "5"
     }
   ↓
10. MockChatRepository detecta currentScreen == "detalle_sesion"
    - Genera respuesta con detalles de la sesión
    - Nuevas sugerencias: ["▶️ Iniciar sesión", "✅ Pasar lista", ...]
    ↓
11. Usuario toca "✅ Pasar lista"
    ↓
12. MockChatRepository detecta action == "pasar_lista"
    - Genera tabla interactiva de alumnos
    - data.items: alumnos con estado de asistencia
    - Sugerencias por alumno: "Ver anotaciones de X"
    - Sugerencias globales: "💾 Guardar lista"
    ↓
13. Usuario marca ausencias/presencias
    ↓
14. Toca "💾 Guardar lista"
    ↓
15. MockChatRepository simula guardado
    - delay(1500)
    - Responde: "✅ Lista guardada correctamente"
    - Nueva sugerencia: "↩️ Volver a mis clases"
```

---

## 🗂️ ESTRUCTURA DEL CONTEXTO

### Context Map que viaja con cada mensaje:

```kotlin
val context = mapOf(
    // Pantalla/Flujo actual
    "currentScreen" to "detalle_sesion",      // Dónde está el usuario
    "previousScreen" to "mis_clases_hoy",     // De dónde viene
    
    // Datos del usuario
    "role" to "Profesor_academia",
    "academiaId" to 5,
    "usuarioId" to 12,
    
    // Recursos en contexto (IDs de registros activos)
    "sesionId" to "1",
    "cursoId" to "5",
    "alumnoId" to null,  // null si no aplica
    
    // Acción/Operación actual
    "actionType" to "pasar_lista",  // "consulta" | "alta" | "modificacion" | "baja"
    
    // Filtros activos
    "filters" to mapOf(
        "fecha" to "2025-10-23",
        "estado" to "activo"
    ),
    
    // Token de sesión conversacional (futuro)
    "conversationToken" to "conv_abc123xyz",
    
    // Navegación
    "canGoBack" to true,
    "parentResource" to "mis_clases_hoy"
)
```

---

## 📊 MAPEO: Opción del Menú → Mock Response

| Opción del Menú | currentScreen | Mock Response Incluye |
|-----------------|---------------|----------------------|
| **Mis Clases Hoy** | `mis_clases_hoy` | Lista de sesiones de hoy + sugerencias: "Ver detalle", "Iniciar sesión" |
| **Mis Clases Semana** | `mis_clases_semana` | Lista de sesiones semanales + filtros por día |
| **Mis Cursos** | `mis_cursos` | Lista de cursos del profesor + "Ver alumnos", "Historial" |
| **Mis Anotaciones** | `mis_anotaciones` | Lista últimas anotaciones + "Nueva anotación", "Filtrar" |
| **Cursos** (Admin) | `cursos` | Lista de cursos de la academia + "Crear curso", "Ver detalle" |
| **Alumnos** (Admin) | `alumnos` | Lista de alumnos + "Alta alumno", "Ver detalle" |
| **Profesores** (Admin) | `profesores` | Lista de profesores + "Asignar a curso", "Ver detalle" |
| **Gestión Económica** | `gestion_economica` | Extractos pendientes + "Movimientos", "Registrar pago" |
| **Academias** (Super) | `academias` | Lista de academias + "Ver detalle", "Crear academia" |

---

## 🎨 COMPONENTES UI REUTILIZABLES

### CompactList (ya existe en ChatScreen.kt)
- ✅ Tabla genérica con scroll
- ✅ summaryFields para columnas
- ✅ Botón "Ver detalle" por fila
- ✅ Paginación automática (mostrar 5, luego expandir)

### SuggestionChip (ya existe)
- ✅ Chips interactivos
- ✅ Se desactivan al hacer click
- ✅ Tipos: Genérica, Paginación, Registro

### TypingIndicator (ya existe)
- ✅ Puntos animados mientras carga
- ✅ Aparece como burbuja del asistente

### ModalBottomSheet para detalles (ya existe)
- ✅ Muestra todos los campos de un registro
- ✅ Formato limpio key-value

---

## ✅ CONCLUSIÓN

### Lo que YA tienes implementado:
- ✅ ChatScreen con toda la UI necesaria
- ✅ Navegación con drawer
- ✅ Componentes reutilizables (CompactList, chips, etc.)
- ✅ Sistema de sugerencias tipadas
- ✅ Gestión de estado con ViewModel

### Lo que FALTA implementar:
- ❌ Menú dinámico por rol (1 día)
- ❌ MockChatRepository con respuestas contextuales (2 días)
- ❌ MockData con datos realistas (1 día)
- ❌ Lógica de contexto y navegación profunda (1 día)
- ❌ Pulido de UX y transiciones (1 día)

**TOTAL: ~6-7 días para tener demo completa** 🚀

### Ventajas de esta arquitectura:
1. ✅ **Simplicidad extrema**: Una sola pantalla
2. ✅ **Experiencia natural**: Todo es conversación
3. ✅ **Fácil de mockear**: Solo respuestas de texto + JSON
4. ✅ **Escalable**: Añadir funciones = añadir casos al switch
5. ✅ **Transición suave**: Cuando tengas backend, solo cambias el repo
