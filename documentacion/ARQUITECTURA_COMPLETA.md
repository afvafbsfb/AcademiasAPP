# Arquitectura Completa - Sistema Academia Chat-Driven

## Índice
1. [Visión General](#1-visión-general)
2. [Diagrama de Arquitectura General (Simplificado)](#2-diagrama-de-arquitectura-general-simplificado)
3. [Diagrama de Seguridad JWT - Token Delegado](#3-diagrama-de-seguridad-jwt---token-delegado)
4. [Modelo de Datos - Base de Datos MySQL](#4-modelo-de-datos---base-de-datos-mysql)
5. [Contrato de Respuesta del Chat](#5-contrato-de-respuesta-del-chat)
6. [Tabla de Endpoints y Seguridad](#6-tabla-de-endpoints-y-seguridad)
7. [Flujo Completo: Caso de Uso Real](#7-flujo-completo-caso-de-uso-real)
8. [Ventajas de la Arquitectura](#8-ventajas-de-la-arquitectura)
9. [Tecnologías Utilizadas](#9-tecnologías-utilizadas)
10. [Testing y Calidad](#10-testing-y-calidad)
11. [Plan de Despliegue en Producción](#11-plan-de-despliegue-en-producción)

---

## 1. Visión General

El sistema **Academia Chat-Driven** es una plataforma educativa que utiliza inteligencia artificial conversacional como única interfaz de usuario. La arquitectura se basa en tres pilares fundamentales:

### Componentes Principales

**Cliente Móvil (Android)**
- Aplicación Kotlin con Jetpack Compose
- Única pantalla (ChatScreen) para toda la interacción
- Sistema de navegación mediante contexto conversacional
- Soporte para modo Mock (desarrollo) y Real (producción)

**Backend Chat (Orquestador)**
- Servidor Spring Boot (Java 21) que actúa como intermediario
- Gestión de autenticación JWT para usuarios
- Integración con OpenAI GPT-4 para procesamiento de lenguaje natural
- Orquestación de llamadas a herramientas (tool calling)
- Estructuración de respuestas según contrato Envelope
- **Reenvía el JWT del usuario a API Workers** (concepto de "token delegado")

**API Workers (Python/Flask)**
- API REST con lógica de negocio
- Autenticación mediante JWT (el mismo del usuario)
- Acceso directo a base de datos MySQL
- Endpoints para gestión académica completa
- Validación de permisos a nivel de negocio
- **Despliegue:** AWS Elastic Beanstalk

**Base de Datos (MySQL 8.0)**
- Modelo relacional normalizado
- Sistema de auditoría con timestamps
- Seguridad mediante prepared statements
- **Conexiones:**
  - **Desarrollo:** `mysql://user:pass@localhost:3307/api_workers`
  - **Producción:** AWS RDS MySQL 8.0 (connection string en variable de entorno)

### Concepto Clave: Token Delegado

**¿Qué es el Token Delegado?**

El "token delegado" es el **mismo JWT del usuario** que viaja sin modificación desde Android hasta MySQL, pasando por Backend Chat y API Workers. No es un token diferente, sino una **delegación de la identidad del usuario** a través de todas las capas del sistema.

**Flujo del Token Delegado:**
```
Usuario login → JWT generado (API Workers)
  ↓
Android guarda JWT
  ↓
Backend Chat recibe JWT (valida claims)
  ↓
Backend Chat REENVÍA el MISMO JWT a API Workers
  ↓
API Workers valida el MISMO JWT
  ↓
MySQL registra usuarioId del JWT en auditoría
```

**Ventajas del Token Delegado:**

1. **Trazabilidad Completa:** Cada acción en cada capa está asociada al mismo `usuarioId`
2. **Simplicidad:** Un único secret compartido entre Backend Chat y API Workers
3. **Multi-tenancy Automático:** El `academiaId` del JWT filtra datos en todas las capas
4. **Sin Conversión de Credenciales:** No hay transformación de tokens (menor superficie de ataque)
5. **Logs Correlacionables:** Los logs distribuidos se pueden correlacionar por `usuarioId`

**Contraste con API-Key (NO usado):**
- API-Key autentica el **servicio** (Backend Chat como cliente)
- JWT autentica al **usuario** (persona que usa la app)
- En nuestro sistema, ambos backends están bajo el mismo control → solo necesitamos JWT

---

## 2. Diagrama de Arquitectura General (Simplificado)

Vista de alto nivel con los 4 componentes principales:

```mermaid
graph LR
    subgraph "CLIENTE"
        A[Android App<br/>Kotlin + Compose]
    end

    subgraph "ORQUESTADOR"
        B[Backend Chat<br/>Spring Boot + Java 21<br/>+ OpenAI GPT-4]
    end

    subgraph "LÓGICA DE NEGOCIO"
        C[API Workers<br/>Python + Flask]
    end

    subgraph "DATOS"
        D[(MySQL 8.0<br/>AWS RDS)]
    end

    A -->|JWT usuario| B
    B -->|MISMO JWT<br/>token delegado| C
    C -->|Queries SQL| D

    style A fill:#e8f5e9
    style B fill:#fff3e0
    style C fill:#fce4ec
    style D fill:#f3e5f5
```

**Despliegue en Producción:**
- **Android:** Google Play Store
- **Backend Chat:** AWS Elastic Beanstalk (Single Instance + Let's Encrypt HTTPS)
- **API Workers:** AWS Elastic Beanstalk (Python/Flask + MySQL driver)
- **MySQL:** AWS RDS MySQL 8.0 (Multi-AZ para alta disponibilidad)

**Protocolos:**
- Android ↔ Backend Chat: HTTPS/TLS 1.3 (puerto 443)
- Backend Chat ↔ OpenAI: HTTPS (API oficial)
- Backend Chat ↔ API Workers: HTTPS/TLS 1.3
- API Workers ↔ MySQL: MySQL Protocol sobre TLS

---

## 3. Diagrama de Seguridad JWT - Token Delegado

### Arquitectura de Seguridad: JWT Único (Token Delegado)

**Principio clave:** Un único JWT viaja de punta a punta (Android → Backend Chat → API Workers)

```mermaid
sequenceDiagram
    participant U as Usuario
    participant A as Android App
    participant AC as AuthRepository
    participant API as API Workers /auth
    participant Chat as Backend Chat
    participant OpenAI as OpenAI GPT-4
    participant APIRes as API Workers /recursos
    participant DB as MySQL

    rect rgb(230, 245, 255)
        Note over U,DB: FASE 1: AUTENTICACIÓN Y LOGIN
    end
    
    U->>A: Ingresa email y password
    A->>AC: login(email, password)
    AC->>API: POST /auth/login
    Note right of API: Sin autenticación<br/>(endpoint público)
    API->>DB: SELECT * FROM Usuario WHERE email = ?
    DB-->>API: Usuario encontrado
    API->>API: Verifica hash de password (bcrypt)
    API->>API: Genera JWT (exp: 15 min)<br/>Claims: { usuarioId, academiaId, role }<br/>Genera Refresh Token (exp: 30 días)
    API->>DB: INSERT INTO RefreshToken
    API-->>AC: 200 OK<br/>{ jwt, refreshToken, user: { name, role, academiaId } }
    AC->>A: Almacena en SessionStore (DataStore)
    A-->>U: Redirige a ChatScreen

    rect rgb(255, 245, 230)
        Note over U,DB: FASE 2: CHAT CON TOOL CALLING
    end
    
    U->>A: Escribe: "Muéstrame mis clases de hoy"
    A->>Chat: POST /chat/send<br/>Headers: { Authorization: Bearer JWT_ABC123 }
    Chat->>Chat: Middleware valida JWT<br/>jwt.verify(token, SECRET_KEY)
    Chat->>Chat: Extrae claims del JWT:<br/>{ usuarioId: 12, academiaId: 5, role: "Profesor_academia" }
    Chat->>OpenAI: POST /v1/chat/completions<br/>Body: { messages, tools: served-openapi.json }
    Note right of OpenAI: Primer ciclo:<br/>Analiza intención del usuario
    OpenAI-->>Chat: ToolCall Required:<br/>{ name: "GET /sesiones", params: { fecha: "hoy", profesor_id: 12 } }
    
    rect rgb(255, 240, 245)
        Note over Chat,DB: Backend Chat REENVÍA EL MISMO JWT
    end
    
    Chat->>APIRes: GET /sesiones?fecha=2025-10-23&profesor_id=12<br/>Headers: { Authorization: Bearer JWT_ABC123 }
    Note right of APIRes: EL MISMO JWT del usuario<br/>NO hay API-Key
    APIRes->>APIRes: Middleware valida JWT<br/>jwt.verify(token, SECRET_KEY)
    APIRes->>APIRes: Extrae claims: { usuarioId: 12, academiaId: 5 }
    APIRes->>APIRes: Valida permisos según role
    APIRes->>DB: SELECT s.*, h.*, c.* FROM Sesion s<br/>JOIN HorarioCurso h ON s.horario_curso_id = h.id<br/>JOIN Curso c ON h.curso_id = c.id<br/>WHERE DATE(s.timestamp_alta) = ?<br/>AND s.curso_profesor_id IN<br/>(SELECT id FROM Curso_Profesores WHERE usuario_id = ?)<br/>AND c.academia_id = 5
    Note right of DB: Filtrado automático<br/>por academiaId del JWT
    DB-->>APIRes: Resultados (3 sesiones)
    APIRes-->>Chat: 200 OK<br/>{ data: [...sesiones] }
    
    Chat->>OpenAI: POST /v1/chat/completions<br/>Body: { messages: [...previous, tool_result] }
    Note right of OpenAI: Segundo ciclo:<br/>Genera respuesta natural
    OpenAI-->>Chat: { message: "Tus clases de hoy...", function_call: null }
    Chat->>Chat: Estructura respuesta como Envelope:<br/>{ message, data: { type, items }, uiSuggestions, pagination }
    Chat-->>A: 200 OK - Envelope GenericItem
    A->>A: Actualiza UI con StateFlow
    A-->>U: Muestra tarjetas de clases + sugerencias

    rect rgb(240, 255, 240)
        Note over U,DB: FASE 3: RENOVACIÓN DE TOKEN (JWT expirado)
    end
    
    U->>A: Envía nuevo mensaje (JWT expirado)
    A->>Chat: POST /chat/send<br/>Headers: { Authorization: Bearer EXPIRED_JWT }
    Chat->>Chat: Middleware detecta token expirado
    Chat-->>A: 401 Unauthorized<br/>{ error: "Token expired" }
    A->>AC: AuthRepository.refreshToken()
    AC->>API: POST /auth/refresh<br/>Body: { refreshToken }
    API->>DB: SELECT * FROM RefreshToken WHERE token_hash = ?<br/>AND expires_at > NOW() AND revoked_at IS NULL
    DB-->>API: RefreshToken válido
    API->>API: Genera nuevo JWT (exp: 15 min)<br/>Genera nuevo Refresh Token (rotación)
    API->>DB: UPDATE RefreshToken SET revoked_at = NOW(), replaced_by_id = ?<br/>INSERT INTO RefreshToken (nuevo token)
    API-->>AC: 200 OK<br/>{ jwt, refreshToken }
    AC->>A: Actualiza SessionStore
    A->>Chat: Reintenta POST /chat/send con nuevo JWT
    Chat-->>A: 200 OK - Respuesta exitosa
```

### Por Qué NO Usamos API-Key

**Ventajas de JWT Único:**

1. **Simplicidad:** Una sola clave en todo el sistema
2. **Trazabilidad:** Sabemos qué usuario hizo cada acción en cada capa
3. **Multi-tenancy automático:** El JWT lleva `academiaId`, filtra datos automáticamente
4. **Menos superficie de ataque:** No hay que rotar API-Keys
5. **Control total:** Ambos backends están bajo nuestro control

**Cuándo SÍ usar API-Key:**
- Cuando terceros necesitan acceder a tu API sin usuarios (ej: Moodle, Google Classroom)
- Cuando el servicio que llama no tiene acceso al JWT del usuario
- Cuando quieres autenticar el **servicio** (no el usuario)

**En nuestro caso:**
- Backend Chat y API Workers están bajo el mismo control
- Backend Chat siempre tiene el JWT del usuario
- Más simple = más seguro

### Capas de Seguridad Implementadas

**Capa 1: Transporte**
- HTTPS obligatorio en todas las comunicaciones
- TLS 1.3 para cifrado end-to-end
- Certificados SSL/TLS válidos

**Capa 2: Autenticación de Usuario (JWT en 3 puntos)**
- **Android → Backend Chat:** JWT con firma HMAC-SHA256
- **Backend Chat → API Workers:** EL MISMO JWT reenviado
- **API Workers → MySQL:** Extrae claims del JWT para filtrar datos

Claims del JWT:
```json
{
  "usuarioId": 12,
  "academiaId": 5,
  "role": "Profesor_academia",
  "email": "maria@academia.com",
  "iat": 1698073000,
  "exp": 1698073900
}
```

**Capa 3: Autorización a Nivel de Negocio**
- Validación de permisos según rol (Admin_plataforma, Admin_academia, Profesor_academia)
- Filtrado de datos por academiaId (multi-tenancy)
- Solo acceso a recursos propios (profesor solo ve sus clases)

**Capa 4: Base de Datos**
- Prepared Statements para prevenir SQL Injection
- Usuario de BD con permisos limitados (no DROP, no ALTER)
- Conexiones cifradas (SSL/TLS)
- Auditoría con timestamps (fecha_alta, fecha_ultima_modificacion)

**Capa 5: Rate Limiting y Anti-Abuse**
- Límite de 100 requests/minuto por usuario en Backend Chat
- Límite de 1000 requests/minuto por IP en API Workers
- Bloqueo temporal tras 5 intentos fallidos de login (locked_until en Usuario)
- Captcha tras 3 intentos fallidos (futuro)

---

## 4. Modelo de Datos - Base de Datos MySQL

### Cadenas de Conexión

**Desarrollo (Local):**
```
mysql://root:password@localhost:3307/api_workers
Charset: utf8mb4
Collation: utf8mb4_unicode_ci
```

**Producción (AWS RDS):**
```
mysql://admin:${RDS_PASSWORD}@academia-db.abc123.us-east-1.rds.amazonaws.com:3306/api_workers
SSL: Required (TLS 1.3)
Multi-AZ: Enabled
Backups: Daily automated (retention 7 days)
```

### Tablas de la Base de Datos

La base de datos contiene **26 tablas** organizadas en módulos funcionales. Fuente: `create_database.sql`

#### Módulo Core (Academia y Configuración)

| Tabla | Descripción | Campos Clave |
|-------|-------------|--------------|
| `Academia` | Entidades/empresas del sistema (multi-tenancy) | id, nombre, fecha_alta, fecha_baja |
| `Tarifa` | Planes de precios por academia | id, academia_id, precio_base, descripcion |
| `Descuentos_tarifa` | Descuentos aplicables (familiar, prorrateado) | id, tarifa_id, motivo_descuento, tipo_descuento, porcentaje |
| `Aula` | Espacios físicos para impartir clases | id, academia_id, nombre, capacidad_maxima |

#### Módulo de Usuarios y Seguridad

| Tabla | Descripción | Campos Clave |
|-------|-------------|--------------|
| `Rol_Usuario` | Roles del sistema (3 roles) | id, nombre (Admin_plataforma, Admin_academia, Profesor_academia) |
| `Usuario` | Usuarios del sistema (profesores, admins) | id, academia_id, email, password (Argon2), rol_id, estado, token_version |
| `UserLoginLog` | Historial de logins/logouts | id, usuario_id, login_at, logout_at, success, ip, user_agent |
| `RefreshToken` | Tokens de refresco (rotación) | id, usuario_id, token_hash, expires_at, revoked_at, replaced_by_id |
| `PasswordResetToken` | Tokens para recuperación de contraseña | id, usuario_id, token_hash, expires_at, used_at |

#### Módulo Académico (Cursos y Horarios)

| Tabla | Descripción | Campos Clave |
|-------|-------------|--------------|
| `Curso` | Cursos académicos | id, academia_id, nombre, anio_academico, fecha_inicio, fecha_fin, capacidad_maxima, tarifa_id, tipo_alumno, estado |
| `HorarioCurso` | Horarios semanales de cada curso | id, curso_id, aula_id, dia_semana, hora_inicio, hora_fin |
| `Curso_Profesores` | Asignación de profesores a cursos | id, curso_id, usuario_id, fecha_alta, fecha_baja |
| `Sesion` | Clases concretas (instancias de horarios) | id, horario_curso_id, aula_id, curso_profesor_id, timestamp_alta, timestamp_baja, notas_sesion, notas_materia |

#### Módulo de Alumnos

| Tabla | Descripción | Campos Clave |
|-------|-------------|--------------|
| `Alumno` | Datos personales de alumnos | id, academia_id, nombre, email, dni, telefono, fecha_nacimiento, direccion, nombre_tutor, telefono_tutor |
| `Inscripcion` | Matrículas de alumnos en cursos | id, alumno_id, curso_id, tarifa_id, fecha_inicio, fecha_fin, motivo_baja |
| `familias_alumnos` | Relaciones familiares entre alumnos | id_familia_alumno, id_alumno1, id_alumno2, relacion_familiar |
| `AnotacionesAlumnoSesion` | Registro de asistencia, evaluaciones y comportamiento | id, sesion_id, inscripcion_id, alumno_id, tipo_anotacion (Ausencia, Evaluacion, Comportamiento), texto |

#### Módulo Financiero

| Tabla | Descripción | Campos Clave |
|-------|-------------|--------------|
| `Extractos` | Estados de cuenta mensuales por alumno | id, inscripcion_id, numero_extracto, fecha_ini_periodo, fecha_fin_periodo, importe_cuota, total_descuentos, total_importe_a_cobrar, estado_liquidacion_extracto |
| `Movimientos_Extracto` | Ingresos y anulaciones de pago | id, extracto_id, fecha_movimiento, tipo_movimiento (Ingreso, Anulacion_Ingreso), importe, metodo_pago, estado_liquidacion_movimiento |

#### Módulo de Automatización (Futuro)

| Tabla | Descripción | Campos Clave |
|-------|-------------|--------------|
| `TrabajadorVirtual` | Agentes IA para automatización | id, academia_id, nombre, foto, descripcion, apikey, es_administrativo_virtual |

### Diagrama ERD Simplificado

```
Academia (1) ──┬──> (*) Curso
               ├──> (*) Aula
               ├──> (*) Usuario
               ├──> (*) Alumno
               └──> (*) Tarifa

Usuario (1) ──> (*) Curso_Profesores ──> (1) Curso

Curso (1) ──┬──> (*) HorarioCurso
            └──> (*) Inscripcion

HorarioCurso (1) ──> (*) Sesion

Inscripcion (1) ──> (*) AnotacionesAlumnoSesion
                └──> (*) Extractos

Extractos (1) ──> (*) Movimientos_Extracto
```

---

## 5. Contrato de Respuesta del Chat

### Estructura del Envelope

Todas las respuestas del Backend Chat siguen un contrato estandarizado llamado **Envelope**, que permite renderizar cualquier tipo de información de manera consistente en la UI.

```mermaid
classDiagram
    class Envelope {
        +String message
        +Data data
        +List~Suggestion~ uiSuggestions
        +Pagination pagination
        +Metadata metadata
    }

    class Data {
        +String type
        +List~GenericItem~ items
        +Map~String,Any~ summary
    }

    class GenericItem {
        +String id
        +String type
        +String titulo
        +Map~String,Any~ campos
        +List~Action~ actions
    }

    class Suggestion {
        +String id
        +String label
        +String message
        +SuggestionType type
        +Map~String,Any~ context
    }

    class Action {
        +String id
        +String label
        +String endpoint
        +String method
        +Map~String,Any~ params
    }

    class Pagination {
        +Int currentPage
        +Int totalPages
        +Int totalItems
        +Int itemsPerPage
        +String nextPageToken
        +Boolean hasNextPage
        +Boolean hasPreviousPage
    }

    class Metadata {
        +String conversationId
        +String timestamp
        +String model
        +Int tokensUsed
        +Float responseTime
        +String version
    }

    Envelope "1" --> "1" Data : contains
    Envelope "1" --> "*" Suggestion : contains
    Envelope "1" --> "0..1" Pagination : contains
    Envelope "1" --> "1" Metadata : contains
    Data "1" --> "*" GenericItem : contains
    GenericItem "1" --> "*" Action : contains
```

### Tipos de Datos Soportados

| Tipo | Descripción | Ejemplo de Uso |
|------|-------------|----------------|
| `sesiones_dia` | Lista de sesiones/clases de un día específico | "Mis clases de hoy" |
| `sesiones_semana` | Vista semanal de sesiones | "Ver toda la semana" |
| `alumnos` | Lista de alumnos | "Alumnos del curso Matemáticas 1º ESO" |
| `alumnos_lista` | Lista de asistencia con checkboxes | "Ver alumnos de la clase de 11:00" |
| `cursos` | Lista de cursos | "Mis cursos activos" |
| `anotaciones` | Lista de anotaciones | "Últimas anotaciones" |
| `detalle_sesion` | Detalle completo de una sesión | "Ver detalle de la clase de las 08:00" |
| `detalle_alumno` | Ficha completa de un alumno | "Ver expediente de Juan Pérez" |
| `extractos` | Estados de cuenta | "Extractos pendientes de pago" |
| `usuarios` | Lista de usuarios del sistema | "Listar usuarios" |
| `academias` | Lista de academias (solo Admin_plataforma) | "Ver todas las academias" |
| `generic_table` | Tabla genérica (fallback) | Cualquier otro dato tabular |

### Ejemplo de Respuesta: Lista de Alumnos

```json
{
  "message": "Aquí están los alumnos del curso Matemáticas 1º ESO (25 alumnos inscritos).",
  "data": {
    "type": "alumnos",
    "items": [
      {
        "id": "12",
        "type": "alumno",
        "titulo": "Juan Pérez García",
        "campos": {
          "email": "juan.perez@estudiante.com",
          "telefono": "612345678",
          "fecha_nacimiento": "2010-05-15",
          "dni": "12345678A",
          "estado_inscripcion": "Activo",
          "fecha_inicio_curso": "2024-09-01",
          "tutor": "María García (Madre)",
          "telefono_tutor": "687654321"
        },
        "actions": [
          {
            "id": "ver_detalle",
            "label": "Ver expediente completo",
            "endpoint": "/alumnos/12",
            "method": "GET",
            "params": {}
          },
          {
            "id": "ver_anotaciones",
            "label": "Ver anotaciones",
            "endpoint": "/anotaciones?alumno_id=12",
            "method": "GET",
            "params": {}
          },
          {
            "id": "ver_extractos",
            "label": "Ver pagos",
            "endpoint": "/extractos?alumno_id=12",
            "method": "GET",
            "params": {}
          }
        ]
      },
      {
        "id": "13",
        "type": "alumno",
        "titulo": "Ana López Martínez",
        "campos": {
          "email": "ana.lopez@estudiante.com",
          "telefono": "623456789",
          "fecha_nacimiento": "2010-08-22",
          "dni": "23456789B",
          "estado_inscripcion": "Activo",
          "fecha_inicio_curso": "2024-09-01",
          "tutor": "Pedro López (Padre)",
          "telefono_tutor": "698765432"
        },
        "actions": [
          {
            "id": "ver_detalle",
            "label": "Ver expediente completo",
            "endpoint": "/alumnos/13",
            "method": "GET",
            "params": {}
          }
        ]
      }
    ],
    "summary": {
      "total_alumnos": 25,
      "activos": 23,
      "baja": 2
    }
  },
  "uiSuggestions": [
    {
      "id": "filtrar_activos",
      "label": "Solo alumnos activos",
      "message": "Muéstrame solo los alumnos activos",
      "type": "filter",
      "context": {
        "filter": "estado=Activo"
      }
    },
    {
      "id": "buscar_alumno",
      "label": "Buscar alumno",
      "message": "Buscar alumno por nombre",
      "type": "search",
      "context": {}
    }
  ],
  "pagination": null,
  "metadata": {
    "conversationId": "conv_20251024_103045_xyz789",
    "timestamp": "2025-10-24T10:30:45.234Z",
    "model": "gpt-4o-mini",
    "tokensUsed": 1523,
    "responseTime": 2.15,
    "version": "1.0.0"
  }
}
```

### Ejemplo de Respuesta Completa: Sesiones del Día

```json
{
  "message": "Tus clases de hoy - Jueves 23 de octubre de 2025\n\nTienes 3 clase(s) programada(s)",
  "data": {
    "type": "sesiones_dia",
    "items": [
      {
        "id": "5",
        "type": "sesion",
        "titulo": "Matemáticas 1º ESO",
        "campos": {
          "horario": "08:00 - 10:00",
          "aula": "Aula 3",
          "aula_id": 3,
          "estado": "completada",
          "profesor": "María García López",
          "descripcion_estado": "Completada (08:02 - 10:05) | Lista pasada: 23/25 alumnos",
          "alumnos": 25,
          "alumnos_asistieron": 23,
          "curso_id": 4,
          "horario_curso_id": 5
        },
        "actions": [
          {
            "id": "ver_alumnos",
            "label": "Ver alumnos",
            "endpoint": "/sesiones/5/alumnos",
            "method": "GET",
            "params": {}
          },
          {
            "id": "ver_anotaciones",
            "label": "Ver anotaciones",
            "endpoint": "/sesiones/5/anotaciones",
            "method": "GET",
            "params": {}
          }
        ]
      },
      {
        "id": "6",
        "type": "sesion",
        "titulo": "Física 2º ESO",
        "campos": {
          "horario": "11:00 - 13:00",
          "aula": "Aula 5",
          "aula_id": 5,
          "estado": "en_curso",
          "profesor": "María García López",
          "descripcion_estado": "Iniciada a las 11:02 | Lista pasada: 18/20 alumnos",
          "alumnos": 20,
          "alumnos_asistieron": 18,
          "curso_id": 5,
          "horario_curso_id": 6
        },
        "actions": [
          {
            "id": "ver_alumnos",
            "label": "Ver alumnos",
            "endpoint": "/sesiones/6/alumnos",
            "method": "GET",
            "params": {}
          },
          {
            "id": "ver_anotaciones",
            "label": "Ver anotaciones",
            "endpoint": "/sesiones/6/anotaciones",
            "method": "GET",
            "params": {}
          },
          {
            "id": "finalizar_sesion",
            "label": "Finalizar sesión",
            "endpoint": "/sesiones/6/finalizar",
            "method": "POST",
            "params": {}
          }
        ]
      },
      {
        "id": "7",
        "type": "sesion",
        "titulo": "Matemáticas 1º ESO",
        "campos": {
          "horario": "21:00 - 23:00",
          "aula": "Aula 3",
          "aula_id": 3,
          "estado": "programada",
          "profesor": "María García López",
          "descripcion_estado": "No iniciada",
          "alumnos": 25,
          "alumnos_asistieron": null,
          "curso_id": 4,
          "horario_curso_id": 11
        },
        "actions": []
      }
    ],
    "summary": {
      "total_clases": 3,
      "completadas": 1,
      "en_curso": 1,
      "programadas": 1,
      "total_alumnos": 70,
      "total_asistencias": 41
    }
  },
  "uiSuggestions": [
    {
      "id": "ver_ayer",
      "label": "Ver clases de ayer",
      "message": "Muéstrame mis clases de ayer",
      "type": "generic",
      "context": {
        "fecha": "2025-10-22",
        "screen": "sesiones",
        "filter": "ayer"
      }
    },
    {
      "id": "ver_manana",
      "label": "Ver clases de mañana",
      "message": "Muéstrame mis clases de mañana",
      "type": "generic",
      "context": {
        "fecha": "2025-10-24",
        "screen": "sesiones",
        "filter": "manana"
      }
    },
    {
      "id": "ver_semana",
      "label": "Ver toda la semana",
      "message": "Muéstrame todas mis clases de la semana",
      "type": "generic",
      "context": {
        "screen": "sesiones",
        "filter": "semana"
      }
    }
  ],
  "pagination": null,
  "metadata": {
    "conversationId": "conv_20251023_091530_abc123",
    "timestamp": "2025-10-23T09:15:30.125Z",
    "model": "gpt-4-turbo",
    "tokensUsed": 1847,
    "responseTime": 2.34,
    "version": "1.0.0"
  }
}
```

### Ventajas del Contrato Envelope

**Consistencia**
- Todas las respuestas siguen la misma estructura
- UI puede renderizar cualquier tipo de dato sin cambios

**Flexibilidad**
- Nuevos tipos de datos solo requieren agregar un nuevo `type`
- No rompe compatibilidad con versiones anteriores

**Riqueza de Metadata**
- Información de debugging (tokensUsed, responseTime)
- Trazabilidad (conversationId, timestamp)
- Versionado para deprecación de campos

**Navegación Intuitiva**
- `uiSuggestions` guía al usuario a la siguiente acción
- Context map permite navegación profunda sin perder estado

**Paginación Automática**
- El backend decide si paginar según cantidad de items
- UI renderiza botones "Ver más" automáticamente

---

## 6. Tabla de Endpoints y Seguridad

### API Workers - Endpoints de Autenticación

| Endpoint | Método | Autenticación | Request Body | Response | Descripción |
|----------|--------|---------------|--------------|----------|-------------|
| `/auth/login` | POST | Pública | `{ email, password }` | `{ jwt, refreshToken, user }` | Login inicial del usuario |
| `/auth/refresh` | POST | RefreshToken | `{ refreshToken }` | `{ jwt, refreshToken }` | Renovación de JWT expirado (rotación automática) |
| `/auth/logout` | POST | JWT | `{}` | `{ success: true }` | Invalidar sesión (revoca refresh token) |
| `/auth/verify` | GET | JWT | - | `{ valid: true, user }` | Verificar si JWT es válido |

### API Workers - Endpoints de Recursos (Completo)

| Endpoint | Método | Autenticación | Permisos | Descripción |
|----------|--------|---------------|----------|-------------|
| **Alumnos** |
| `/alumnos` | GET | JWT | Admin_plataforma, Admin_academia, Profesor_academia | Listar alumnos (filtros: curso_id, estado, búsqueda) |
| `/alumnos/:id` | GET | JWT | Admin_plataforma, Admin_academia, Profesor_academia | Detalle completo de un alumno |
| `/alumnos` | POST | JWT | Admin_plataforma, Admin_academia | Alta de nuevo alumno |
| `/alumnos/:id` | PUT | JWT | Admin_plataforma, Admin_academia | Modificación de datos de alumno |
| `/alumnos/:id/baja` | POST | JWT | Admin_plataforma, Admin_academia | Baja lógica de alumno |
| **Cursos** |
| `/cursos` | GET | JWT | Admin_plataforma, Admin_academia, Profesor_academia | Listar cursos (filtros: academia_id, año_academico) |
| `/cursos/:id` | GET | JWT | Admin_plataforma, Admin_academia, Profesor_academia | Detalle de un curso |
| `/cursos/:id/alumnos` | GET | JWT | Admin_plataforma, Admin_academia, Profesor_academia | Alumnos inscritos en un curso |
| `/cursos` | POST | JWT | Admin_plataforma, Admin_academia | Crear nuevo curso |
| `/cursos/:id` | PUT | JWT | Admin_plataforma, Admin_academia | Modificar curso existente |
| **Sesiones** |
| `/sesiones` | GET | JWT | Admin_plataforma, Admin_academia, Profesor_academia | Listar sesiones (filtros: fecha, profesor_id, estado) |
| `/sesiones/:id` | GET | JWT | Admin_plataforma, Admin_academia, Profesor_academia | Detalle de una sesión |
| `/sesiones/iniciar` | POST | JWT | Admin_plataforma, Admin_academia, Profesor_academia | Crear nueva sesión (abrir clase) |
| `/sesiones/:id/finalizar` | POST | JWT | Admin_plataforma, Admin_academia, Profesor_academia | Cerrar sesión (marca timestamp_baja) |
| `/sesiones/:id/alumnos` | GET | JWT | Admin_plataforma, Admin_academia, Profesor_academia | Lista de asistencia de una sesión |
| **Anotaciones** |
| `/anotaciones` | GET | JWT | Admin_plataforma, Admin_academia, Profesor_academia | Listar anotaciones (filtros: alumno_id, sesion_id, tipo) |
| `/anotaciones` | POST | JWT | Admin_plataforma, Admin_academia, Profesor_academia | Crear nueva anotación (asistencia, evaluación, comportamiento) |
| `/anotaciones/:id` | PUT | JWT | Admin_plataforma, Admin_academia, Profesor_academia | Modificar anotación existente |
| `/anotaciones/:id` | DELETE | JWT | Admin_plataforma, Admin_academia | Eliminar anotación (baja lógica) |
| **Profesores** |
| `/profesores` | GET | JWT | Admin_plataforma, Admin_academia | Listar profesores de la academia |
| `/profesores/:id` | GET | JWT | Admin_plataforma, Admin_academia | Detalle de un profesor |
| `/profesores/:id/cursos` | GET | JWT | Admin_plataforma, Admin_academia, Profesor_academia | Cursos asignados a un profesor |
| **Horarios** |
| `/horarios` | GET | JWT | Admin_plataforma, Admin_academia, Profesor_academia | Obtener horarios de un curso |
| `/horarios` | POST | JWT | Admin_plataforma, Admin_academia | Crear nuevo horario |
| `/horarios/:id` | PUT | JWT | Admin_plataforma, Admin_academia | Modificar horario existente |
| **Usuarios** |
| `/usuarios` | GET | JWT | Admin_plataforma, Admin_academia | Listar usuarios (filtros: academia_id, rol, estado) |
| `/usuarios/:id` | GET | JWT | Admin_plataforma, Admin_academia | Detalle de un usuario |
| `/usuarios` | POST | JWT | Admin_plataforma, Admin_academia | Crear nuevo usuario |
| `/usuarios/:id` | PUT | JWT | Admin_plataforma, Admin_academia | Modificar usuario existente |
| `/usuarios/:id/bloquear` | POST | JWT | Admin_plataforma, Admin_academia | Bloquear usuario temporalmente |
| **Academias** |
| `/academias` | GET | JWT | Admin_plataforma | Listar todas las academias |
| `/academias/:id` | GET | JWT | Admin_plataforma, Admin_academia | Detalle de una academia |
| `/academias` | POST | JWT | Admin_plataforma | Crear nueva academia |
| `/academias/:id` | PUT | JWT | Admin_plataforma | Modificar academia existente |
| **Extractos y Pagos** |
| `/extractos` | GET | JWT | Admin_plataforma, Admin_academia | Listar extractos (filtros: alumno_id, estado) |
| `/extractos/:id` | GET | JWT | Admin_plataforma, Admin_academia | Detalle de extracto con movimientos |
| `/movimientos` | POST | JWT | Admin_plataforma, Admin_academia | Registrar ingreso de pago |
| `/movimientos/:id/anular` | POST | JWT | Admin_plataforma, Admin_academia | Anular movimiento existente |

### Backend Chat - Endpoints

| Endpoint | Método | Autenticación | Request Body | Response | Descripción |
|----------|--------|---------------|--------------|----------|-------------|
| `/chat` | POST | JWT | `{ messages, context }` | `Envelope<GenericItem>` | Enviar mensaje al chat (modo estándar) |
| `/chat/stream` | POST | JWT | `{ messages, context }` | SSE Stream | **Chat en tiempo real con Server-Sent Events (SSE)**. Devuelve respuesta incremental palabra por palabra para mejor UX. Endpoint experimental. |
| `/chat/history` | GET | JWT | - | `{ messages: [...] }` | Historial de conversación (futuro) |
| `/chat/context` | POST | JWT | `{ contextUpdate }` | `{ success: true }` | Actualizar contexto conversacional (futuro) |

**Nota sobre /chat/stream:**
- Utiliza Server-Sent Events (SSE) para streaming
- La respuesta se envía token por token (palabra por palabra) conforme OpenAI la genera
- Mejor experiencia de usuario (similar a ChatGPT web)
- Requiere cliente compatible con EventSource API
- Android: usar OkHttp SSE o biblioteca específica

### Matriz de Permisos por Rol

Basado en la lógica de negocio de API Workers:

| Recurso / Acción | Admin_plataforma | Admin_academia | Profesor_academia |
|------------------|------------------|----------------|-------------------|
| **Academias** |
| Ver todas las academias | ✅ Sí | ❌ No (solo la propia) | ❌ No |
| Crear/modificar academia | ✅ Sí | ❌ No | ❌ No |
| Ver datos de su academia | ✅ Sí | ✅ Sí | ✅ Sí |
| **Usuarios** |
| Ver todos los usuarios | ✅ Sí (todas las academias) | ✅ Sí (solo su academia) | ❌ No |
| Crear usuarios | ✅ Sí | ✅ Sí (solo en su academia) | ❌ No |
| Modificar/bloquear usuarios | ✅ Sí | ✅ Sí (solo de su academia) | ❌ No |
| **Alumnos** |
| Ver alumnos | ✅ Sí (todas las academias) | ✅ Sí (solo su academia) | ✅ Sí (solo de sus cursos) |
| Alta de alumno | ✅ Sí | ✅ Sí | ❌ No |
| Modificar alumno | ✅ Sí | ✅ Sí | ❌ No |
| Baja de alumno | ✅ Sí | ✅ Sí | ❌ No |
| **Cursos** |
| Ver cursos | ✅ Sí (todas las academias) | ✅ Sí (solo su academia) | ✅ Sí (solo los asignados) |
| Crear curso | ✅ Sí | ✅ Sí | ❌ No |
| Modificar curso | ✅ Sí | ✅ Sí | ❌ No |
| Asignar profesor a curso | ✅ Sí | ✅ Sí | ❌ No |
| **Sesiones (Clases)** |
| Ver sesiones | ✅ Sí (todas) | ✅ Sí (de su academia) | ✅ Sí (solo las propias) |
| Iniciar sesión | ✅ Sí | ✅ Sí | ✅ Sí |
| Finalizar sesión | ✅ Sí | ✅ Sí | ✅ Sí |
| Ver lista de asistencia | ✅ Sí | ✅ Sí | ✅ Sí (solo sus sesiones) |
| **Anotaciones** |
| Ver anotaciones | ✅ Sí (todas) | ✅ Sí (de su academia) | ✅ Sí (de sus cursos) |
| Crear anotaciones | ✅ Sí | ✅ Sí | ✅ Sí (solo en sus sesiones) |
| Modificar anotaciones | ✅ Sí | ✅ Sí | ✅ Sí (solo las propias) |
| Eliminar anotaciones | ✅ Sí | ✅ Sí | ❌ No |
| **Extractos y Pagos** |
| Ver extractos | ✅ Sí (todos) | ✅ Sí (de su academia) | ❌ No |
| Registrar pagos | ✅ Sí | ✅ Sí | ❌ No |
| Anular pagos | ✅ Sí | ✅ Sí | ❌ No |
| **Horarios** |
| Ver horarios | ✅ Sí | ✅ Sí | ✅ Sí (de sus cursos) |
| Crear/modificar horarios | ✅ Sí | ✅ Sí | ❌ No |

**Filtrado Automático por Multi-tenancy:**
- Todos los endpoints filtran automáticamente por `academiaId` del JWT
- `Admin_plataforma` puede especificar `?academia_id=X` para ver otras academias
- `Admin_academia` y `Profesor_academia` solo ven datos de su academia (forzado en backend)

---

## 7. Flujo Completo: Caso de Uso Real

### Caso: Profesor inicia sesión, ve sus clases del día, pasa lista y agrega anotación

**Descripción del Flujo:**

Este flujo demuestra cómo un profesor interactúa con el sistema a través de una interfaz conversacional para:
1. Ver sus clases del día
2. Acceder a la lista de alumnos
3. Registrar anotaciones sobre el desempeño de los estudiantes

**Arquitectura del Flujo:**

```
Usuario (Android) → Backend Chat → OpenAI GPT-4 → API Workers → MySQL
                         ↑                              ↑
                         └─────── Token Delegado ──────┘
                              (mismo JWT de punta a punta)
```

**Pasos Detallados:**

**PASO 1: Ver Clases del Día**
1. Usuario toca "Mis Clases de Hoy" en el menú
2. Android envía mensaje + JWT a Backend Chat
3. Backend Chat valida JWT y extrae claims (usuarioId, academiaId, role)
4. Backend Chat consulta OpenAI con el mensaje y la especificación de API
5. OpenAI decide llamar a GET /sesiones con filtros de fecha y profesor
6. Backend Chat reenvía el MISMO JWT a API Workers
7. API Workers valida JWT, filtra por academiaId y ejecuta query SQL
8. MySQL devuelve las 3 sesiones del día
9. Backend Chat pasa los resultados a OpenAI
10. OpenAI genera respuesta en lenguaje natural
11. Backend Chat estructura respuesta como Envelope
12. Android renderiza 3 tarjetas de sesiones con estados

**PASO 2: Ver Lista de Alumnos**
1. Usuario toca "Ver alumnos" en una sesión
2. Flujo similar: Android → Backend Chat → OpenAI
3. OpenAI decide llamar a GET /sesiones/6/alumnos
4. Backend Chat reenvía JWT a API Workers
5. API Workers ejecuta query con JOINs para obtener asistencia
6. Devuelve 20 alumnos con su estado (presente/ausente)
7. Backend Chat estructura respuesta con actions (marcar asistencia)
8. Android renderiza lista interactiva

**PASO 3: Agregar Anotación**
1. Usuario escribe: "Añadir anotación a Juan Pérez: Excelente participación"
2. OpenAI interpreta la intención y extrae parámetros
3. OpenAI decide llamar a POST /anotaciones
4. Backend Chat construye body con datos extraídos
5. Backend Chat envía POST con JWT a API Workers
6. API Workers valida, inserta en tabla AnotacionesAlumnoSesion
7. MySQL confirma inserción (id: 123)
8. OpenAI genera mensaje de confirmación
9. Android muestra: "Anotación registrada correctamente para Juan Pérez"

**Telemetría del Flujo Completo:**

| Etapa | Tiempo Estimado | Descripción |
|-------|-----------------|-------------|
| Paso 1: Cargar clases | 2.5 - 4 segundos | Incluye 2 llamadas a OpenAI + 1 query MySQL |
| Paso 2: Cargar alumnos | 1.5 - 2.5 segundos | 2 llamadas OpenAI + 1 query complejo |
| Paso 3: Guardar anotación | 1 - 1.5 segundos | 2 llamadas OpenAI + 1 INSERT |
| **TOTAL** | **5 - 8 segundos** | Para flujo completo de 3 pasos |

**Ventajas de esta Arquitectura:**

- **Trazabilidad:** El mismo usuarioId aparece en logs de todas las capas
- **Seguridad:** JWT validado en cada punto, filtrado automático por academiaId
- **Flexibilidad:** OpenAI interpreta lenguaje natural y decide qué API llamar
- **UX Natural:** Usuario no necesita conocer la estructura de datos

---

## 8. Ventajas de la Arquitectura

### Separación de Responsabilidades (SoC)

**Android App**
- Responsabilidad única: Renderizar UI y gestionar estado local
- No contiene lógica de negocio
- No conoce la estructura de la BD
- Fácil de testear (UI tests con Compose)

**Backend Chat (Orquestador)**
- Responsabilidad: Seguridad, orquestación y traducción
- Valida autenticación (JWT)
- Coordina LLM ↔ API Workers
- Estructura respuestas en formato Envelope
- Gestiona conversaciones (futuro: historial)
- **Reenvía JWT sin modificar** (trazabilidad completa)

**OpenAI GPT-4**
- Responsabilidad: Interpretación de lenguaje natural
- Decide qué herramientas llamar (function calling)
- Genera respuestas en lenguaje natural
- No tiene acceso directo a datos (seguridad)

**API Workers**
- Responsabilidad: Lógica de negocio y acceso a datos
- CRUD completo de todos los recursos
- Validaciones de negocio (ej: capacidad máxima de aulas)
- No conoce el contexto conversacional
- API REST estándar (puede ser usada por otros clientes)

### Seguridad en Capas (Defense in Depth)

1. **Capa 1 - Transporte**: HTTPS/TLS
2. **Capa 2 - Autenticación**: JWT único de punta a punta
3. **Capa 3 - Autorización**: Permisos por rol
4. **Capa 4 - Validación**: Input sanitization
5. **Capa 5 - BD**: Prepared statements, permisos limitados

**Ventaja:** Si una capa falla, las otras siguen protegiendo el sistema.

### Trazabilidad Completa

**Un único JWT viaja por todo el sistema:**
```
Usuario login (JWT generado) 
  → Android guarda JWT
    → Backend Chat valida JWT
      → Backend Chat reenvía MISMO JWT a API Workers
        → API Workers valida MISMO JWT
          → MySQL registra usuarioId del JWT
```

**Beneficios:**
- Logs distribuidos pueden correlacionarse por usuarioId
- Auditoría completa: sabemos qué usuario hizo cada acción en cada capa
- No hay "conversión" de credenciales (menos superficie de ataque)

### Escalabilidad Horizontal

**Android App**
- Escala automáticamente (cada dispositivo es una instancia)

**Backend Chat**
- Stateless (excepto conversationId futuro)
- Puede escalar horizontalmente con load balancer
- Cachear respuestas frecuentes (Redis)

**API Workers**
- Cloudflare edge network (global)
- Escalado automático según tráfico
- CDN para assets estáticos

**MySQL**
- Read replicas para consultas frecuentes
- Sharding por academiaId (multi-tenancy)
- Índices optimizados para queries comunes

### Flexibilidad y Mantenibilidad

**Cambiar LLM sin romper nada**
```javascript
// Solo cambiar en Backend Chat
const openai = new OpenAI({ apiKey: process.env.OPENAI_API_KEY });
// Por:
const anthropic = new Anthropic({ apiKey: process.env.ANTHROPIC_API_KEY });
```

**Añadir nuevas funcionalidades**
- Android: Solo renderizar nuevo tipo en `when (data.type)`
- Backend: Agregar caso al served-openapi.json
- API: Crear nuevo endpoint

**A/B Testing de Prompts**
```javascript
const promptVersion = user.id % 2 === 0 ? 'prompt_v1' : 'prompt_v2';
const systemMessage = PROMPTS[promptVersion];
```

### Desarrollo Paralelo

**Frontend Team** puede trabajar con mocks mientras **Backend Team** implementa endpoints reales.

**QA Team** puede testear UI sin consumir quota de OpenAI.

**Product Team** puede validar UX sin necesidad de backend funcional.

### Auditabilidad y Debugging

**Logs Distribuidos:**
- Android: Logcat con tags por componente
- Backend Chat: Winston con niveles (info, warn, error)
- API Workers: Cloudflare Analytics
- MySQL: Slow query log

**Trazabilidad:**
- Cada mensaje tiene `conversationId` único
- Timestamps en cada capa
- Metadata de modelo y tokens usados
- **usuarioId del JWT en todos los logs**

**Métricas:**
- Latencia por endpoint
- Tasa de éxito/fallo
- Uso de tokens (costos)
- Endpoints más usados

---

## 9. Tecnologías Utilizadas

### Frontend (Android)

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Kotlin | 1.9.x | Lenguaje principal |
| Jetpack Compose | 1.5.x | UI declarativa |
| Material 3 | 1.1.x | Design system |
| Retrofit | 2.9.x | Cliente HTTP |
| OkHttp | 4.11.x | HTTP client (interceptors para JWT) |
| Kotlinx Serialization | 1.6.x | JSON parsing |
| DataStore | 1.0.x | Almacenamiento de preferencias (JWT) |
| ViewModel | 2.6.x | Gestión de estado |
| Navigation Compose | 2.7.x | Navegación declarativa |
| Coroutines | 1.7.x | Programación asíncrona |
| StateFlow | 1.7.x | Reactive state management |

### Backend Chat (Spring Boot)

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 21 LTS | Lenguaje principal |
| Spring Boot | 3.5.5 | Framework web |
| OpenAI Java SDK | Custom | Cliente de OpenAI |
| jjwt (Java JWT) | 0.11.x | Validación JWT |
| Jackson | 2.15.x | JSON parsing |
| Spring Security | 6.x | Security headers |
| Logback | 1.4.x | Logging |
| Maven | 3.9.x | Build tool |

### API Workers (Python/Flask)

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Python | 3.11.x | Lenguaje principal |
| Flask | 3.0.x | Framework web |
| mysql-connector-python | 8.x | Cliente MySQL (con prepared statements) |
| PyJWT | 2.8.x | Generación y validación JWT |
| Argon2-cffi | 23.x | Hashing de passwords (superior a bcrypt) |
| Marshmallow | 3.20.x | Validación de schemas |
| Flask-CORS | 4.0.x | CORS middleware |
| python-dotenv | 1.0.x | Variables de entorno |
| Alembic | 1.13.x | Migraciones de BD |

### Base de Datos

| Tecnología | Versión | Uso |
|------------|---------|-----|
| MySQL | 8.0.x | Base de datos relacional |
| InnoDB | Default | Motor de almacenamiento |
| AWS RDS | - | Servicio de base de datos gestionado |

### DevOps y Despliegue

| Tecnología | Versión | Uso |
|------------|---------|-----|
| AWS Elastic Beanstalk | - | Hosting de Backend Chat y API Workers |
| AWS RDS | MySQL 8.0 | Base de datos en producción |
| Let's Encrypt | - | Certificados SSL/TLS gratuitos |
| GitHub Actions | - | CI/CD |
| Docker | 24.x | Containerización (local development) |

---

## 10. Testing y Calidad

### Tipos de Testing Implementados

El proyecto incluye testing a múltiples niveles para garantizar calidad y fiabilidad:

#### 1. Tests Unitarios (Android)

**Ubicación:** `app/src/test/`

```kotlin
// Ejemplo: MockDataTest.kt
class MockDataTest {
    @Test
    fun `generarSesionesDinamicas genera estados correctos segun hora`() {
        val fecha = LocalDate.now()
        val sesiones = MockData.generarSesionesDinamicas(fecha, "Test Profesor")
        
        // Verificar que sesiones pasadas están completadas
        val ahora = LocalTime.now()
        sesiones.forEach { sesion ->
            if (sesion.horaFin < ahora) {
                assertEquals("completada", sesion.estado)
            }
        }
    }
}
```

**Ejecutar tests unitarios:**
```bash
./gradlew test
```

#### 2. Tests de Integración (Backend Chat)

**Ubicación:** `src/test/java/com/workers/profesores/`

```java
@SpringBootTest
class ChatControllerIntegrationTest {
    @Test
    void testChatEndpointWithValidJWT() {
        // Simula request completo con JWT válido
        // Verifica respuesta Envelope correcta
    }
}
```

**Ejecutar tests de integración:**
```bash
./mvnw test
```

#### 3. Tests E2E con Trazabilidad HTML

**El sistema más avanzado:** Backend Chat genera reportes HTML automáticos de cada flujo E2E completo.

**Activación:**

La generación de reportes HTML se controla mediante la cabecera HTTP `X-Flow-Diagram`:

```bash
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Flow-Diagram: true" \
  -d '{
    "messages": [
      {"role": "user", "content": "Listar usuarios"}
    ]
  }'
```

**Configuración:**

```properties
# application.properties
# Controla si el modo debug está activo (afecta logging detallado)
backend.debug.dev=true   # Desarrollo: verbose logging
backend.debug.prod=false # Producción: logging mínimo
```

**Ubicación de reportes:**
```
backend-chat-openai-worker-profesores/documentacion/
  ├── prueba-20251023-211011-121.html
  ├── prueba-20251024-103045-234.html
  └── ...
```

**Contenido del reporte HTML:**

El HTML generado incluye una tabla cronológica con cada paso del flujo:

| Paso | Descripción | Timestamp |
|------|-------------|-----------|
| 1. ChatController | Recibida petición POST /chat | 2025-10-23 21:10:11.122 |
| 2. ChatController | JWT válido: usuarioId=10, roles=[Admin_academia] | 2025-10-23 21:10:11.125 |
| 3. ChatService | Inicio de runChat | 2025-10-23 21:10:11.126 |
| 4. ChatService | Delegated token creado: preview=Bearer e... | 2025-10-23 21:10:11.136 |
| 5. OpenAI | Llamada a OpenAI (iteración 0) | 2025-10-23 21:10:11.147 |
| 6. OpenAIClient | Procesando tool_call: call_api | 2025-10-23 21:10:15.206 |
| 7. ApiProxyService | Llamada a API: usuarios.listar_usuarios (GET) | 2025-10-23 21:10:15.226 |
| 8. Telemetry | api_call_ms=311 (single) | 2025-10-23 21:10:15.538 |
| 9. OpenAI | Segunda llamada a OpenAI (sin herramientas) | 2025-10-23 21:10:15.587 |
| 10. ChatController | Respuesta generada (JSON completo) | 2025-10-23 21:10:19.473 |

**Métricas automáticas incluidas:**
- `seed_ms`: Tiempo de preparación de contexto
- `parse_ms`: Tiempo de parseo de respuesta OpenAI
- `api_call_ms`: Tiempo de llamada a API Workers
- `second_ms`: Tiempo del segundo turno con OpenAI
- `decision_ms`: Tiempo total de decisión (ambos turnos)
- `render_ms`: Tiempo de renderizado de respuesta
- `controller_ms_desde_inicio`: Tiempo total desde request hasta response

**Ejemplo de salida HTML:**

Ver archivo real: `documentacion/prueba-20251023-211011-121.html`

```html
<h2>Flujo de la prueba: prueba-20251023-211011-121</h2>
<table>
  <tr>
    <td>30. ChatController</td>
    <td>Respuesta generada por ChatService (pretty)={
      "status": "success",
      "message": "Te muestro el resultado de la búsqueda de usuarios: 15 usuarios encontrados (página 1 de 2).",
      "data": { ... }
    }</td>
    <td>2025-10-23 21:10:19.473</td>
  </tr>
</table>
```

**Cuándo usar reportes HTML:**

✅ **Activar (X-Flow-Diagram: true):**
- Testing manual con Postman/curl
- Debugging de flujos complejos
- Demos para stakeholders
- Auditorías de performance
- Análisis de prompts de OpenAI

❌ **Desactivar (omitir cabecera):**
- Producción (overhead de I/O)
- Tests automatizados masivos
- Requests de usuarios reales

**Ventajas del sistema de reportes:**

1. **Trazabilidad Completa:** Ve cada paso del flujo OpenAI → API Workers → MySQL
2. **Debugging Visual:** HTML legible con timestamps precisos
3. **Performance Profiling:** Identifica cuellos de botella por paso
4. **Auditoría:** Registro permanente de decisiones del LLM
5. **Documentación Viva:** Los HTMLs sirven como ejemplos reales

#### 4. Tests de API (Postman/Thunder Client)

**Colecciones disponibles:**
- `postman/Academia_Chat_API.json` (todas las rutas)
- Incluye tests automatizados de cada endpoint
- Variables de entorno para dev/prod

**Ejecutar colección:**
```bash
newman run postman/Academia_Chat_API.json --environment dev.json
```

#### 5. Tests de UI (Android - Espresso)

**Ubicación:** `app/src/androidTest/`

```kotlin
@Test
fun testChatScreenRendersMessages() {
    composeTestRule.setContent {
        ChatScreen(...)
    }
    
    composeTestRule.onNodeWithText("Mis Clases de Hoy").assertIsDisplayed()
}
```

### Estrategia de Testing por Entorno

| Entorno | Tests Ejecutados | Frecuencia |
|---------|------------------|------------|
| **Local (Dev)** | Unitarios + Mocks | En cada cambio |
| **CI/CD (GitHub Actions)** | Unitarios + Integración | En cada push |
| **Staging** | E2E con HTML + Postman | Antes de cada release |
| **Producción** | Smoke tests + Monitoreo | Post-deployment |

### Cobertura de Código

**Objetivos:**
- Android: >80% cobertura en ViewModels y Repositories
- Backend Chat: >70% cobertura en Services
- API Workers: >75% cobertura en Routes y Logic

**Generar reporte de cobertura:**
```bash
# Android
./gradlew testDebugUnitTestCoverage

# Backend Chat
./mvnw jacoco:report

# API Workers
pytest --cov=src --cov-report=html
```

---

## 11. Plan de Despliegue en Producción

### Estado Actual del Proyecto

**Fase 1: MVP (✅ COMPLETADO) - Listo para Demo**

**Android App:**
- ✅ ChatScreen completamente funcional
- ✅ NavigationDrawer con menú dinámico por rol
- ✅ MockChatRepository con casos de uso completos
- ✅ RealChatRepository implementado y testeado
- ✅ SessionStore con gestión de tokens JWT
- ✅ LoginScreen con validación y manejo de errores
- ✅ Integración completa con Backend Chat en producción

**Backend Chat (Spring Boot):**
- ✅ Servidor funcional con middleware JWT
- ✅ Integración OpenAI GPT-4 con function calling
- ✅ Endpoint POST /chat con validación completa
- ✅ Sistema de prompts (served-openapi.json)
- ✅ Manejo de errores y retry logic
- ✅ Logging con telemetría detallada
- ✅ Rate limiting configurado
- ✅ Middleware de reenvío de JWT (token delegado)
- ✅ Reportes HTML E2E para debugging

**API Workers (Python/Flask):**
- ✅ Endpoints de autenticación (login, refresh, logout)
- ✅ Generación y validación de JWT
- ✅ Endpoints completos de alumnos, cursos, sesiones, anotaciones
- ✅ Middleware de validación JWT
- ✅ Conexión a MySQL con pool de conexiones
- ✅ Migraciones de BD (create_database.sql)
- ✅ Seeds de datos de prueba
- ✅ Tests de integración funcionales

**Base de Datos:**
- ✅ MySQL 8.0 en AWS RDS
- ✅ 26 tablas con relaciones completas
- ✅ Índices optimizados
- ✅ Backups automáticos configurados

### Arquitectura de Despliegue en AWS

```
┌─────────────────────────────────────────────────────────────┐
│ USUARIOS MÓVILES (Android App)                              │
└────────────────┬────────────────────────────────────────────┘
                 │ HTTPS/TLS 1.3
                 ▼
┌─────────────────────────────────────────────────────────────┐
│ AWS ELASTIC BEANSTALK - Backend Chat                        │
│ - Entorno: Java 21 (Corretto)                              │
│ - Tipo: Single Instance t2.micro (Free Tier)               │
│ - HTTPS: Let's Encrypt (certbot automático)                │
│ - URL: backend-chat.elasticbeanstalk.com                   │
│ - Health check: /health                                     │
│ - Auto-scaling: Disabled (costo $0 primer año)             │
└────────────────┬────────────────────────────────────────────┘
                 │ HTTPS (token delegado - JWT)
                 ▼
┌─────────────────────────────────────────────────────────────┐
│ AWS ELASTIC BEANSTALK - API Workers                         │
│ - Entorno: Python 3.11                                      │
│ - Tipo: Single Instance t2.micro (Free Tier)               │
│ - HTTPS: Let's Encrypt                                      │
│ - URL: api-workers.elasticbeanstalk.com                    │
│ - Health check: /health                                     │
└────────────────┬────────────────────────────────────────────┘
                 │ MySQL Protocol over TLS
                 ▼
┌─────────────────────────────────────────────────────────────┐
│ AWS RDS - MySQL 8.0                                          │
│ - Instancia: db.t3.micro (Free Tier)                       │
│ - Almacenamiento: 20 GB SSD                                 │
│ - Multi-AZ: No (para reducir costos)                       │
│ - Backups: Automated (7 días retention)                    │
│ - Conexión: SSL/TLS obligatorio                            │
└─────────────────────────────────────────────────────────────┘
```

### Costos Mensuales Estimados

**Opción 1: Free Tier (Primer Año) - $0/mes**
- Elastic Beanstalk Backend Chat: $0 (t2.micro Free Tier)
- Elastic Beanstalk API Workers: $0 (t2.micro Free Tier)
- RDS MySQL: $0 (db.t3.micro Free Tier 750h/mes)
- **Total: $0/mes** ✅

**Opción 2: Post Free Tier - ~$11-13/mes**
- EC2 t2.micro (Backend Chat): ~$8.50/mes
- EC2 t2.micro (API Workers): Incluido en t2.micro de Backend
- RDS db.t3.micro: ~$15/mes
- Elastic IP: $0 (mientras esté asignado)
- **Total: ~$11-13/mes** (académico sostenible)

**Opción 3: Producción Escalada - ~$65-85/mes**
- Application Load Balancer: ~$16/mes
- EC2 t3.small x2 (Auto-scaling): ~$30/mes
- RDS db.t3.small (Multi-AZ): ~$30/mes
- CloudWatch + S3: ~$5/mes
- **Total: ~$65-85/mes** (50-100 usuarios concurrentes)

Ver documento completo: `docs/ESCALADO_A_PRODUCCION_COMERCIAL.md`

### Pasos de Despliegue

**Fase 2: Despliegue Inicial (1-2 semanas)**

1. **Preparar Backend Chat para AWS:**
   ```bash
   # Crear archivo .ebextensions/01-app-config.config
   # Configurar variables de entorno en EB
   # Probar localmente con perfil prod
   ```

2. **Desplegar Backend Chat:**
   ```bash
   eb init backend-chat --region us-east-1 --platform java-21
   eb create backend-chat-env --single --instance-type t2.micro
   eb setenv OPENAI_API_KEY=sk-xxx JWT_DELEGATION_SECRET=xxx
   eb deploy
   ```

3. **Configurar HTTPS con Let's Encrypt:**
   ```bash
   # SSH a la instancia
   sudo certbot --nginx -d backend-chat.tu-dominio.com
   # Configurar renovación automática
   sudo crontab -e
   # 0 0 1 * * certbot renew --quiet
   ```

4. **Desplegar API Workers:**
   ```bash
   eb init api-workers --region us-east-1 --platform python-3.11
   eb create api-workers-env --single --instance-type t2.micro
   eb setenv DATABASE_URL=mysql://... JWT_SECRET_KEY=xxx
   eb deploy
   ```

5. **Configurar RDS MySQL:**
   ```bash
   # Crear instancia RDS desde consola AWS
   # Aplicar create_database.sql
   mysql -h academia-db.xxx.rds.amazonaws.com -u admin -p < docs/create_database.sql
   
   # Ejecutar seeds
   python init_db_pruebas_test.py
   ```

6. **Actualizar Android App:**
   ```kotlin
   // build.gradle.kts
   buildConfigField("String", "BACKEND_CHAT_URL", 
     "\"https://backend-chat.elasticbeanstalk.com\"")
   buildConfigField("boolean", "USE_MOCK", "false")
   ```

7. **Testing E2E Completo:**
   ```bash
   # Activar X-Flow-Diagram en Postman
   # Verificar generación de HTMLs
   # Validar tiempos de respuesta
   # Verificar trazabilidad JWT end-to-end
   ```

**Fase 3: Optimización y Monitoreo (Continuo)**

1. **CloudWatch Alarms:**
   - CPU > 80% durante 5 minutos
   - Disco > 90%
   - Health check failures

2. **Logs Centralizados:**
   - CloudWatch Logs para Backend Chat
   - CloudWatch Logs para API Workers
   - Query Insights en RDS

3. **Performance:**
   - Caché de respuestas frecuentes (Redis futuro)
   - CDN para assets estáticos (CloudFront futuro)
   - Optimización de queries MySQL (índices)

### Checklist de Pre-Producción

- [ ] Todos los secrets en variables de entorno (NO en código)
- [ ] HTTPS configurado con certificados válidos
- [ ] Backups automáticos de RDS activados
- [ ] Health checks respondiendo correctamente
- [ ] Logs de error capturando excepciones
- [ ] Rate limiting configurado en ambos backends
- [ ] CORS restrictivo (solo dominios autorizados)
- [ ] SQL Injection prevención (prepared statements)
- [ ] JWT secrets largos y aleatorios (>32 caracteres)
- [ ] Tests E2E pasando con reportes HTML
- [ ] Documentación actualizada (este documento)
- [ ] Plan de rollback preparado
- [ ] Monitoreo configurado (CloudWatch/Datadog)

### Estrategia de Rollback

En caso de problemas en producción:

1. **Rollback de Backend:**
   ```bash
   eb deploy --version <version-anterior>
   ```

2. **Rollback de BD (si aplica):**
   ```bash
   # Restaurar desde snapshot más reciente
   aws rds restore-db-instance-from-db-snapshot --db-instance-identifier ...
   ```

3. **Rollback de Android:**
   ```bash
   # Compilar versión anterior
   git checkout <tag-anterior>
   ./gradlew assembleRelease
   # Subir a Google Play como hotfix
   ```

### Documentación de Referencia

- **Despliegue Backend Chat:** `docs/DEPLOY_AWS_HTTPS.md`
- **Despliegue API Workers:** `docs/DEPLOY_AWS_API_WORKERS.md`
- **Escalado Comercial:** `docs/ESCALADO_A_PRODUCCION_COMERCIAL.md`
- **Seguridad Android:** `documentacion/SEGURIDAD_ENCRIPTACION.md`
- **Testing E2E:** `documentacion/prueba-20251023-211011-121.html` (ejemplo)

---

## Anexos

### A. Glosario de Términos

**Envelope**: Estructura de datos estándar que envuelve todas las respuestas del chat.

**GenericItem**: Objeto polimórfico que representa cualquier entidad (alumno, curso, sesión, etc.).

**Tool Calling**: Mecanismo de OpenAI para que el LLM solicite ejecutar funciones externas.

**served-openapi.json**: Especificación de la API en formato OpenAPI que se envía a GPT-4.

**SessionStore**: Almacén local (DataStore) de tokens y datos del usuario.

**Context Map**: Mapa de datos que viaja con cada mensaje para mantener estado conversacional.

**Mock**: Implementación simulada de un repositorio para desarrollo sin backend.

**JWT**: JSON Web Token, estándar para autenticación basada en tokens. En nuestro sistema viaja sin modificarse desde Android hasta MySQL.

**Claims**: Datos incluidos en el JWT (usuarioId, academiaId, role, email, exp, iat).

### B. Referencias

**Documentación Oficial:**
- [OpenAI Function Calling Guide](https://platform.openai.com/docs/guides/function-calling)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Cloudflare Workers Docs](https://developers.cloudflare.com/workers/)
- [MySQL 8.0 Reference Manual](https://dev.mysql.com/doc/refman/8.0/en/)

**Estándares:**
- [JWT RFC 7519](https://datatracker.ietf.org/doc/html/rfc7519)
- [OpenAPI Specification](https://swagger.io/specification/)
- [RESTful API Guidelines](https://restfulapi.net/)

**Arquitectura:**
- [Clean Architecture (Robert C. Martin)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [MVVM Pattern](https://developer.android.com/topic/architecture)

---

**Versión del Documento:** 2.0.0  
**Fecha de Última Actualización:** 24 de enero de 2025  
**Autor:** Angel Fernández Vidal  
**Estado:** Documento Vivo (se actualiza con cada fase)  
**Cambios en v2.0.0:** 
- Explicado concepto de "token delegado"
- Listadas TODAS las tablas de base de datos
- Simplificado diagrama de arquitectura
- Expandida tabla de endpoints
- Agregado ejemplo de respuesta de alumnos
- Extraídos permisos por rol
- Corregido error de sintaxis Mermaid
- Eliminadas referencias a mocks en secciones de producción
- Reemplazado roadmap de implementación por plan de despliegue en producción
- Agregadas cadenas de conexión a base de datos
- Agregada sección de testing con ejemplos E2E
- Clarificado endpoint /chat/stream (SSE)
