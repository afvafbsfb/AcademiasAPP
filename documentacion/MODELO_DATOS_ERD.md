# MODELO DE DATOS - SISTEMA DE GESTIÓN DE ACADEMIAS

**Versión:** 1.0.0  
**Fecha:** 24 de octubre de 2025  
**Autor:** Ángel Fernández Vidal  
**Fuente:** create_database.sql  
**Motor:** MySQL 8.0 / InnoDB  
**Charset:** utf8mb4 / utf8mb4_unicode_ci  

---

## 1. UNIVERSO DEL DISCURSO

El sistema "Academia Manager" es una plataforma integral para la gestión de academias educativas que ofrece cursos de formación en diferentes áreas (idiomas, música, apoyo escolar, preparación de exámenes, etc.) para alumnos de todas las edades.

### 1.1. Contexto General

El sistema permite gestionar múltiples academias desde una misma base de datos (arquitectura multi-tenant), garantizando el aislamiento total de los datos de cada academia. Cada academia puede tener su propia configuración de cursos, profesores, alumnos, tarifas y horarios.

### 1.2. Gestión de Academias

Cada academia registrada en el sistema tiene:
- Un nombre único que la identifica
- Fecha de alta en el sistema
- Control de bajas lógicas (soft delete) para mantener el historial
- Trazabilidad de modificaciones (fecha y usuario responsable)

Las academias pueden estar en estado activo o de baja, pero nunca se eliminan físicamente de la base de datos para mantener la integridad referencial de los datos históricos.

### 1.3. Gestión de Usuarios y Seguridad

El sistema implementa un robusto modelo de seguridad con tres niveles de permisos:

**Roles disponibles:**
1. **Admin_plataforma:** Tiene acceso completo al sistema, puede gestionar todas las academias, crear y eliminar usuarios administradores de academia, y tiene permisos sobre toda la configuración global.

2. **Admin_academia:** Tiene acceso completo a su academia específica. Puede gestionar profesores, alumnos, cursos, horarios, tarifas, inscripciones y toda la información financiera de su academia. No tiene acceso a otras academias.

3. **Profesor_academia:** Tiene acceso limitado a su academia. Puede consultar los cursos a los que está asignado, registrar sesiones, tomar asistencia, hacer anotaciones sobre los alumnos y consultar información académica. No puede modificar configuraciones ni acceder a información financiera.

**Sistema de autenticación:**
- Contraseñas hasheadas con Argon2 (algoritmo resistente a ataques de fuerza bruta)
- Tokens JWT para autenticación en API
- RefreshTokens con rotación automática (30 días de validez)
- Sistema anti-fuerza bruta: bloqueo automático tras intentos fallidos
- Revocación masiva de tokens mediante `token_version`
- Auditoría completa de accesos (login/logout) con registro de IP, user-agent y dispositivo
- Recuperación de contraseña con tokens de un solo uso y caducidad

### 1.4. Gestión Académica

**Cursos:**
Cada academia puede ofrecer múltiples cursos caracterizados por:
- Nombre descriptivo del curso
- Año académico (formato: "2024-2025")
- Fechas de inicio y fin del curso
- Capacidad máxima de alumnos
- Tipo de alumnado: Infantil, Juvenil o Adultos
- Estado: Activo, Inactivo o Finalizado
- Indicador de si acepta nuevos alumnos
- Tarifa asociada para la matrícula

**Aulas:**
Las academias disponen de aulas físicas con:
- Nombre o identificador del aula
- Capacidad máxima de personas

**Horarios:**
Cada curso tiene uno o varios horarios semanales definidos por:
- Día de la semana
- Hora de inicio y fin
- Aula donde se imparte
- Relación con el curso

**Profesores asignados:**
Los profesores (usuarios con rol Profesor_academia) se asignan a cursos específicos mediante la tabla Curso_Profesores que registra:
- Fecha de alta en el curso
- Fecha de baja (si aplica)
- Motivo de la baja
- Trazabilidad de modificaciones

Un profesor puede estar asignado a múltiples cursos y un curso puede tener múltiples profesores.

**Sesiones de clase:**
Por cada horario programado, se generan sesiones (clases) que registran:
- Horario del curso al que pertenece
- Aula donde se imparte (puede variar del aula planificada)
- Profesor que imparte la sesión
- Timestamp real de inicio de la sesión
- Timestamp real de finalización
- Hora planificada de inicio y fin
- Notas generales de la sesión
- Notas sobre la materia impartida
- Motivo de baja si la sesión se cancela

### 1.5. Gestión de Alumnos

**Datos del alumno:**
Del alumno se registra:
- Datos personales: nombre, DNI, email, teléfono, fecha de nacimiento
- Dirección postal completa
- Datos del tutor legal (para menores): nombre, relación, teléfono, email

El email del alumno debe ser único en todo el sistema.

**Inscripciones:**
Un alumno se matricula en uno o varios cursos mediante inscripciones que registran:
- Alumno y curso relacionados
- Tarifa aplicada en el momento de la inscripción
- Fecha de inicio y fin de la inscripción
- Motivo de baja (si el alumno causa baja antes del fin del curso)

**Relaciones familiares:**
El sistema permite registrar vínculos familiares entre alumnos (hermanos, primos, etc.) para:
- Aplicar descuentos familiares automáticos
- Facilitar la gestión administrativa
- Generar comunicaciones agrupadas

**Anotaciones académicas:**
Durante las sesiones, los profesores pueden registrar tres tipos de anotaciones sobre los alumnos:
1. **Ausencia:** Registro de que el alumno no asistió a la sesión
2. **Evaluacion:** Comentarios sobre el progreso académico del alumno
3. **Comportamiento:** Observaciones sobre la actitud o conducta

Cada anotación incluye:
- Sesión, inscripción, curso y profesor relacionados
- Tipo de anotación
- Texto descriptivo
- Timestamps de alta y baja
- Motivo de baja (si se elimina la anotación)

### 1.6. Gestión Financiera

**Tarifas:**
Cada academia define sus propias tarifas con:
- Descripción de la tarifa
- Precio base mensual
- Fechas de vigencia (alta y baja)
- Trazabilidad de modificaciones

**Descuentos sobre tarifas:**
Las tarifas pueden tener descuentos asociados:
- Motivo: 'F' para descuento familiar, 'M' para descuento por periodo corto
- Tipo: 'P' para porcentaje, 'F' para importe fijo
- Valor del descuento (porcentaje o importe)

**Extractos mensuales:**
Por cada inscripción activa, se generan extractos mensuales que calculan automáticamente:
- Número correlativo de extracto
- Periodo del extracto (fecha inicio y fin)
- Saldo arrastrado del mes anterior
- Cuota base sin descuentos
- Hasta 3 descuentos aplicables simultáneamente (cada uno con motivo, tipo, porcentaje e importe)
- Total de descuentos aplicados
- Cuota mensual con descuentos
- Exceso de ingresos (si los pagos superan lo adeudado)
- Total a cobrar
- Estado de liquidación: Pendiente o Liquidado

**Movimientos del extracto:**
Cada extracto tiene asociados movimientos financieros:
- Tipo: Ingreso o Anulación de Ingreso
- Fecha del movimiento
- Importe
- Método de pago: Efectivo, Tarjeta, Transferencia
- Descripción del movimiento
- Estado de liquidación: Pendiente o Cobrado
- Indicador de si el movimiento está anulado

### 1.7. Trabajadores Virtuales (IA)

El sistema contempla la integración de asistentes virtuales basados en Inteligencia Artificial:
- Nombre del trabajador virtual
- Foto (URL)
- Descripción de sus capacidades
- API Key para la integración con servicios de IA
- Indicador de si es un administrativo virtual

Los trabajadores virtuales permiten:
- Atención automatizada a consultas de alumnos y padres
- Asistencia en tareas administrativas
- Respuestas a preguntas frecuentes
- Gestión de citas y comunicaciones

### 1.8. Auditoría y Trazabilidad

El sistema mantiene un registro exhaustivo de:
- Todos los intentos de login (exitosos y fallidos)
- Sesiones activas de usuarios
- Cambios en datos críticos (con fecha y usuario responsable)
- Tokens de sesión y su cadena de rotación
- Direcciones IP y dispositivos desde donde se accede
- Soft deletes para mantener historial sin pérdida de datos

### 1.9. Características Técnicas

**Multi-tenancy:**
Todas las tablas principales están vinculadas a `academia_id` para garantizar el aislamiento de datos entre academias.

**Integridad referencial:**
Todas las relaciones están respaldadas por Foreign Keys con políticas de ON DELETE y ON UPDATE apropiadas.

**Soft Delete:**
Las entidades críticas (Academia, Usuario, Inscripcion, Sesion, AnotacionesAlumnoSesion) utilizan baja lógica mediante el campo `fecha_baja`.

**Auditoría automática:**
Campos `fecha_alta`, `fecha_ultima_modificacion` y `usuario_id_ultima_modificacion` se mantienen automáticamente mediante triggers y valores por defecto.

---

## 2. DIAGRAMA ENTIDAD-RELACIÓN

**Nota:** El diagrama en notación de Chen (rectángulos para entidades, rombos para relaciones, círculos para atributos) se encuentra en el archivo `MODELO_DATOS_ERD.drawio` adjunto.

Para visualizar el diagrama, abrir el archivo con draw.io (https://app.diagrams.net/)

---

## 3. DESCRIPCIÓN DETALLADA DE TABLAS

### 3.1. Academia

**Descripción:** Entidad principal que representa cada academia registrada en el sistema. Implementa multi-tenancy.

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | INT | Identificador único de la academia | PK, AUTO_INCREMENT |
| nombre | VARCHAR(100) | Nombre comercial de la academia | NOT NULL, UNIQUE |
| fecha_alta | DATETIME | Fecha y hora de registro en el sistema | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| fecha_baja | DATETIME | Fecha y hora de baja lógica | NULL (NULL = activa) |
| fecha_ultima_modificacion | DATETIME | Última modificación de cualquier campo | NOT NULL, ON UPDATE CURRENT_TIMESTAMP |
| usuario_id_ultima_modificacion | INT | Usuario que realizó la última modificación | NULL |

**Índices:**
- PK: id
- UK: nombre

**Relaciones:**
- Es referenciada por: Tarifa, Curso, Aula, Usuario, Alumno, TrabajadorVirtual

---

### 3.2. Rol_Usuario

**Descripción:** Catálogo de roles disponibles en el sistema para control de acceso basado en roles (RBAC).

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | INT | Identificador único del rol | PK, AUTO_INCREMENT |
| nombre | VARCHAR(50) | Nombre del rol | NOT NULL, UNIQUE |

**Valores predefinidos:**
1. Admin_plataforma
2. Admin_academia
3. Profesor_academia

**Índices:**
- PK: id
- UK: nombre

**Relaciones:**
- Es referenciada por: Usuario

---

### 3.3. Usuario

**Descripción:** Usuarios del sistema con acceso a la plataforma (administradores y profesores).

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | INT | Identificador único del usuario | PK, AUTO_INCREMENT |
| academia_id | INT | Academia a la que pertenece | NULL (NULL para Admin_plataforma), FK |
| nombre | VARCHAR(100) | Nombre completo del usuario | NOT NULL |
| email | VARCHAR(120) | Email único para login | NOT NULL, UNIQUE |
| password | VARCHAR(255) | Hash Argon2 de la contraseña | NOT NULL |
| rol_id | INT | Rol asignado al usuario | NOT NULL, FK |
| estado | ENUM | Estado actual del usuario | NOT NULL, DEFAULT 'Activo' |
| fecha_alta | DATETIME | Fecha de alta en el sistema | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| fecha_baja | DATETIME | Fecha de baja lógica | NULL |
| fecha_ultima_modificacion | DATETIME | Última modificación | NOT NULL, ON UPDATE CURRENT_TIMESTAMP |
| token_version | INT | Versión de token para revocación masiva | NOT NULL, DEFAULT 0 |
| failed_login_count | INT | Contador de intentos fallidos de login | NOT NULL, DEFAULT 0 |
| last_failed_login_at | DATETIME | Timestamp del último intento fallido | NULL |
| locked_until | DATETIME | Timestamp hasta el que está bloqueado | NULL |

**Valores ENUM estado:**
- Activo
- Bloqueado
- Baja

**Índices:**
- PK: id
- UK: email (idx_usuario_email)
- INDEX: token_version (idx_usuario_token_version)

**Relaciones:**
- FK: academia_id → Academia(id)
- FK: rol_id → Rol_Usuario(id)
- Es referenciada por: UserLoginLog, RefreshToken, PasswordResetToken, Curso_Profesores

---

### 3.4. UserLoginLog

**Descripción:** Auditoría de intentos de login (exitosos y fallidos) y sesiones de usuarios.

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | BIGINT | Identificador único del log | PK, AUTO_INCREMENT |
| usuario_id | INT | Usuario que intenta login | NOT NULL, FK |
| login_at | DATETIME | Timestamp del login | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| logout_at | DATETIME | Timestamp del logout | NULL |
| success | BOOLEAN | Si el login fue exitoso | NOT NULL |
| fail_reason | VARCHAR(100) | Motivo del fallo | NULL |
| ip | VARBINARY(16) | Dirección IP (IPv4/IPv6) | NULL |
| user_agent | VARCHAR(255) | Navegador/cliente usado | NULL |
| device_id | VARCHAR(100) | Identificador del dispositivo | NULL |
| client | VARCHAR(50) | Tipo de cliente | NULL |

**Valores de fail_reason:**
- BAD_CREDENTIALS
- LOCKED
- MFA_FAIL
- ACCOUNT_DISABLED

**Valores de client:**
- web
- android
- ios

**Índices:**
- PK: id
- INDEX: (usuario_id, login_at) idx_ull_usuario_login
- INDEX: (success, login_at) idx_ull_success_login

**Relaciones:**
- FK: usuario_id → Usuario(id)

---

### 3.5. RefreshToken

**Descripción:** Tokens de refresco para mantener sesiones activas con rotación automática.

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | BIGINT | Identificador único del token | PK, AUTO_INCREMENT |
| usuario_id | INT | Usuario propietario del token | NOT NULL, FK |
| token_hash | CHAR(64) | Hash SHA-256 del token | NOT NULL, UNIQUE |
| issued_at | DATETIME | Fecha de emisión | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| expires_at | DATETIME | Fecha de expiración (30 días) | NOT NULL |
| revoked_at | DATETIME | Fecha de revocación | NULL |
| replaced_by_id | BIGINT | Token que lo reemplaza en rotación | NULL, FK |
| ip | VARBINARY(16) | IP desde donde se emitió | NULL |
| user_agent | VARCHAR(255) | User agent del cliente | NULL |
| device_id | VARCHAR(100) | Identificador del dispositivo | NULL |
| scope | VARCHAR(200) | Alcance del token | NULL |

**Índices:**
- PK: id
- UK: token_hash (uk_refreshtoken_hash)
- INDEX: (usuario_id, expires_at) idx_refreshtoken_usuario_exp
- INDEX: revoked_at (idx_refreshtoken_revoked)

**Relaciones:**
- FK: usuario_id → Usuario(id)
- FK: replaced_by_id → RefreshToken(id) (auto-referencia)

---

### 3.6. PasswordResetToken

**Descripción:** Tokens de un solo uso para recuperación de contraseña.

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | BIGINT | Identificador único del token | PK, AUTO_INCREMENT |
| usuario_id | INT | Usuario que solicita reset | NOT NULL, FK |
| token_hash | CHAR(64) | Hash SHA-256 del token | NOT NULL, UNIQUE |
| expires_at | DATETIME | Fecha de expiración | NOT NULL |
| used_at | DATETIME | Fecha de uso del token | NULL |
| ip | VARBINARY(16) | IP de la solicitud | NULL |
| user_agent | VARCHAR(255) | User agent del cliente | NULL |

**Índices:**
- PK: id
- UK: token_hash (uk_pwdreset_hash)
- INDEX: (usuario_id, expires_at) idx_pwdreset_usuario_exp

**Relaciones:**
- FK: usuario_id → Usuario(id)

---

### 3.7. Tarifa

**Descripción:** Tarifas definidas por cada academia para cobro de cursos.

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | INT | Identificador único de la tarifa | PK, AUTO_INCREMENT |
| academia_id | INT | Academia propietaria | NOT NULL, FK |
| descripcion | VARCHAR(255) | Descripción de la tarifa | NULL |
| precio_base | FLOAT | Precio base mensual en euros | NOT NULL |
| fecha_alta | DATETIME | Fecha de creación | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| fecha_baja | DATETIME | Fecha de baja lógica | NULL |
| fecha_ultima_modificacion | DATETIME | Última modificación | NOT NULL, ON UPDATE CURRENT_TIMESTAMP |

**Índices:**
- PK: id

**Relaciones:**
- FK: academia_id → Academia(id)
- Es referenciada por: Descuentos_tarifa, Curso, Inscripcion

---

### 3.8. Descuentos_tarifa

**Descripción:** Descuentos aplicables sobre una tarifa base.

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | INT | Identificador único del descuento | PK, AUTO_INCREMENT |
| tarifa_id | INT | Tarifa a la que se aplica | NOT NULL, FK |
| motivo_descuento | CHAR(1) | Código del motivo | NOT NULL |
| tipo_descuento | CHAR(1) | Tipo de cálculo | NOT NULL |
| porcentaje_descuento | FLOAT | Porcentaje si tipo='P' | NULL |
| importe_descuento | FLOAT | Importe fijo si tipo='F' | NULL |

**Valores de motivo_descuento:**
- 'F': Familiar (hermanos en el centro)
- 'M': Mensualidad reducida (periodo < 15 días)

**Valores de tipo_descuento:**
- 'P': Porcentaje sobre precio_base
- 'F': Importe fijo a descontar

**Índices:**
- PK: id

**Relaciones:**
- FK: tarifa_id → Tarifa(id)

---

### 3.9. Curso

**Descripción:** Cursos ofrecidos por cada academia.

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | INT | Identificador único del curso | PK, AUTO_INCREMENT |
| academia_id | INT | Academia que ofrece el curso | NOT NULL, FK |
| nombre | VARCHAR(100) | Nombre del curso | NOT NULL |
| anio_academico | VARCHAR(20) | Año académico (ej: "2024-2025") | NOT NULL |
| fecha_inicio | DATE | Fecha de inicio del curso | NOT NULL |
| fecha_fin | DATE | Fecha de finalización | NOT NULL |
| acepta_nuevos_alumnos | BOOLEAN | Si acepta matrículas nuevas | NOT NULL |
| capacidad_maxima | INT | Número máximo de alumnos | NOT NULL |
| tarifa_id | INT | Tarifa asociada al curso | NOT NULL, FK |
| tipo_alumno | ENUM | Rango de edad del alumnado | NOT NULL |
| estado | ENUM | Estado actual del curso | NOT NULL |

**Valores ENUM tipo_alumno:**
- Infantil
- Juvenil
- Adultos

**Valores ENUM estado:**
- Activo
- Inactivo
- Finalizado

**Índices:**
- PK: id

**Relaciones:**
- FK: academia_id → Academia(id)
- FK: tarifa_id → Tarifa(id)
- Es referenciada por: HorarioCurso, Inscripcion, Curso_Profesores, AnotacionesAlumnoSesion, Extractos, Movimientos_Extracto

---

### 3.10. Aula

**Descripción:** Espacios físicos donde se imparten las clases.

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | INT | Identificador único del aula | PK, AUTO_INCREMENT |
| academia_id | INT | Academia propietaria | NOT NULL, FK |
| nombre | VARCHAR(100) | Nombre o código del aula | NOT NULL |
| capacidad_maxima | INT | Aforo máximo del aula | NOT NULL |

**Índices:**
- PK: id

**Relaciones:**
- FK: academia_id → Academia(id)
- Es referenciada por: HorarioCurso, Sesion

---

### 3.11. HorarioCurso

**Descripción:** Horarios semanales de cada curso.

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | INT | Identificador único del horario | PK, AUTO_INCREMENT |
| curso_id | INT | Curso al que pertenece | NOT NULL, FK |
| aula_id | INT | Aula donde se imparte | NOT NULL, FK |
| dia_semana | VARCHAR(20) | Día de la semana | NOT NULL |
| hora_inicio | TIME | Hora de inicio de la clase | NOT NULL |
| hora_fin | TIME | Hora de fin de la clase | NOT NULL |

**Valores de dia_semana:**
- Lunes, Martes, Miércoles, Jueves, Viernes, Sábado, Domingo

**Índices:**
- PK: id

**Relaciones:**
- FK: curso_id → Curso(id)
- FK: aula_id → Aula(id)
- Es referenciada por: Sesion

---

### 3.12. Curso_Profesores

**Descripción:** Asignación de profesores a cursos específicos.

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | INT | Identificador único de la asignación | PK, AUTO_INCREMENT |
| curso_id | INT | Curso asignado | NOT NULL, FK |
| usuario_id | INT | Profesor asignado | NOT NULL, FK |
| fecha_alta | DATETIME | Fecha de asignación | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| fecha_baja | DATETIME | Fecha de baja del curso | NULL |
| fecha_ult_modificacion | DATETIME | Última modificación | NOT NULL, ON UPDATE CURRENT_TIMESTAMP |
| motivo_baja | VARCHAR(255) | Razón de la desasignación | NULL |

**Índices:**
- PK: id

**Relaciones:**
- FK: curso_id → Curso(id)
- FK: usuario_id → Usuario(id)
- Es referenciada por: Sesion, AnotacionesAlumnoSesion

---

### 3.13. Sesion

**Descripción:** Sesiones individuales de clase (instancias concretas de HorarioCurso).

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | INT | Identificador único de la sesión | PK, AUTO_INCREMENT |
| horario_curso_id | INT | Horario planificado | NOT NULL, FK |
| aula_id | INT | Aula donde se impartió | NOT NULL, FK |
| curso_profesor_id | INT | Profesor que impartió la sesión | NOT NULL, FK |
| timestamp_alta | DATETIME | Inicio real de la sesión | NOT NULL |
| hora_inicio | TIME | Hora planificada de inicio | NOT NULL |
| hora_fin | TIME | Hora planificada de fin | NOT NULL |
| timestamp_baja | DATETIME | Fin real de la sesión | NULL |
| motivo_baja | VARCHAR(255) | Motivo de cancelación | NULL |
| notas_sesion | TEXT | Observaciones generales | NULL |
| notas_materia | TEXT | Contenido impartido | NULL |

**Índices:**
- PK: id

**Relaciones:**
- FK: horario_curso_id → HorarioCurso(id)
- FK: aula_id → Aula(id)
- FK: curso_profesor_id → Curso_Profesores(id)
- Es referenciada por: AnotacionesAlumnoSesion

---

### 3.14. Alumno

**Descripción:** Estudiantes matriculados en alguna academia.

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | INT | Identificador único del alumno | PK, AUTO_INCREMENT |
| academia_id | INT | Academia donde está matriculado | NOT NULL, FK |
| nombre | VARCHAR(100) | Nombre completo | NOT NULL |
| email | VARCHAR(120) | Email único del alumno | NOT NULL, UNIQUE |
| dni | VARCHAR(20) | DNI/NIE | NOT NULL |
| telefono | VARCHAR(20) | Teléfono de contacto | NOT NULL |
| fecha_nacimiento | DATE | Fecha de nacimiento | NOT NULL |
| direccion | VARCHAR(255) | Dirección postal | NOT NULL |
| nombre_tutor | VARCHAR(100) | Nombre del tutor legal | NULL |
| relaccion_tutor_alumno | VARCHAR(100) | Relación con el tutor | NULL |
| telefono_tutor | VARCHAR(20) | Teléfono del tutor | NULL |
| email_tutor | VARCHAR(120) | Email del tutor | NULL |

**Índices:**
- PK: id
- UK: email

**Relaciones:**
- FK: academia_id → Academia(id)
- Es referenciada por: Inscripcion, familias_alumnos, AnotacionesAlumnoSesion, Extractos, Movimientos_Extracto

---

### 3.15. Inscripcion

**Descripción:** Matriculación de un alumno en un curso específico.

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | INT | Identificador único de la inscripción | PK, AUTO_INCREMENT |
| alumno_id | INT | Alumno inscrito | NOT NULL, FK |
| curso_id | INT | Curso al que se inscribe | NOT NULL, FK |
| tarifa_id | INT | Tarifa aplicada | NOT NULL, FK |
| fecha_inicio | DATE | Fecha de inicio de la inscripción | NOT NULL |
| fecha_fin | DATE | Fecha de fin | NULL |
| motivo_baja | VARCHAR(255) | Razón de baja anticipada | NULL |

**Índices:**
- PK: id

**Relaciones:**
- FK: alumno_id → Alumno(id)
- FK: curso_id → Curso(id)
- FK: tarifa_id → Tarifa(id)
- Es referenciada por: AnotacionesAlumnoSesion, Extractos, Movimientos_Extracto

---

### 3.16. familias_alumnos

**Descripción:** Vínculos familiares entre alumnos para gestión de descuentos.

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id_familia_alumno | INT | Identificador único del vínculo | PK, AUTO_INCREMENT |
| id_alumno1 | INT | Primer alumno del vínculo | NOT NULL, FK |
| id_alumno2 | INT | Segundo alumno del vínculo | NOT NULL, FK |
| relacion_familiar | VARCHAR(100) | Tipo de relación | NOT NULL |

**Valores de relacion_familiar:**
- Hermano/a
- Primo/a
- Otro familiar

**Índices:**
- PK: id_familia_alumno

**Relaciones:**
- FK: id_alumno1 → Alumno(id)
- FK: id_alumno2 → Alumno(id)

---

### 3.17. AnotacionesAlumnoSesion

**Descripción:** Anotaciones sobre alumnos durante las sesiones de clase.

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | INT | Identificador único de la anotación | PK, AUTO_INCREMENT |
| sesion_id | INT | Sesión donde se registra | NOT NULL, FK |
| inscripcion_id | INT | Inscripción del alumno | NOT NULL, FK |
| curso_id | INT | Curso (desnormalizado) | NOT NULL, FK |
| curso_profesor_id | INT | Profesor que anota | NOT NULL, FK |
| alumno_id | INT | Alumno anotado | NOT NULL, FK |
| tipo_anotacion | ENUM | Tipo de anotación | NOT NULL |
| texto | VARCHAR(255) | Contenido de la anotación | NULL |
| timestamp_alta | DATETIME | Fecha de creación | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| timestamp_baja | DATETIME | Fecha de eliminación lógica | NULL |
| motivo_baja | VARCHAR(255) | Razón de la eliminación | NULL |

**Valores ENUM tipo_anotacion:**
- Ausencia: El alumno no asistió
- Evaluacion: Comentarios sobre su progreso
- Comportamiento: Observaciones de conducta

**Índices:**
- PK: id

**Relaciones:**
- FK: sesion_id → Sesion(id)
- FK: inscripcion_id → Inscripcion(id)
- FK: curso_id → Curso(id)
- FK: curso_profesor_id → Curso_Profesores(id)
- FK: alumno_id → Alumno(id)

---

### 3.18. Extractos

**Descripción:** Estados de cuenta mensuales por inscripción para facturación.

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | INT | Identificador único del extracto | PK, AUTO_INCREMENT |
| inscripcion_id | INT | Inscripción facturada | NOT NULL, FK |
| curso_id | INT | Curso (desnormalizado) | NOT NULL, FK |
| alumno_id | INT | Alumno (desnormalizado) | NOT NULL, FK |
| numero_extracto | INT | Número correlativo | NOT NULL |
| saldo_ingreso_cuenta_anterior | FLOAT | Saldo del mes anterior | NULL |
| estado_liquidacion_extracto | ENUM | Estado del cobro | NOT NULL |
| fecha_alta | DATETIME | Fecha de generación | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| fecha_ini_periodo | DATE | Inicio del periodo facturado | NOT NULL |
| fecha_fin_periodo | DATE | Fin del periodo facturado | NOT NULL |
| id_dcto_tarifa_1 | INT | Primer descuento aplicado | NULL |
| motivo_dcto1 | VARCHAR(255) | Descripción del descuento 1 | NULL |
| tipo_dcto_1 | CHAR(1) | Tipo ('P' o 'F') | NULL |
| porcentaje_dcto_1 | FLOAT | Porcentaje si tipo='P' | NULL |
| importe_descuento_1 | FLOAT | Importe descontado | NULL |
| id_dcto_tarifa_2 | INT | Segundo descuento aplicado | NULL |
| motivo_dcto2 | VARCHAR(255) | Descripción del descuento 2 | NULL |
| tipo_dcto_2 | CHAR(1) | Tipo ('P' o 'F') | NULL |
| porcentaje_dcto_2 | FLOAT | Porcentaje si tipo='P' | NULL |
| importe_descuento_2 | FLOAT | Importe descontado | NULL |
| id_dcto_tarifa_3 | INT | Tercer descuento aplicado | NULL |
| motivo_dcto3 | VARCHAR(255) | Descripción del descuento 3 | NULL |
| tipo_dcto_3 | CHAR(1) | Tipo ('P' o 'F') | NULL |
| porcentaje_dcto_3 | FLOAT | Porcentaje si tipo='P' | NULL |
| importe_descuento_3 | FLOAT | Importe descontado | NULL |
| importe_cuota | FLOAT | Cuota base del curso | NOT NULL |
| total_descuentos | FLOAT | Suma de todos los descuentos | NOT NULL |
| importe_cuota_mensual_sin_descuentos | FLOAT | Cuota sin descuentos | NOT NULL |
| importe_cuota_mensual_con_descuentos | FLOAT | Cuota final con descuentos | NOT NULL |
| importe_exceso_ingresos | FLOAT | Exceso pagado | NULL |
| total_importe_a_cobrar | FLOAT | Importe final a cobrar | NOT NULL |

**Valores ENUM estado_liquidacion_extracto:**
- '1': Pendiente de liquidación
- '2': Liquidado

**Índices:**
- PK: id

**Relaciones:**
- FK: inscripcion_id → Inscripcion(id)
- FK: curso_id → Curso(id)
- FK: alumno_id → Alumno(id)
- Es referenciada por: Movimientos_Extracto

---

### 3.19. Movimientos_Extracto

**Descripción:** Movimientos financieros (ingresos y anulaciones) de cada extracto.

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | INT | Identificador único del movimiento | PK, AUTO_INCREMENT |
| inscripcion_id | INT | Inscripción (desnormalizado) | NOT NULL, FK |
| curso_id | INT | Curso (desnormalizado) | NOT NULL, FK |
| alumno_id | INT | Alumno (desnormalizado) | NOT NULL, FK |
| extracto_id | INT | Extracto al que pertenece | NOT NULL, FK |
| fecha_movimiento | DATE | Fecha del movimiento | NOT NULL |
| tipo_movimiento | ENUM | Tipo de operación | NOT NULL |
| estado_liquidacion_movimiento | ENUM | Estado del cobro | NOT NULL |
| importe | FLOAT | Importe del movimiento | NOT NULL |
| descripcion_movimiento | VARCHAR(255) | Descripción del concepto | NULL |
| metodo_pago | VARCHAR(50) | Forma de pago | NOT NULL |
| indicador_movimiento_anulado | ENUM | Si está anulado | NOT NULL, DEFAULT 'N' |

**Valores ENUM tipo_movimiento:**
- Ingreso: Pago realizado
- Anulacion_Ingreso: Reverso de un pago

**Valores ENUM estado_liquidacion_movimiento:**
- '1': Pendiente de cobro
- '2': Cobrado

**Valores de metodo_pago:**
- Efectivo
- Tarjeta
- Transferencia
- Domiciliación bancaria

**Valores ENUM indicador_movimiento_anulado:**
- 'S': Movimiento anulado
- 'N': Movimiento válido

**Índices:**
- PK: id

**Relaciones:**
- FK: inscripcion_id → Inscripcion(id)
- FK: curso_id → Curso(id)
- FK: alumno_id → Alumno(id)
- FK: extracto_id → Extractos(id)

---

### 3.20. TrabajadorVirtual

**Descripción:** Asistentes virtuales basados en IA para atención automatizada.

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| id | INT | Identificador único del trabajador virtual | PK, AUTO_INCREMENT |
| academia_id | INT | Academia propietaria | NOT NULL, FK |
| nombre | VARCHAR(100) | Nombre del asistente | NOT NULL |
| foto | VARCHAR(255) | URL de la imagen del avatar | NULL |
| descripcion | TEXT | Descripción de capacidades | NULL |
| apikey | VARCHAR(255) | Clave API para servicios IA | NULL |
| es_administrativo_virtual | BOOLEAN | Si es administrativo (vs docente) | NOT NULL |

**Índices:**
- PK: id

**Relaciones:**
- FK: academia_id → Academia(id)

---

## 4. RESUMEN DE RELACIONES

**Total de tablas:** 20

**Relaciones principales:**
- Academia (1) → (N) Tarifa, Aula, Curso, Usuario, Alumno, TrabajadorVirtual
- Rol_Usuario (1) → (N) Usuario
- Usuario (1) → (N) UserLoginLog, RefreshToken, PasswordResetToken, Curso_Profesores
- Tarifa (1) → (N) Descuentos_tarifa, Curso, Inscripcion
- Curso (1) → (N) HorarioCurso, Curso_Profesores, Inscripcion
- Aula (1) → (N) HorarioCurso, Sesion
- HorarioCurso (1) → (N) Sesion
- Curso_Profesores (1) → (N) Sesion, AnotacionesAlumnoSesion
- Alumno (1) → (N) Inscripcion, AnotacionesAlumnoSesion, Extractos, Movimientos_Extracto
- Alumno (N) ← → (N) Alumno (a través de familias_alumnos)
- Inscripcion (1) → (N) AnotacionesAlumnoSesion, Extractos, Movimientos_Extracto
- Sesion (1) → (N) AnotacionesAlumnoSesion
- Extractos (1) → (N) Movimientos_Extracto
- RefreshToken (1) → (1) RefreshToken (auto-referencia para rotación)

---

**FIN DEL DOCUMENTO**
