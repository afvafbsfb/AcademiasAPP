# Especificación del Navigation Drawer - Opciones por Rol

**Fecha:** 23 de octubre de 2025  
**Arquitectura:** Chat-Driven UI - Todo es chat guiado con mensajes pre-cargados

---

## 🎯 CONCEPTO ARQUITECTÓNICO

### No hay navegación tradicional:
- ❌ **NO** navegamos a `CursosScreen`, `AlumnosScreen`, etc.
- ✅ **SÍ** pre-cargamos un mensaje en el chat y simulamos respuesta

### Flujo técnico al tocar una opción del menú:
```kotlin
// Ejemplo: Usuario toca "Mis Clases Hoy"
onMenuItemClick("mis_clases_hoy") {
    1. Cerrar drawer
    2. Navegar a ChatScreen (si no estamos ya)
    3. Auto-enviar mensaje: "Muéstrame mis clases de hoy"
    4. MockChatRepository detecta el contexto
    5. Genera respuesta mock instantánea (2-3s delay)
    6. Muestra sugerencias contextuales
}
```

---

## 📱 ESTRUCTURA DEL DRAWER (Código actual)

### Header (Encabezado)
```kotlin
// Fondo con color primario
Column {
    Text(userName ?: "Usuario")  // Nombre del usuario
    Text(roleOrAcademiaName)      // Rol o nombre de academia
}
```

### Opciones actuales (hardcoded):
```kotlin
val itemsList = listOf(
    "Inicio",
    "Chat",           // ← Actual: Chat libre
    "Academias",      // ← Sin implementar
    "Cursos",         // ← Sin implementar
    "Profesores",     // ← Sin implementar
    "Alumnos",        // ← Sin implementar
    "Estadísticas",   // ← Sin implementar
    "Configuración",  // ← Sin implementar
    "Cerrar sesión"
)
```

---

## 🔄 NUEVO DISEÑO: Menú Dinámico por Rol

### Estructura de datos para opciones del menú:

```kotlin
data class MenuOption(
    val id: String,                    // ID único
    val label: String,                 // Texto visible
    val icon: ImageVector,             // Icono material
    val chatMessage: String?,          // Mensaje a enviar al chat (null = acción especial)
    val contextData: Map<String, Any>? = null,  // Contexto adicional
    val roles: List<String>            // Roles que pueden ver esta opción
)
```

---

## 📋 OPCIONES DEL MENÚ POR ROL

### 🌐 Opciones COMUNES (todos los roles)

```kotlin
val commonMenuOptions = listOf(
    MenuOption(
        id = "inicio",
        label = "Inicio",
        icon = Icons.Default.Home,
        chatMessage = "Muéstrame un resumen de inicio",  // Personalizado por rol
        roles = listOf("Admin_plataforma", "Admin_academia", "Profesor_academia")
    ),
    MenuOption(
        id = "chat_libre",
        label = "Chat Libre",
        icon = Icons.Default.Chat,
        chatMessage = null,  // null = limpiar chat y dejar libre
        roles = listOf("Admin_plataforma", "Admin_academia", "Profesor_academia")
    ),
    MenuOption(
        id = "configuracion",
        label = "Configuración",
        icon = Icons.Default.Settings,
        chatMessage = "Muéstrame mi perfil y configuración",
        roles = listOf("Admin_plataforma", "Admin_academia", "Profesor_academia")
    ),
    MenuOption(
        id = "cerrar_sesion",
        label = "Cerrar sesión",
        icon = Icons.Default.Logout,
        chatMessage = null,  // null = acción especial (logout)
        roles = listOf("Admin_plataforma", "Admin_academia", "Profesor_academia")
    )
)
```

---

### 👑 Admin_plataforma - Opciones Exclusivas

```kotlin
val adminPlataformaOptions = listOf(
    MenuOption(
        id = "academias",
        label = "🏢 Academias",
        icon = Icons.Default.Business,
        chatMessage = "Muéstrame todas las academias de la plataforma",
        contextData = mapOf("resource" to "academias", "scope" to "global"),
        roles = listOf("Admin_plataforma")
    ),
    MenuOption(
        id = "usuarios_sistema",
        label = "👥 Usuarios del Sistema",
        icon = Icons.Default.People,
        chatMessage = "Muéstrame todos los usuarios del sistema",
        contextData = mapOf("resource" to "usuarios", "scope" to "global"),
        roles = listOf("Admin_plataforma")
    ),
    MenuOption(
        id = "estadisticas_globales",
        label = "📊 Estadísticas Globales",
        icon = Icons.Default.Analytics,
        chatMessage = "Muéstrame estadísticas globales de la plataforma",
        contextData = mapOf("resource" to "estadisticas", "scope" to "global"),
        roles = listOf("Admin_plataforma")
    )
)
```

---

### 🏢 Admin_academia - Opciones Exclusivas

```kotlin
val adminAcademiaOptions = listOf(
    MenuOption(
        id = "cursos",
        label = "📚 Cursos",
        icon = Icons.Default.School,
        chatMessage = "Muéstrame todos los cursos de mi academia",
        contextData = mapOf("resource" to "cursos", "scope" to "academia"),
        roles = listOf("Admin_academia")
    ),
    MenuOption(
        id = "alumnos",
        label = "👨‍🎓 Alumnos",
        icon = Icons.Default.Person,
        chatMessage = "Muéstrame todos los alumnos de mi academia",
        contextData = mapOf("resource" to "alumnos", "scope" to "academia"),
        roles = listOf("Admin_academia")
    ),
    MenuOption(
        id = "profesores",
        label = "👥 Profesores",
        icon = Icons.Default.Group,
        chatMessage = "Muéstrame los profesores de mi academia",
        contextData = mapOf("resource" to "profesores", "scope" to "academia"),
        roles = listOf("Admin_academia")
    ),
    MenuOption(
        id = "sesiones_clases",
        label = "🗓️ Clases/Sesiones",
        icon = Icons.Default.CalendarToday,
        chatMessage = "Muéstrame las clases de hoy",
        contextData = mapOf("resource" to "sesiones", "scope" to "academia", "filter" to "hoy"),
        roles = listOf("Admin_academia")
    ),
    MenuOption(
        id = "gestion_economica",
        label = "💰 Gestión Económica",
        icon = Icons.Default.AttachMoney,
        chatMessage = "Muéstrame los extractos y movimientos pendientes",
        contextData = mapOf("resource" to "extractos", "scope" to "academia"),
        roles = listOf("Admin_academia")
    ),
    MenuOption(
        id = "mi_academia",
        label = "🏫 Mi Academia",
        icon = Icons.Default.Business,
        chatMessage = "Muéstrame los datos de mi academia",
        contextData = mapOf("resource" to "academia", "scope" to "propia"),
        roles = listOf("Admin_academia")
    )
)
```

---

### 👨‍🏫 Profesor_academia - Opciones Exclusivas

```kotlin
val profesorAcademiaOptions = listOf(
    MenuOption(
        id = "mis_clases_hoy",
        label = "📅 Mis Clases Hoy",
        icon = Icons.Default.Today,
        chatMessage = "Muéstrame mis clases de hoy",
        contextData = mapOf(
            "resource" to "sesiones",
            "scope" to "profesor",
            "filter" to "hoy"
        ),
        roles = listOf("Profesor_academia")
    ),
    MenuOption(
        id = "mis_clases_semana",
        label = "🗓️ Mis Clases (Semana)",
        icon = Icons.Default.CalendarMonth,
        chatMessage = "Muéstrame mis clases de esta semana",
        contextData = mapOf(
            "resource" to "sesiones",
            "scope" to "profesor",
            "filter" to "semana"
        ),
        roles = listOf("Profesor_academia")
    ),
    MenuOption(
        id = "mis_cursos",
        label = "📚 Mis Cursos",
        icon = Icons.Default.School,
        chatMessage = "Muéstrame los cursos que imparto",
        contextData = mapOf(
            "resource" to "cursos",
            "scope" to "profesor"
        ),
        roles = listOf("Profesor_academia")
    ),
    MenuOption(
        id = "mis_anotaciones",
        label = "📝 Mis Anotaciones",
        icon = Icons.Default.Notes,
        chatMessage = "Muéstrame mis anotaciones recientes",
        contextData = mapOf(
            "resource" to "anotaciones",
            "scope" to "profesor",
            "order" to "recientes"
        ),
        roles = listOf("Profesor_academia")
    )
)
```

---

## 🔧 IMPLEMENTACIÓN EN CÓDIGO

### 1. Función para obtener opciones según rol:

```kotlin
fun getMenuOptionsForRole(role: String): List<MenuOption> {
    val options = mutableListOf<MenuOption>()
    
    // Opciones comunes para todos
    options.add(MenuOption("inicio", "Inicio", Icons.Default.Home, "Muéstrame un resumen de inicio", emptyList()))
    options.add(MenuOption("chat_libre", "Chat Libre", Icons.Default.Chat, null, emptyList()))
    
    // Opciones específicas por rol
    when (role.lowercase()) {
        "admin_plataforma" -> {
            options.add(MenuOption("academias", "🏢 Academias", Icons.Default.Business, 
                "Muéstrame todas las academias de la plataforma", emptyList()))
            options.add(MenuOption("usuarios_sistema", "👥 Usuarios", Icons.Default.People,
                "Muéstrame todos los usuarios del sistema", emptyList()))
            options.add(MenuOption("estadisticas_globales", "📊 Estadísticas", Icons.Default.Analytics,
                "Muéstrame estadísticas globales de la plataforma", emptyList()))
        }
        "admin_academia" -> {
            options.add(MenuOption("cursos", "📚 Cursos", Icons.Default.School,
                "Muéstrame todos los cursos de mi academia", emptyList()))
            options.add(MenuOption("alumnos", "👨‍🎓 Alumnos", Icons.Default.Person,
                "Muéstrame todos los alumnos de mi academia", emptyList()))
            options.add(MenuOption("profesores", "👥 Profesores", Icons.Default.Group,
                "Muéstrame los profesores de mi academia", emptyList()))
            options.add(MenuOption("sesiones", "🗓️ Clases", Icons.Default.CalendarToday,
                "Muéstrame las clases de hoy", emptyList()))
            options.add(MenuOption("gestion_economica", "💰 Facturación", Icons.Default.AttachMoney,
                "Muéstrame los extractos y movimientos pendientes", emptyList()))
            options.add(MenuOption("mi_academia", "🏫 Mi Academia", Icons.Default.Business,
                "Muéstrame los datos de mi academia", emptyList()))
        }
        "profesor_academia" -> {
            options.add(MenuOption("mis_clases_hoy", "📅 Mis Clases Hoy", Icons.Default.Today,
                "Muéstrame mis clases de hoy", emptyList()))
            options.add(MenuOption("mis_clases_semana", "🗓️ Mis Clases (Semana)", Icons.Default.CalendarMonth,
                "Muéstrame mis clases de esta semana", emptyList()))
            options.add(MenuOption("mis_cursos", "📚 Mis Cursos", Icons.Default.School,
                "Muéstrame los cursos que imparto", emptyList()))
            options.add(MenuOption("mis_anotaciones", "📝 Mis Anotaciones", Icons.Default.Notes,
                "Muéstrame mis anotaciones recientes", emptyList()))
        }
    }
    
    // Opciones finales comunes
    options.add(MenuOption("configuracion", "Configuración", Icons.Default.Settings,
        "Muéstrame mi perfil y configuración", emptyList()))
    options.add(MenuOption("cerrar_sesion", "Cerrar sesión", Icons.Default.Logout, null, emptyList()))
    
    return options
}
```

---

### 2. Handler de clicks del menú:

```kotlin
fun handleMenuClick(
    option: MenuOption,
    navController: NavController,
    drawerState: DrawerState,
    chatViewModel: ChatViewModel,
    sessionStore: SessionStore,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        when (option.id) {
            "cerrar_sesion" -> {
                // Lógica de logout
                drawerState.close()
                chatViewModel.reset()
                sessionStore.clear()
                navController.navigate("login") { 
                    popUpTo("login") { inclusive = true } 
                }
            }
            
            "chat_libre" -> {
                // Limpiar chat y dejar libre
                drawerState.close()
                chatViewModel.reset()
                navController.navigate("chat") {
                    launchSingleTop = true
                    restoreState = true
                }
            }
            
            else -> {
                // Todas las demás opciones: pre-cargar mensaje en chat
                drawerState.close()
                
                if (option.chatMessage != null) {
                    // Construir contexto
                    val context = buildMap {
                        put("currentScreen", option.id)
                        put("role", sessionStore.role.value)
                        option.contextData?.forEach { (k, v) -> put(k, v) }
                    }
                    
                    // Auto-enviar mensaje con contexto
                    chatViewModel.sendMessageWithContext(option.chatMessage, context)
                }
                
                // Asegurar que estamos en ChatScreen
                navController.navigate("chat") {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }
}
```

---

### 3. Modificación del ChatScreen para renderizar menú dinámico:

```kotlin
@Composable
fun ChatScreen(...) {
    // ... código actual ...
    
    val session = app.container.session
    val userRole by session.role.collectAsState(initial = "")
    
    // Obtener opciones del menú según rol
    val menuOptions = remember(userRole) {
        getMenuOptionsForRole(userRole)
    }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Header (igual que ahora)
                Column(/* ... */) { /* ... */ }
                
                Spacer(Modifier.height(8.dp))
                
                // Renderizar opciones dinámicas
                menuOptions.forEach { option ->
                    NavigationDrawerItem(
                        label = { Text(option.label) },
                        icon = { Icon(option.icon, contentDescription = null) },
                        selected = false,  // TODO: marcar seleccionado si estamos en ese contexto
                        onClick = {
                            handleMenuClick(
                                option = option,
                                navController = navController,
                                drawerState = drawerState,
                                chatViewModel = vm,
                                sessionStore = session,
                                coroutineScope = coroutineScope
                            )
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        // Contenido del chat (igual que ahora)
        Scaffold(...) { ... }
    }
}
```

---

## 🎯 RESUMEN DE CAMBIOS NECESARIOS

### Archivos a modificar:

1. **ChatScreen.kt**
   - ✅ Reemplazar lista hardcoded por `getMenuOptionsForRole()`
   - ✅ Implementar `handleMenuClick()` con lógica de pre-carga de mensajes
   - ✅ Pasar contexto al ViewModel cuando se auto-envía mensaje

2. **ChatViewModel.kt**
   - ✅ Añadir función `sendMessageWithContext(message: String, context: Map<String, Any?>)`
   - ✅ Mantener contexto actual para siguientes mensajes del mismo flujo

3. **MockChatRepository.kt** (nuevo archivo)
   - ✅ Detectar `currentScreen` del contexto
   - ✅ Generar respuestas mock según pantalla/rol
   - ✅ Incluir sugerencias contextuales

4. **AppContainer.kt**
   - ✅ Añadir flag `useMockData: Boolean`
   - ✅ Instanciar `MockChatRepository` cuando esté activado

---

## ✅ VENTAJAS DE ESTA ARQUITECTURA

1. ✅ **Una sola pantalla**: `ChatScreen` - simplicidad extrema
2. ✅ **Todo es chat guiado**: Experiencia consistente y natural
3. ✅ **Fácil de mockear**: Solo necesitas `MockChatRepository`
4. ✅ **Escalable**: Añadir nuevas funciones = añadir casos al mock
5. ✅ **Demo impactante**: Flujos completos sin backend
6. ✅ **Transición suave**: Cuando tengas backend, solo cambias el repositorio

---

## 📊 PRÓXIMOS PASOS

1. **FASE 1**: Implementar menú dinámico (1 día)
2. **FASE 2**: Crear `MockChatRepository` básico (1 día)
3. **FASE 3**: Implementar respuestas mock para cada opción del menú (2-3 días)
4. **FASE 4**: Añadir sugerencias y navegación profunda (2 días)
5. **FASE 5**: Pulir UX y latencia simulada (1 día)

**Total: 7-8 días para demo completa** 🚀
