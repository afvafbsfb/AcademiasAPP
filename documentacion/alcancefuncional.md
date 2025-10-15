# Documentación Funcional

## Nombre de la aplicación
Academia

## Descripción general
Aplicación para gestión de academias con autenticación de usuarios (email/contraseña) y un asistente virtual (worker digital) como canal principal de interacción. El usuario opera dentro del ámbito de una academia concreta según su rol, excepto el rol Admin_plataforma que puede obtener informacion de cualqueir academia. No existen usuarios “alumnos” en este proyecto.  Los usuarios estan vinculado a una academia excepto los Admin_plataforma porque éstos pueden administrar cualqueir plataforma.

## Roles y permisos
1. Administrador de plataforma
   - Puede administrar cualquier academia (crear/editar/borrar academias) y gestionar usuarios a nivel global.
   - Para operar sobre recursos dependientes de academia (calendario, cursos, alumnos, usuarios vinculados), debe seleccionar previamente una academia como contexto de trabajo.

2. Administrador de academia
   - Acceso completo a su academia: gestión de usuarios (crear/editar/eliminar/asignar roles), cursos (crear/editar/eliminar/asignar profesores), aulas (crear/editar/eliminar/asignar a cursos), horarios, pagos, matrículas, informes, estadísticas, notificaciones y configuración.
   - Puede realizar todas las funciones de un profesor. En academias pequeñas, el admin de academia puede impartir cursos.

3. Profesor de academia
   - Acceso restringido a su academia y a los cursos en los que participa.
   - Puede consultar información de su academia, gestionar contenido de sus cursos (crear sesiones, añadir anotaciones, consultar alumnos/notas/progreso) y realizar anotaciones de: comportamiento, aprendizaje, evaluación continua y faltas de asistencia.

## Interacción principal (chat con un único worker)
- Existe un único worker digital (global) que centraliza la interacción por chat.
- Personaliza respuestas y acciones según el rol del usuario y la academia en contexto.
- Las respuestas siguen un contrato JSON estandarizado (envelope) proporcionado por el backend de chat.

## Multi-academia y aislamiento de datos
- Cada academia posee su propio conjunto de usuarios, cursos y datos.
- El acceso siempre se limita al ámbito permitido por el rol del usuario.
- El worker aplica control de acceso para evitar fugas entre academias.

## Autenticación y autorización
- Inicio de sesión con email y contraseña contra el API de academias.
- Tras autenticación, el usuario recibe un token de acceso que determina sus permisos y el ámbito de academia.
- El modelo de autorización se aplica en el backend y en el worker, que sólo permite operaciones dentro del ámbito y rol del usuario.

## Consideraciones de seguridad y cumplimiento
- Los tokens no deben exponerse en logs ni persistirse en claro.  Solo se permite en fase de pruebas.
- En producción el tráfico debe ser HTTPS; en desarrollo y pruebas locales se permite HTTP contra entornos locales.

---
Nota: Este documento define alcance y reglas funcionales. No describe vistas, navegación ni menús (eso se recoge en `vistas.md`).