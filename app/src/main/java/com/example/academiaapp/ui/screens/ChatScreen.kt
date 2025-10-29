package com.example.academiaapp.ui.screens

import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.academiaapp.AcademiaApp
import com.example.academiaapp.ui.viewmodels.ChatViewModel
import com.example.academiaapp.ui.viewmodels.ChatViewModelFactory
import com.example.academiaapp.ui.components.AppTopBar
import com.example.academiaapp.ui.components.SuggestionClarificationDialog
import com.example.academiaapp.ui.utils.MenuBuilder
import com.example.academiaapp.data.remote.dto.needsClarification
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ✅ NUEVO: Composable para los puntos suspensivos animados estilo WhatsApp con efecto de ola
@Composable
private fun TypingIndicator(
    modifier: Modifier = Modifier,
    dotColor: Color = Color(0xFF555555)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    // Animación para cada punto con diferentes delays - efecto de ola (translateY)
    val offsetY1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )

    val offsetY2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )

    val offsetY3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .offset(y = offsetY1.dp)
                .background(dotColor, shape = CircleShape)
        )
        Box(
            modifier = Modifier
                .size(8.dp)
                .offset(y = offsetY2.dp)
                .background(dotColor, shape = CircleShape)
        )
        Box(
            modifier = Modifier
                .size(8.dp)
                .offset(y = offsetY3.dp)
                .background(dotColor, shape = CircleShape)
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatScreen(fromLogin: Boolean = false, navController: NavController? = null) {
    val app = LocalContext.current.applicationContext as AcademiaApp
    val factory = remember { ChatViewModelFactory(app.container.chatRepository, app.container.session) }
    // Use Activity as owner so the ChatViewModel survives navigation and doesn't get recreated (preserves messages)
    val activityOwner = LocalActivity.current as? ViewModelStoreOwner
    val owner: ViewModelStoreOwner = activityOwner
        ?: LocalViewModelStoreOwner.current
        ?: error("No ViewModelStoreOwner found")
    val vm: ChatViewModel = viewModel(viewModelStoreOwner = owner, factory = factory)
    val ui by vm.ui.collectAsState()
    // Si venimos desde el login, solicitar el mensaje de bienvenida
    LaunchedEffect(fromLogin) {
        if (fromLogin && ui.messages.isEmpty()) vm.loadWelcome()
    }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var detailsItem by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var input by remember { mutableStateOf("") }
    
    // ✅ NUEVO: Estado para el diálogo de aclaración de sugerencias
    var showClarificationDialog by remember { mutableStateOf(false) }
    var clarificationSuggestion by remember { mutableStateOf<com.example.academiaapp.data.remote.dto.Suggestion?>(null) }
    var clarificationMessageIndex by remember { mutableStateOf(-1) }

    // ✅ Scroll automático al final cuando llegan mensajes nuevos O cuando cambia el estado de loading
    LaunchedEffect(ui.messages.size, ui.loading) {
        if (ui.messages.isNotEmpty() || ui.loading) {
            coroutineScope.launch {
                // Pequeño delay para permitir que se renderice la burbuja de loading
                delay(100)
                val targetIndex = if (ui.loading) ui.messages.size else ui.messages.lastIndex
                if (targetIndex >= 0) {
                    listState.animateScrollToItem(targetIndex)
                }
            }
        }
    }

    // Drawer state and session (simple usage)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val session = app.container.session
    val userName by session.name.collectAsState(initial = "")
    val userRole by session.role.collectAsState(initial = "")
    val academiaName by session.academiaName.collectAsState(initial = null)

    val isDark = isSystemInDarkTheme()
    val chatBackground = if (isDark) Color(0xFF0B141A) else Color(0xFFECE5DD)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Header: usar color primario y respetar status bar; limitar a su contenido para evitar que ocupe todo el drawer
                Column(
                    Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                            .padding(16.dp)
                    ) {
                        val displayName = (userName ?: "").ifBlank { "Usuario" }
                        Text(text = displayName, color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                        val roleText = if (userRole.equals("admin_plataforma", ignoreCase = true)) {
                            "Administrador plataforma"
                        } else {
                            academiaName ?: "Academia vinculada"
                        }
                        Text(text = roleText, color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                    }

                    Spacer(Modifier.height(8.dp))
                }

                // ✅ NUEVO: Generar menú dinámico según el rol del usuario
                val menuOptions = remember(userRole) {
                    MenuBuilder.getMenuOptions(userRole ?: "")
                }

                // Renderizar las opciones del menú
                menuOptions.forEach { option ->
                    NavigationDrawerItem(
                        label = { Text(option.label) },
                        icon = { Icon(imageVector = option.icon, contentDescription = "") },
                        selected = option.id == "chat_libre",
                        onClick = {
                            Log.d("NavDrawer", "Clicked item: ${option.id} - ${option.label}")
                            coroutineScope.launch {
                                // Cerrar el drawer
                                try { drawerState.close() } catch (_: Exception) {}

                                when (option.id) {
                                    "chat_libre" -> {
                                        // Chat libre: navegar al chat sin mensaje pre-cargado
                                        navController?.let { nc ->
                                            nc.navigate("chat") {
                                                launchSingleTop = true
                                                restoreState = true
                                                popUpTo(nc.graph.startDestinationId) { saveState = true }
                                            }
                                        }
                                    }
                                    "cerrar_sesion" -> {
                                        // Cerrar sesión: limpiar todo y volver a login
                                        try { vm.reset() } catch (_: Exception) { }
                                        try { app.container.session.clear() } catch (_: Exception) { }
                                        try { app.container.chatRepository.clearWelcomeCache() } catch (_: Exception) { }
                                        navController?.navigate("login") { popUpTo("login") { inclusive = true } }
                                    }
                                    else -> {
                                        // Para todas las demás opciones: auto-enviar mensaje al chat
                                        option.chatMessage?.let { message ->
                                            // Asegurarnos de estar en el chat
                                            navController?.let { nc ->
                                                nc.navigate("chat") {
                                                    launchSingleTop = true
                                                    restoreState = true
                                                    popUpTo(nc.graph.startDestinationId) { saveState = true }
                                                }
                                            }
                                            // Pequeño delay para que la navegación se complete
                                            delay(100)
                                            // Enviar mensaje con contexto
                                            vm.sendMessageWithContext(message, option.contextData)
                                        }
                                    }
                                }
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                            selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondaryContainer,
                            unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            containerColor = chatBackground,
            topBar = {
                Log.d("ChatScreen", "Scaffold topBar lambda invoked")
                AppTopBar(
                    title = "Chat asistente virtual",
                    showNavIcon = true,
                    onNavClick = { coroutineScope.launch { drawerState.open() } },
                    // usar color primario del tema para la cabecera
                    backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                    centerTitle = false
                )
            },
            bottomBar = {
                Surface(color = if (isDark) Color(0xFF1F2C34) else Color(0xFFE8E8E8)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Escribe un mensaje…") },
                            enabled = !ui.loading,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Send,
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Text
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (!ui.loading && input.isNotBlank()) {
                                        vm.sendMessage(input)
                                        input = ""
                                    }
                                }
                            ),
                            maxLines = 1,
                            singleLine = true
                        )

                        // IconButton con icono Send y efecto visual
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()

                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(48.dp)
                                .background(
                                    color = when {
                                        isPressed && !ui.loading && input.isNotBlank() ->
                                            androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                        else -> Color.Transparent
                                    },
                                    shape = CircleShape
                                )
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = androidx.compose.material.ripple.rememberRipple(bounded = true, radius = 24.dp),
                                    enabled = !ui.loading && input.isNotBlank()
                                ) {
                                    if (!ui.loading && input.isNotBlank()) {
                                        vm.sendMessage(input)
                                        input = ""
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviar mensaje",
                                tint = if (!ui.loading && input.isNotBlank()) {
                                    androidx.compose.material3.MaterialTheme.colorScheme.primary
                                } else {
                                    Color.Gray
                                },
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            // Detectar si estamos realmente al final del scroll (último elemento completamente visible)
            val showScrollToBottom by remember {
                derivedStateOf {
                    val layoutInfo = listState.layoutInfo
                    val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                    val totalItems = ui.messages.size

                    // Mostrar botón solo si no estamos viendo el último elemento O si no está completamente visible
                    if (totalItems == 0) {
                        false
                    } else {
                        lastVisibleItem?.index != totalItems - 1
                    }
                }
            }

            // Estado para mostrar/ocultar la scrollbar
            var showScrollbar by remember { mutableStateOf(false) }

            // Detectar cuando hay scroll activo
            LaunchedEffect(listState.isScrollInProgress) {
                if (listState.isScrollInProgress) {
                    showScrollbar = true
                } else {
                    // Ocultar después de 1.5 segundos de inactividad
                    delay(1500)
                    showScrollbar = false
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // ✅ MEJORADO: Si no hay mensajes y está cargando, mostrar indicador centrado con puntos animados
                if (ui.messages.isEmpty() && ui.loading) {
                    // Indicador de carga centrado para el mensaje de bienvenida
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TypingIndicator(dotColor = Color(0xFF555555))
                            }
                        }
                    }
                } else {
                    // Vista normal del chat con mensajes
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(ui.messages) { index, m ->
                            val isAssistant = m.role == "assistant"
                            val showExtras = isAssistant && (m.items.isNotEmpty() || m.suggestions.isNotEmpty())

                            val userBubbleColor = Color(0xFFDCF8C6)
                            val otherBubbleColor = Color(0xFFFFFFFF)
                            val bubbleBg = if (!isAssistant) userBubbleColor else otherBubbleColor
                            val textColor = Color(0xFF000000)
                            val align = if (!isAssistant) Alignment.CenterEnd else Alignment.CenterStart
                            val bubbleShape = if (isAssistant) {
                                RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomEnd = 12.dp,
                                    bottomStart = 4.dp
                                )
                            } else {
                                RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomStart = 12.dp,
                                    bottomEnd = 4.dp
                                )
                            }

                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = align) {
                                BoxWithConstraints(Modifier.fillMaxWidth()) {
                                    val hasTable = showExtras && m.items.isNotEmpty()
                                    val maxBubbleWidth = if (hasTable) this.maxWidth * 0.95f else this.maxWidth * 0.8f
                                    Card(
                                        modifier = Modifier
                                            .align(align)
                                            .widthIn(max = maxBubbleWidth),
                                        colors = CardDefaults.cardColors(containerColor = bubbleBg),
                                        shape = bubbleShape
                                    ) {
                                        Column(
                                            Modifier
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(m.text, color = textColor)
                                            if (showExtras) {
                                                if (m.items.isNotEmpty()) {
                                                    // Si es formulario de alta, renderizar formulario editable CON botones
                                                    if (m.type == "formulario_alta_alumno") {
                                                        AlumnoAltaForm(
                                                            formSpec = m.items.firstOrNull() ?: emptyMap(),
                                                            onCancel = { vm.sendMessageWithContext("cancelar alta", mapOf("screen" to "alumnos")) },
                                                            onSubmit = { formData ->
                                                                // Enviar acción de submit al mock mediante contexto
                                                                vm.sendMessageWithContext("alta nuevo alumno", mapOf(
                                                                    "screen" to "alumnos",
                                                                    "action" to "submit_alta",
                                                                    "form_data" to formData
                                                                ))
                                                            }
                                                        )
                                                    } else if (m.type == "sesiones_dia") {
                                                        // Renderizador especial para clases del día (cards híbridas)
                                                        SesionesDelDiaCards(items = m.items)
                                                    } else {
                                                        val typeLabel = m.type?.let { "Resultados (${it})" } ?: "Resultados:"
                                                        Text(typeLabel, color = textColor)
                                                        CompactList(items = m.items, summaryFields = m.summaryFields, onOpenDetails = { detailsItem = it })
                                                    }
                                                }
                                                // No mostrar sugerencias si es formulario (ya tiene sus propios botones)
                                                if (m.suggestions.isNotEmpty() && m.type != "formulario_alta_alumno") {
                                                    // ✅ Mejora 5: Ordenar sugerencias - paginación primero y en la misma línea
                                                    val (paginationSuggestions, otherSuggestions) = m.suggestions.partition {
                                                        it.type.equals("Paginacion", ignoreCase = true)
                                                    }

                                                    // Ordenar las de paginación: "prev" primero, "next" después
                                                    val sortedPagination = paginationSuggestions.sortedBy { suggestion ->
                                                        when (suggestion.pagination?.direction) {
                                                            "prev" -> 0
                                                            "next" -> 1
                                                            else -> 2
                                                        }
                                                    }

                                                    Column(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        // Mostrar sugerencias de paginación como botones destacados en una fila
                                                        if (sortedPagination.isNotEmpty()) {
                                                            // Etiqueta opcional para claridad
                                                            Text(
                                                                text = "Navegación:",
                                                                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                                                                color = Color(0xFF666666),
                                                                modifier = Modifier.padding(bottom = 4.dp)
                                                            )
                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .horizontalScroll(rememberScrollState()),
                                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                            ) {
                                                                sortedPagination.forEach { suggestion ->
                                                                    Button(
                                                                        onClick = {
                                                                            if (!ui.loading && m.suggestionsEnabled) {
                                                                                vm.disableSuggestionsForMessage(index)
                                                                                vm.sendMessage(suggestion.displayText)
                                                                            }
                                                                        },
                                                                        enabled = !ui.loading && m.suggestionsEnabled,
                                                                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                                                            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                                                                            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondaryContainer,
                                                                            disabledContainerColor = Color.LightGray.copy(alpha = 0.3f),
                                                                            disabledContentColor = Color.DarkGray
                                                                        ),
                                                                        shape = RoundedCornerShape(20.dp),
                                                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                                                    ) {
                                                                        // Icono según dirección
                                                                        val icon = when (suggestion.pagination?.direction) {
                                                                            "prev" -> Icons.AutoMirrored.Filled.KeyboardArrowLeft
                                                                            "next" -> Icons.AutoMirrored.Filled.KeyboardArrowRight
                                                                            else -> null
                                                                        }

                                                                        if (icon != null && suggestion.pagination?.direction == "prev") {
                                                                            Icon(
                                                                                imageVector = icon,
                                                                                contentDescription = null,
                                                                                modifier = Modifier.size(18.dp)
                                                                            )
                                                                            Spacer(Modifier.width(4.dp))
                                                                        }

                                                                        Text(
                                                                            text = suggestion.displayText,
                                                                            maxLines = 1,
                                                                            overflow = TextOverflow.Ellipsis
                                                                        )

                                                                        if (icon != null && suggestion.pagination?.direction == "next") {
                                                                            Spacer(Modifier.width(4.dp))
                                                                            Icon(
                                                                                imageVector = icon,
                                                                                contentDescription = null,
                                                                                modifier = Modifier.size(18.dp)
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        // Mostrar otras sugerencias como chips, una por línea
                                                        if (otherSuggestions.isNotEmpty()) {
                                                            // Etiqueta para claridad si hay paginación
                                                            if (sortedPagination.isNotEmpty()) {
                                                                Text(
                                                                    text = "Otras opciones:",
                                                                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                                                                    color = Color(0xFF666666),
                                                                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                                                                )
                                                            }

                                                            otherSuggestions.forEach { suggestion ->
                                                                Row(
                                                                    modifier = Modifier.fillMaxWidth()
                                                                ) {
                                                                    SuggestionChip(
                                                                        onClick = {
                                                                            if (!ui.loading && m.suggestionsEnabled) {
                                                                                // ✅ Verificar si necesita aclaración (lógica determinista)
                                                                                if (suggestion.needsClarification()) {
                                                                                    // Mostrar diálogo para pedir más información
                                                                                    clarificationSuggestion = suggestion
                                                                                    clarificationMessageIndex = index
                                                                                    showClarificationDialog = true
                                                                                } else {
                                                                                    // Enviar directamente (Paginacion o Generica tipo listado)
                                                                                    vm.disableSuggestionsForMessage(index)
                                                                                    vm.sendMessage(suggestion.displayText)
                                                                                }
                                                                            }
                                                                        },
                                                                        enabled = !ui.loading && m.suggestionsEnabled,
                                                                        label = {
                                                                            Text(
                                                                                suggestion.displayText,
                                                                                maxLines = 1,
                                                                                overflow = TextOverflow.Ellipsis
                                                                            )
                                                                        },
                                                                        colors = androidx.compose.material3.SuggestionChipDefaults.suggestionChipColors(
                                                                            containerColor = Color(0xFF81C784),
                                                                            labelColor = Color.White,
                                                                            disabledContainerColor = Color.LightGray.copy(alpha = 0.3f),
                                                                            disabledLabelColor = Color.DarkGray
                                                                        ),
                                                                        border = null,
                                                                        shape = RoundedCornerShape(20.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ✅ Burbuja de "Procesando respuesta..." con puntos animados cuando está cargando Y ya hay mensajes
                        if (ui.loading && ui.messages.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Card(
                                        modifier = Modifier.widthIn(max = 250.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                                        shape = RoundedCornerShape(
                                            topStart = 12.dp,
                                            topEnd = 12.dp,
                                            bottomEnd = 12.dp,
                                            bottomStart = 4.dp
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            TypingIndicator(dotColor = Color(0xFF555555))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Scrollbar vertical
                if (showScrollbar && ui.messages.isNotEmpty()) {
                    val scrollbarHeight = remember { derivedStateOf {
                        val totalItems = ui.messages.size
                        val visibleItems = listState.layoutInfo.visibleItemsInfo.size
                        if (totalItems > 0) (visibleItems.toFloat() / totalItems) else 0f
                    } }

                    val scrollbarOffset = remember { derivedStateOf {
                        val totalItems = ui.messages.size
                        val firstVisible = listState.firstVisibleItemIndex
                        if (totalItems > 0) (firstVisible.toFloat() / totalItems) else 0f
                    } }

                    BoxWithConstraints(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .padding(end = 4.dp, top = 8.dp, bottom = 8.dp)
                    ) {
                        val maxHeight = this.maxHeight
                        val barHeight = (maxHeight * scrollbarHeight.value).coerceAtLeast(30.dp)
                        val barOffset = maxHeight * scrollbarOffset.value

                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(barHeight)
                                .padding(top = barOffset)
                                .background(
                                    color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(2.dp)
                                )
                                .alpha(if (showScrollbar) 0.6f else 0f)
                        )
                    }
                }

                // Botón flotante para bajar
                if (showScrollToBottom && ui.messages.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = {
                            val lastIndex = ui.messages.lastIndex
                            if (lastIndex >= 0) {
                                coroutineScope.launch { listState.animateScrollToItem(lastIndex) }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .size(48.dp),
                        shape = CircleShape,
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Bajar",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        if (detailsItem != null) {
            // ✅ Función auxiliar para formatear valores (eliminar decimales innecesarios)
            fun formatValue(value: Any?): String {
                return when (value) {
                    null -> "(vacío)"
                    is Number -> {
                        val doubleValue = value.toDouble()
                        // Si el número es entero (no tiene decimales), mostrarlo sin .0
                        if (doubleValue % 1.0 == 0.0) {
                            doubleValue.toLong().toString()
                        } else {
                            doubleValue.toString()
                        }
                    }
                    is Map<*, *> -> {
                        // NUEVO: Formatear objetos anidados (academia, curso, etc.)
                        val map = value as? Map<String, Any?>
                        when {
                            map == null -> "(vacío)"
                            map.containsKey("nombre") -> {
                                // Mostrar: "Academia Central (ID: 2)"
                                val nombre = map["nombre"] ?: "Sin nombre"
                                val id = map["id"]
                                // ✅ Formatear el ID para quitar .0 si es entero
                                val idFormatted = if (id is Number) {
                                    val doubleId = id.toDouble()
                                    if (doubleId % 1.0 == 0.0) doubleId.toLong().toString() else doubleId.toString()
                                } else {
                                    id?.toString() ?: ""
                                }
                                if (id != null) "$nombre (ID: $idFormatted)" else nombre.toString()
                            }
                            else -> {
                                // Fallback: mostrar pares clave-valor
                                map.entries.joinToString(", ") { "${it.key}: ${it.value}" }
                            }
                        }
                    }
                    else -> value.toString()
                }
            }

            ModalBottomSheet(onDismissRequest = { detailsItem = null }) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Título del modal
                    Text(
                        text = "Detalles del registro",
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    androidx.compose.material3.HorizontalDivider(
                        thickness = 1.dp,
                        color = Color(0xFFE0E0E0),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Mostrar cada campo en una tarjeta
                    val itemMap = detailsItem!!
                    itemMap.forEach { (k, v) ->
                        // ✅ NUEVO: Skip FKs redundantes cuando existe el objeto expandido
                        val isRedundantFK = k.endsWith("_id") && 
                                            itemMap.containsKey(k.removeSuffix("_id"))

                        if (!isRedundantFK) {
                            val formattedValue = formatValue(v)
                            // Si el campo es de tipo deuda/euros, agregar símbolo €
                            val displayValue = if (k.contains("deuda", ignoreCase = true) || k.contains("euros", ignoreCase = true)) {
                                if (formattedValue.isNotBlank() && formattedValue != "(vacío)") "${formattedValue}€" else formattedValue
                            } else {
                                formattedValue
                            }
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF5F5F5)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = k,
                                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF666666)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = displayValue,
                                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF212121)
                                )
                            }
                        }
                        } // cierre del if (!isRedundantFK)
                    }

                    Spacer(Modifier.height(16.dp))

                    // Botón para cerrar
                    Button(
                        onClick = { detailsItem = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Cerrar")
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
    
    // ✅ NUEVO: Diálogo de aclaración para sugerencias que necesitan más información
    if (showClarificationDialog && clarificationSuggestion != null) {
        SuggestionClarificationDialog(
            suggestion = clarificationSuggestion!!,
            onDismiss = {
                showClarificationDialog = false
                clarificationSuggestion = null
                clarificationMessageIndex = -1
            },
            onSend = { editedMessage ->
                // Deshabilitar las sugerencias del mensaje original
                if (clarificationMessageIndex >= 0) {
                    vm.disableSuggestionsForMessage(clarificationMessageIndex)
                }
                // Enviar el mensaje editado
                vm.sendMessage(editedMessage)
                // Cerrar el diálogo
                showClarificationDialog = false
                clarificationSuggestion = null
                clarificationMessageIndex = -1
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlumnoAltaForm(
    formSpec: Map<String, Any?>,
    onSubmit: (Map<String, Any?>) -> Unit,
    onCancel: () -> Unit
) {
    // Extraer cursos disponibles si vienen
    val cursos = (formSpec["cursos_disponibles"] as? List<Map<String, Any?>>) ?: emptyList()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") } // DD/MM/YYYY
    var direccion by remember { mutableStateOf("") }
    var cursoSeleccionado by remember { mutableStateOf<Int?>(null) }
    var cursoLabel by remember { mutableStateOf("Seleccionar curso") }
    var expandedCursos by remember { mutableStateOf(false) }

    // Validation states
    var nombreError by remember { mutableStateOf(false) }
    var telefonoError by remember { mutableStateOf(false) }
    var fechaError by remember { mutableStateOf(false) }

    // Diálogo de confirmación
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Focus requesters para cada campo obligatorio
    val nombreFocusRequester = remember { FocusRequester() }
    val telefonoFocusRequester = remember { FocusRequester() }
    val fechaFocusRequester = remember { FocusRequester() }
    
    // Función de validación
    fun validateAndShowDialog() {
        var ok = true
        var firstErrorField: FocusRequester? = null
        
        if (nombre.isBlank()) { 
            nombreError = true
            ok = false
            if (firstErrorField == null) firstErrorField = nombreFocusRequester
        }
        if (telefono.isBlank()) { 
            telefonoError = true
            ok = false
            if (firstErrorField == null) firstErrorField = telefonoFocusRequester
        }
        val fechaRegex = "^\\d{2}/\\d{2}/\\d{4}$".toRegex()
        if (!fechaRegex.matches(fecha)) { 
            fechaError = true
            ok = false
            if (firstErrorField == null) firstErrorField = fechaFocusRequester
        }

        if (!ok) {
            firstErrorField?.requestFocus()
            return
        }
        
        // Si validó bien, mostrar diálogo
        showConfirmDialog = true
    }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Title
        Text("Formulario de alta", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)

        // Nombre (required)
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it; if (it.isNotBlank()) nombreError = false },
            label = { Text("Nombre completo *") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(nombreFocusRequester)
                .background(if (nombreError) Color(0xFFFFE0B2) else Color.Transparent),
            singleLine = true,
            isError = nombreError
        )

        // Telefono (required)
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it; if (it.isNotBlank()) telefonoError = false },
            label = { Text("Teléfono *") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(telefonoFocusRequester)
                .background(if (telefonoError) Color(0xFFFFE0B2) else Color.Transparent),
            singleLine = true,
            isError = telefonoError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        // Fecha nacimiento (required) with simple mask
        OutlinedTextField(
            value = fecha,
            onValueChange = { input ->
                // Allow only digits and '/'
                val digits = input.filter { it.isDigit() }
                val masked = buildString {
                    for (i in digits.indices) {
                        append(digits[i])
                        if (i == 1 || i == 3) append('/')
                        if (i >= 7) break
                    }
                }
                fecha = masked
                if (masked.isNotBlank()) fechaError = false
            },
            label = { Text("Fecha de nacimiento (DD/MM/AAAA) *") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(fechaFocusRequester)
                .background(if (fechaError) Color(0xFFFFE0B2) else Color.Transparent),
            singleLine = true,
            isError = fechaError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // Email, DNI, Dirección (optional)
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = dni, onValueChange = { dni = it }, label = { Text("DNI") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())

        // Curso (optional) - Dropdown Menu
        if (cursos.isNotEmpty()) {
            ExposedDropdownMenuBox(
                expanded = expandedCursos,
                onExpandedChange = { expandedCursos = it }
            ) {
                OutlinedTextField(
                    value = cursoLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Curso (opcional)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCursos) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                
                ExposedDropdownMenu(
                    expanded = expandedCursos,
                    onDismissRequest = { expandedCursos = false }
                ) {
                    cursos.forEach { c ->
                        val id = (c["id"] as? Number)?.toInt()
                        val label = c["display_text"] as? String ?: ""
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                cursoSeleccionado = id
                                cursoLabel = label
                                expandedCursos = false
                            }
                        )
                    }
                }
            }
        }
        
        Spacer(Modifier.height(12.dp))
        
        // Botones estilizados como sugerencias
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Botón Alta
            Row(modifier = Modifier.fillMaxWidth()) {
                SuggestionChip(
                    onClick = { validateAndShowDialog() },
                    label = { Text("Alta") },
                    colors = androidx.compose.material3.SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Color(0xFF81C784),
                        labelColor = Color.White
                    ),
                    border = null,
                    shape = RoundedCornerShape(20.dp)
                )
            }
            
            // Botón Cancelar
            Row(modifier = Modifier.fillMaxWidth()) {
                SuggestionChip(
                    onClick = { onCancel() },
                    label = { Text("Cancelar") },
                    colors = androidx.compose.material3.SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Color(0xFF81C784),
                        labelColor = Color.White
                    ),
                    border = null,
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
    
    // Diálogo de confirmación
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar alta") },
            text = { Text("¿Has verificado todos los datos del alumno?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    // Build form map
                    val formMap = mapOf(
                        "nombre" to nombre,
                        "email" to email,
                        "dni" to dni,
                        "telefono" to telefono,
                        "fecha_nacimiento" to fecha,
                        "direccion" to direccion,
                        "curso_id" to cursoSeleccionado
                    )
                    onSubmit(formMap)
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
private fun CompactList(
    items: List<Map<String, Any?>>, 
    summaryFields: List<String>,
    onOpenDetails: (Map<String, Any?>) -> Unit
) {
    // ✅ NUEVO: Función para acceder a campos anidados con dot notation
    fun getNestedValue(item: Map<String, Any?>, path: String): Any? {
        if (!path.contains(".")) return item[path]
        
        val parts = path.split(".")
        var current: Any? = item
        
        for (part in parts) {
            current = (current as? Map<*, *>)?.get(part)
            if (current == null) break
        }
        
        return current
    }
    
    // ✅ Función auxiliar para formatear valores (eliminar decimales innecesarios)
    fun formatValue(value: Any?): String {
        return when (value) {
            null -> ""
            is Number -> {
                val doubleValue = value.toDouble()
                // Si el número es entero (no tiene decimales), mostrarlo sin .0
                if (doubleValue % 1.0 == 0.0) {
                    doubleValue.toLong().toString()
                } else {
                    doubleValue.toString()
                }
            }
            else -> value.toString()
        }
    }

    // Estado para controlar cuántos items mostrar (por defecto 5)
    var itemsToShow by remember { mutableStateOf(5) }
    val allItemsShown = itemsToShow >= items.size

    // Estado para el scroll de la LazyColumn de la tabla
    val tableListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Contador de registros
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mostrando ${itemsToShow.coerceAtMost(items.size)} de ${items.size} registros",
                style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                color = Color(0xFF555555)
            )
            if (!allItemsShown) {
                Text(
                    text = "${items.size - itemsToShow} más",
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                )
            }
        }

        // Cabecera de la tabla
        items.firstOrNull()?.let { firstItem ->
            val keys = firstItem.keys.toList()
            fun findKeyIgnoreCase(target: String): String? = keys.firstOrNull { it.equals(target, ignoreCase = true) }

            val summary = summaryFields.take(2)
            val (primaryKey, secondaryKey) = if (summary.isNotEmpty()) {
                // ✅ Usar directamente los paths (pueden contener dot notation)
                val first = summary.getOrNull(0)
                val second = summary.getOrNull(1)
                first to second
            } else {
                // ✅ MEJORADO: Heurística que excluye objetos anidados y campos _id
                // Filtrar claves: solo primitivas (no Maps), no terminan en _id
                val simpleKeys = keys.filter { key ->
                    val value = firstItem[key]
                    value !is Map<*, *> && !key.endsWith("_id", ignoreCase = true)
                }
                
                fun pick(vararg candidates: String): String? = 
                    candidates.firstOrNull { c -> simpleKeys.any { it.equals(c, ignoreCase = true) } }
                
                val primary = pick("descripcion", "nombre", "title", "name", "full_name", "displayName", "email")
                val secondary = pick("precio_base", "precio", "importe", "rol", "role", "estado", "status", 
                                    "ciudad", "city", "telefono", "phone", "codigo")
                
                primary to secondary
            }

            // Fila de cabecera
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                    )
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Formatear nombres de columnas: capitalizar y limpiar guiones bajos
                fun formatColumnName(name: String?): String {
                    if (name == null) return "Campo principal"
                    return name.replace("_", " ").split(" ").joinToString(" ") { word ->
                        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    }
                }
                
                Text(
                    text = formatColumnName(primaryKey),
                    modifier = Modifier.weight(0.6f),
                    style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text(
                    text = formatColumnName(secondaryKey) ?: "Detalle",
                    modifier = Modifier.weight(0.35f),
                    style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // Espacio para el icono de acción
                Spacer(Modifier.size(32.dp))
            }
        }

        // Calcular altura dinámica: crece con los registros hasta un máximo de 600dp
        val dynamicMaxHeight = remember(itemsToShow) {
            val estimatedRowHeight = 52.dp
            val headerHeight = 50.dp
            val calculatedHeight = headerHeight + (estimatedRowHeight * itemsToShow)
            // Crecimiento: desde 280dp hasta máximo 600dp
            calculatedHeight.coerceIn(280.dp, 600.dp)
        }

        LazyColumn(
            state = tableListState,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = dynamicMaxHeight),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            itemsIndexed(items.take(itemsToShow)) { index, item ->
                val keys = item.keys.toList()
                fun findKeyIgnoreCase(target: String): String? = keys.firstOrNull { it.equals(target, ignoreCase = true) }
                
                // ✅ NUEVO: valueOf con soporte para dot notation
                fun valueOf(path: String?): String? {
                    if (path == null) return null
                    
                    // Usar getNestedValue para soportar "academia.nombre"
                    val rawValue = getNestedValue(item, path)
                    val formatted = formatValue(rawValue)
                    
                    // Si el campo es de tipo deuda/euros, agregar símbolo €
                    return if (path.contains("deuda", ignoreCase = true) || path.contains("euros", ignoreCase = true)) {
                        if (formatted.isNotBlank()) "${formatted}€" else formatted
                    } else {
                        formatted
                    }
                }

                val summary = summaryFields.take(2)
                val (primaryKey, secondaryKey) = if (summary.isNotEmpty()) {
                    // ✅ Usar directamente el path de summary_fields (puede contener dot notation)
                    val first = summary.getOrNull(0)
                    val second = summary.getOrNull(1)
                    first to second
                } else {
                    // ✅ MEJORADO: Heurística que excluye objetos anidados y campos _id
                    val simpleKeys = keys.filter { key ->
                        val value = item[key]
                        value !is Map<*, *> && !key.endsWith("_id", ignoreCase = true)
                    }
                    
                    fun pick(vararg candidates: String): String? = 
                        candidates.firstOrNull { c -> simpleKeys.any { it.equals(c, ignoreCase = true) } }
                    
                    val primary = pick("descripcion", "nombre", "title", "name", "full_name", "displayName", "email")
                    val secondary = pick("precio_base", "precio", "importe", "rol", "role", "estado", "status", 
                                        "ciudad", "city", "telefono", "phone", "codigo")
                    
                    primary to secondary
                }

                val primary = valueOf(primaryKey) ?: item.values.mapNotNull { formatValue(it).takeIf { it.isNotBlank() } }.firstOrNull() ?: "(sin datos)"
                val secondary = valueOf(secondaryKey) ?: item.values.mapNotNull { formatValue(it).takeIf { it.isNotBlank() } }.distinct().drop(1).firstOrNull()

                // Colores alternos más profesionales: gris muy claro para pares, blanco para impares
                val rowColor = if (index % 2 == 0) Color(0xFFF5F5F5) else Color(0xFFFFFFFF)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(rowColor)
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = primary,
                        modifier = Modifier.weight(0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF212121)
                    )
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = secondary ?: "",
                        modifier = Modifier.weight(0.35f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = Color(0xFF757575)
                    )
                    IconButton(onClick = { onOpenDetails(item) }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Ver detalles",
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Separador entre filas (excepto la última)
                if (index < itemsToShow - 1) {
                    androidx.compose.material3.HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        thickness = 1.dp,
                        color = Color(0xFFE0E0E0)
                    )
                }
            }
        }

        // Botón para expandir la tabla si hay más items
        if (!allItemsShown) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        val previousCount = itemsToShow
                        // Mostrar 5 items más cada vez que se pulsa
                        itemsToShow = (itemsToShow + 5).coerceAtMost(items.size)

                        // Hacer scroll automático hacia los nuevos registros después de actualizar
                        coroutineScope.launch {
                            delay(150) // Delay para que se rendericen los nuevos items

                            // Calcular si necesitamos hacer scroll al final de la tabla
                            val newItemIndex = (itemsToShow - 1).coerceAtLeast(0)

                            // Si ya estamos mostrando más de ~11 registros (cuando la tabla alcanza su altura máxima)
                            // hacer scroll hasta el último registro para que se vean los nuevos
                            if (itemsToShow > 11) {
                                // Scroll hasta el último registro visible
                                tableListState.animateScrollToItem(newItemIndex)
                            } else {
                                // Si aún no hemos alcanzado la altura máxima, scroll suave al primer registro nuevo
                                tableListState.animateScrollToItem(previousCount.coerceAtMost(newItemIndex))
                            }
                        }
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.UnfoldMore,
                        contentDescription = "Mostrar más",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Mostrar más registros")
                }
            }
        }
    }
}

/**
 * Renderizador de cards híbridas para sesiones del día
 * Muestra cada clase como una card con estado, horario, curso y acciones contextuales
 */
@Composable
fun SesionesDelDiaCards(
    items: List<Map<String, Any?>>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { clase ->
            val icono = clase["icono"] as? String ?: ""
            val horaInicio = clase["hora_inicio"] as? String ?: ""
            val horaFin = clase["hora_fin"] as? String ?: ""
            val curso = clase["curso"] as? String ?: ""
            val aula = clase["aula"] as? String ?: ""
            val estado = clase["estado"] as? String ?: ""
            val alumnosTotal = clase["alumnos"] as? Number ?: 0
            val alumnosAsistieron = clase["alumnos_asistieron"] as? Number ?: 0
            val descripcionEstado = clase["descripcion_estado"] as? String ?: ""
            @Suppress("UNCHECKED_CAST")
            val acciones = (clase["acciones_disponibles"] as? List<String>) ?: emptyList()
            
            // Formato de asistencia según estado
            val textoAlumnos = when (estado) {
                "completada", "en_curso" -> "${alumnosAsistieron.toInt()}/${alumnosTotal.toInt()} alumnos"
                else -> "${alumnosTotal.toInt()} alumnos"  // Programada: solo capacidad
            }
            
            // Card con borde y padding
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Línea 1: Icono + Horario + Aula
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$icono $horaInicio - $horaFin",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "| $aula",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Línea 2: Curso
                    Text(
                        text = curso,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    
                    // Línea 3: Descripción estado
                    Text(
                        text = descripcionEstado,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Línea 4: Alumnos (formato según estado)
                    Text(
                        text = textoAlumnos,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Línea 5: Acciones
                    if (acciones.isNotEmpty()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 6.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            acciones.forEach { accion ->
                                OutlinedButton(
                                    onClick = { /* TODO: Implementar acciones */ },
                                    modifier = Modifier.height(36.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = accion,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
