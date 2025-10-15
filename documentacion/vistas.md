# Documentación de Vistas

Este documento describe únicamente pantallas y navegación. El alcance funcional y reglas de negocio están en `alcancefuncional.md`.

## Login (pantalla inicial)
Descripción visual:
- Fondo con imagen a pantalla completa.
- Contenedor centrado con campos de email y contraseña (toggle mostrar/ocultar).
- Checkbox “Recordar usuario”.
- Enlace “¿Olvidaste la contraseña?”.
- Botón “Acceder”.

Comportamiento:
- Al pulsar “Acceder” se valida credenciales contra el API.
- Si es correcto, se navega SIEMPRE a la Vista de Chat (app chat-céntrica) y se obtiene el mensaje de bienvenida del chat.
- Si falla, mostrar mensaje de error.

## Vista de Chat (único worker)
Pantalla de conversación con el asistente virtual único. Tras login, se solicita un mensaje de bienvenida al backend y en la vista ya se verá la bienvenida que nos da el asistente virtual.

Características UI:
- Barra superior: botón hamburguesa (menú lateral), título “Chat”, avatar/nombre del asistente, indicador de estado (“En línea”).
- Zona central: burbujas de mensajes (usuario a la derecha, asistente a la izquierda), marcas de hora, indicador “Escribiendo…”.
- Barra inferior: campo de texto “Escribe tu mensaje…”, botón enviar y botón voz-a-texto (opcional).

Render de respuestas (envelope):
- Mostrar `message` (texto humano de la IA) destacado sobre el bloque de resultados.
- Si `data.type` es uno de [usuarios, academias, cursos, alumnos, profesores], renderizar `items` como lista con título/subtítulo; cada ítem puede tener acción “Ver detalle” o “Abrir en chat”.
- Si `data.pagination.has_more == true`, mostrar botón “Siguiente página”.
- `suggestions` se representan como chips/botones bajo el input; al pulsar, se envía el texto sugerido.
- En errores (`status = error`), mostrar banner/diálogo con `error.details` y permitir reintentar.

Estados:
- Cargando, Error, Vacío, Datos.

## Menú Lateral (navegación)
Opciones (alineadas con permisos por rol):
- Ver perfil (todos).
- Ver academias (solo Administrador de plataforma).
- Ver calendario, Ver cursos, Ver alumnos (Administrador de academia y Profesor). Administrador de plataforma debe seleccionar academia antes de acceder.
- Ver usuarios (Administrador de academia: vinculados a su academia; Administrador de plataforma: todos, con filtro por academia).
- Chat (todos).
- Ajustes, Cerrar sesión.

Notas:
- Un único asistente virtual (no se listan múltiples workers).
- Selección de academia bajo demanda (solo Admin_plataforma):
	- El menú lateral siempre se muestra completo con sus opciones visibles.
	- Al pulsar opciones que requieren contexto de academia (p. ej., Calendario, Cursos, Alumnos), si no hay una academia en memoria se abre un popup de selección de academias.
	- Tras seleccionar, se persiste la academia en memoria (DataStore) y se navega a la vista correspondiente.
	- Acción adicional en el drawer: “Cambiar academia” para actualizar o limpiar la selección cuando se desee.

## Vista de Academias
Listado de academias (visible solo para Administrador de plataforma).

Componentes UI:
- Barra superior con hamburguesa y acceso rápido al Chat.
- Lista vertical de academias.
- Indicador de carga y estados de error.

Acciones:
- Seleccionar una academia establece el contexto. Opción para “Abrir chat para esta academia”.
  
Nota:
- Esta vista sirve para explorar o cambiar manualmente la academia, pero la selección también se realiza on‑demand mediante el popup cuando una opción del menú lo requiera.

## Vista de Calendario
Calendario del día en la academia en contexto.

Componentes UI:
- Barra superior con hamburguesa, nombre de la academia y acceso al Chat.
- Fecha actual (“Día de la semana, DD de Mes de YYYY”), navegación (día anterior/siguiente), selector de fecha.
- Lista vertical de franjas horarias con tarjetas de cursos (scroll horizontal por franja si hay múltiples).
- Indicadores de carga y error.

Restricciones:
- Administrador de academia y Profesor: acceso directo a su academia.
- Administrador de plataforma: si no hay academia en memoria al acceder, se abre el popup de selección; tras seleccionar, se muestra el calendario de esa academia.

## Popup de Selección de Academia (on‑demand)
Cuándo aparece:
- Si el usuario es Admin_plataforma y accede a una vista que requiere academia y aún no hay academia en memoria.
- Cuando el usuario elige “Cambiar academia” en el menú lateral.

UI:
- Popup (modal o full‑screen dialog) con buscador (por nombre/código/ciudad) y filtros básicos.
- Listado de academias (paginado si aplica). Acción “Seleccionar” por ítem.

Comportamiento:
- Confirmar: persiste la academia (DataStore) y reintenta la navegación original.
- Cancelar: cierra el popup y permanece en la pantalla actual (normalmente Chat).

Persistencia:
- Se guarda en DataStore (academia_id). Se puede limpiar al pulsar “Cambiar academia”.

## Buenas prácticas (UI/UX y arquitectura)
- Compose + MVVM con un único Activity y navegación por rutas.
- Estados de UI claros (cargando/error/vacío/datos). Manejo de reintentos.
- Accesibilidad: ContentDescription, contraste, tamaños de toque adecuados.
- i18n: cadenas en recursos; soporte a pluralización y fechas locales.
- Seguridad: no mostrar tokens; en dev se permite HTTP 10.0.2.2, en prod HTTPS.


