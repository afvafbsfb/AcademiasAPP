# AcademiaAPP - Cliente Android

> **Aplicación móvil Android para gestión de academias mediante chat conversacional con IA**  
> Proyecto Final de Ciclo - FP DAM | Ángel Fernández Vidal | 2025

## 📱 Descripción

AcademiaAPP es el cliente Android nativo del sistema de gestión de academias basado en chat inteligente. Permite a administradores y profesores gestionar usuarios, cursos, tarifas y alumnos mediante lenguaje natural en español, potenciado por OpenAI GPT-4.

### Características Principales

- 🔐 **Autenticación segura** con JWT contra API REST Python (Flask)
- 💬 **Chat conversacional IA** integrado con backend mediador Java (Spring Boot)
- 🎨 **Interfaz moderna** construida con Jetpack Compose (100% Kotlin)
- 🏗️ **Arquitectura MVVM** limpia y escalable
- 🔄 **Gestión de estado** con StateFlow y ViewModels
- 💾 **Persistencia local** con DataStore (sesión cifrada)
- 🌐 **Multi-entorno** (dev, prod) mediante Build Variants
- 🎭 **Roles diferenciados**: Admin plataforma, Admin academia, Profesor

## 🏗️ Arquitectura

### Stack Tecnológico

| Componente | Tecnología | Versión |
|------------|------------|---------|
| **Lenguaje** | Kotlin | 1.9.0 |
| **UI Framework** | Jetpack Compose | 1.5.1 |
| **Arquitectura** | MVVM (Model-View-ViewModel) | - |
| **Inyección Dependencias** | Hilt | 2.48 |
| **Networking** | Retrofit + OkHttp | 2.9.0 |
| **Serialización JSON** | Kotlinx Serialization | 1.6.0 |
| **Persistencia** | DataStore (Preferences) | 1.0.0 |
| **Corrutinas** | Kotlin Coroutines | 1.7.3 |
| **Navigation** | Compose Navigation | 2.7.3 |
| **Min SDK** | Android 8.0 (API 26) | - |
| **Target SDK** | Android 14 (API 34) | - |

### Estructura del Proyecto

```
app/src/main/
├── java/com/workers/profesores/academiaapp/
│   ├── di/              # Módulos de inyección de dependencias (Hilt)
│   ├── data/
│   │   ├── remote/      # API clients (Retrofit interfaces)
│   │   ├── repository/  # Repositorios (capa de datos)
│   │   └── local/       # DataStore (sesión, preferencias)
│   ├── domain/
│   │   └── model/       # Modelos de dominio (DTOs)
│   ├── ui/
│   │   ├── login/       # Pantalla Login (ViewModel + Composables)
│   │   ├── chat/        # Pantalla Chat (ViewModel + Composables)
│   │   ├── components/  # Componentes UI reutilizables
│   │   └── theme/       # Material3 Theme customizado
│   └── MainActivity.kt  # Activity principal con Navigation
├── res/
│   ├── values/          # Strings, colores, dimensiones
│   └── drawable/        # Iconos y assets
└── AndroidManifest.xml
```

### Arquitectura MVVM Implementada

```
┌─────────────────────────────────────────────────────┐
│                     View (UI)                        │
│  ┌────────────────────────────────────────────────┐ │
│  │  @Composable LoginScreen()                     │ │
│  │  @Composable ChatScreen()                      │ │
│  └────────────────────────────────────────────────┘ │
└──────────────────┬──────────────────────────────────┘
                   │ observa StateFlow
                   │ dispara eventos
                   ▼
┌─────────────────────────────────────────────────────┐
│                  ViewModel                          │
│  ┌────────────────────────────────────────────────┐ │
│  │  LoginViewModel                                │ │
│  │  ChatViewModel                                 │ │
│  │  - Lógica de presentación                      │ │
│  │  - Gestión de estado (StateFlow)              │ │
│  │  - Orquestación de repositorios               │ │
│  └────────────────────────────────────────────────┘ │
└──────────────────┬──────────────────────────────────┘
                   │ llama a repositorios
                   ▼
┌─────────────────────────────────────────────────────┐
│                  Repository                         │
│  ┌────────────────────────────────────────────────┐ │
│  │  AuthRepository                                │ │
│  │  ChatRepository                                │ │
│  │  - Abstracción de fuentes de datos            │ │
│  │  - Coordinación Remote + Local                │ │
│  └────────────────────────────────────────────────┘ │
└──────────────┬──────────────────┬───────────────────┘
               │                  │
               ▼                  ▼
┌──────────────────────┐  ┌─────────────────────────┐
│   Remote (API)       │  │   Local (DataStore)     │
│  ┌────────────────┐  │  │  ┌───────────────────┐  │
│  │ Retrofit       │  │  │  │ SessionDataStore  │  │
│  │ OkHttp         │  │  │  │ Preferences       │  │
│  └────────────────┘  │  │  └───────────────────┘  │
└──────────────────────┘  └─────────────────────────┘
```

## 🚀 Instalación y Configuración

### Requisitos Previos

- **Android Studio** Hedgehog (2023.1.1) o superior
- **JDK** 17 o superior
- **SDK Android** 26-34 instalado
- **Emulador Android** o dispositivo físico con Android 8.0+
- **Backends ejecutándose**:
  - API Python Flask en `http://localhost:5000`
  - Backend Chat Java en `http://localhost:8080`

### Configuración de Build Variants

El proyecto tiene 2 flavors configurados:

#### Flavor `dev` (Desarrollo local)
```gradle
dev {
    applicationIdSuffix ".dev"
    versionNameSuffix "-dev"
    buildConfigField "String", "API_BASE_URL", "\"http://10.0.2.2:5000/\""
    buildConfigField "String", "CHAT_BASE_URL", "\"http://10.0.2.2:8080/\""
}
```
- **API URL:** `http://10.0.2.2:5000` (emulador → localhost:5000)
- **Chat URL:** `http://10.0.2.2:8080` (emulador → localhost:8080)

#### Flavor `prod` (Producción)
```gradle
prod {
    buildConfigField "String", "API_BASE_URL", "\"https://api.academias.com/\""
    buildConfigField "String", "CHAT_BASE_URL", "\"https://chat.academias.com/\""
}
```

### Pasos de Instalación

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/afvafbsfb/AcademiaAPP.git
   cd AcademiaAPP
   ```

2. **Abrir en Android Studio:**
   - File → Open → Seleccionar carpeta `AcademiaAPP`
   - Esperar sincronización de Gradle

3. **Seleccionar Build Variant:**
   - Build → Select Build Variant
   - Elegir: `devDebug` (desarrollo) o `prodRelease` (producción)

4. **Configurar backends (solo para `dev`):**
   - Asegurar que API Python Flask corre en `http://localhost:5000`
   - Asegurar que Backend Chat Java corre en `http://localhost:8080`
   - Verificar conectividad desde emulador (usar `10.0.2.2` para localhost)

5. **Ejecutar la aplicación:**
   - Run → Run 'app' (o `Shift + F10`)
   - Seleccionar emulador o dispositivo físico

### Usuarios de Prueba

Para probar la aplicación en entorno `dev`:

| Email | Contraseña | Rol | Academia ID |
|-------|-----------|-----|-------------|
| `admin@plataforma.com` | `admin123` | Admin_plataforma | - |
| `admin@academia1.com` | `admin123` | Admin_academia | 1 |
| `profesor@academia1.com` | `profesor123` | Profesor_academia | 1 |

## 📖 Documentación Técnica

### Documentación Disponible en GitHub Pages

- 🏗️ **[Arquitectura MVVM Completa](https://afvafbsfb.github.io/api-workers-profesores/ARQUITECTURA_ACADEMIAAPP_ANDROID.html)** - Diagramas, patrones y decisiones de diseño
- 🎨 **[Guía de Experiencia de Usuario (UX)](https://afvafbsfb.github.io/api-workers-profesores/UX_ACADEMIAAPP_ANDROID.html)** - Flujos, navegación y componentes UI
- 🔗 **[Documentación API REST](https://afvafbsfb.github.io/api-workers-profesores/API_SWAGGER_STANDALONE_TFG.html)** - Endpoints, contratos y ejemplos
- 📝 **[Memoria Completa del TFG](https://afvafbsfb.github.io/api-workers-profesores/MEMORIA_TFG_SISTEMA_CHAT_ACADEMIAS.html)** - Documentación técnica completa del sistema

### Documentación Local

- **`documentacion/ARQUITECTURA_ACADEMIAAPP_ANDROID.html`** - Arquitectura MVVM (copia local)
- **`documentacion/UX_ACADEMIAAPP_ANDROID.html`** - Guía UX (copia local)

## 🔧 Desarrollo

### Comandos Útiles

```bash
# Compilar el proyecto
./gradlew assembleDevDebug

# Ejecutar tests unitarios
./gradlew testDevDebugUnitTest

# Generar APK de desarrollo
./gradlew assembleDevDebug

# Generar APK de producción firmado
./gradlew assembleProdRelease

# Limpiar build
./gradlew clean
```

### Configuración de Red para Emulador

El emulador Android mapea `10.0.2.2` a `localhost` de la máquina host:

```kotlin
// AppModule.kt (Hilt)
@Provides
@Singleton
fun provideApiService(): ApiService {
    val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL) // http://10.0.2.2:5000/
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
    
    return retrofit.create(ApiService::class.java)
}
```

### Gestión de Sesión

La sesión se almacena cifrada en DataStore:

```kotlin
// SessionDataStore.kt
class SessionDataStore(private val dataStore: DataStore<Preferences>) {
    
    suspend fun saveSession(token: String, userId: Int, roles: List<String>) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = userId
            preferences[ROLES_KEY] = roles.joinToString(",")
        }
    }
    
    val sessionFlow: Flow<SessionData?> = dataStore.data.map { preferences ->
        val token = preferences[TOKEN_KEY]
        if (token != null) {
            SessionData(
                token = token,
                userId = preferences[USER_ID_KEY] ?: 0,
                roles = preferences[ROLES_KEY]?.split(",") ?: emptyList()
            )
        } else null
    }
}
```

## 🧪 Testing

### Tests Unitarios (ViewModel)

```kotlin
// ChatViewModelTest.kt
@Test
fun `sendMessage should update chatHistory on success`() = runTest {
    // Given
    val message = "Listar usuarios"
    coEvery { chatRepository.sendMessage(message) } returns Result.success(mockResponse)
    
    // When
    viewModel.sendMessage(message)
    
    // Then
    val state = viewModel.uiState.value
    assertEquals(2, state.chatHistory.size)
    assertEquals(message, state.chatHistory[0].text)
}
```

### Tests de Integración (Repository)

```kotlin
// ChatRepositoryTest.kt
@Test
fun `sendMessage should call API and return ChatResponse`() = runTest {
    // Given
    val mockApi = mockk<ChatApiService>()
    coEvery { mockApi.sendMessage(any()) } returns mockChatResponse
    
    // When
    val result = repository.sendMessage("test")
    
    // Then
    assertTrue(result.isSuccess)
    assertEquals("Respuesta del chat", result.getOrNull()?.text)
}
```

## 🔐 Seguridad

- ✅ **JWT** para autenticación (almacenado en DataStore cifrado)
- ✅ **HTTPS** en producción (certificado SSL)
- ✅ **Ofuscación** con ProGuard en release builds
- ✅ **No hardcoded secrets** (BuildConfig generado en tiempo de compilación)
- ✅ **Timeout de sesión** configurable (default: 24h)

## 🌍 Internacionalización

Actualmente en **español (España)**. Estructura preparada para i18n:

```
res/
├── values/              # Español (default)
│   └── strings.xml
└── values-en/           # Inglés (futuro)
    └── strings.xml
```

## 🐛 Troubleshooting

### Error: "Unable to resolve host 10.0.2.2"
**Solución:** Verificar que los backends estén ejecutándose en localhost.

### Error: "401 Unauthorized"
**Solución:** Token expirado. Cerrar sesión y volver a iniciar sesión.

### Error: "Connection refused"
**Solución:** Verificar Build Variant (debe ser `devDebug` para desarrollo local).

### Layouts rotos en Compose Preview
**Solución:** Build → Clean Project → Rebuild Project

## 📦 Dependencias Principales

```gradle
// Jetpack Compose
implementation "androidx.compose.ui:ui:1.5.1"
implementation "androidx.compose.material3:material3:1.1.1"
implementation "androidx.navigation:navigation-compose:2.7.3"

// Hilt (DI)
implementation "com.google.dagger:hilt-android:2.48"
kapt "com.google.dagger:hilt-compiler:2.48"

// Retrofit + OkHttp
implementation "com.squareup.retrofit2:retrofit:2.9.0"
implementation "com.squareup.okhttp3:okhttp:4.11.0"

// Kotlinx Serialization
implementation "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0"

// DataStore
implementation "androidx.datastore:datastore-preferences:1.0.0"

// Coroutines
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
```

## 👨‍💻 Autor

**Ángel Fernández Vidal**  
Proyecto Final de Ciclo - FP Desarrollo de Aplicaciones Multiplataforma  
Diciembre 2025

## 📄 Licencia

Este proyecto es parte de un Trabajo Final de Grado (TFG) y está disponible públicamente para fines educativos y de evaluación.

## 🔗 Repositorios Relacionados

- **API REST Python (Flask):** [api-workers-profesores](https://github.com/afvafbsfb/api-workers-profesores)
- **Backend Chat Java (Spring Boot):** [backend-chat-openai-worker-profesores](https://github.com/afvafbsfb/backend-chat-openai-worker-profesores)
- **Documentación GitHub Pages:** [https://afvafbsfb.github.io/api-workers-profesores/](https://afvafbsfb.github.io/api-workers-profesores/)

## 📞 Contacto

Para consultas sobre el proyecto:
- **Email:** angel.fernandez@academia.es
- **GitHub:** [@afvafbsfb](https://github.com/afvafbsfb)