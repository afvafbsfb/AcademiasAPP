package com.example.academiaapp.ui.screens

import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatScreen(fromLogin: Boolean = false, navController: NavController? = null) {
    val app = LocalContext.current.applicationContext as AcademiaApp
    val factory = remember { ChatViewModelFactory(app.container.chatRepository) }
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

                val itemsList = listOf("Inicio", "Chat", "Academias", "Cursos", "Profesores", "Alumnos", "Estadísticas", "Configuración", "Cerrar sesión")
                itemsList.forEach { label ->
                    NavigationDrawerItem(
                        label = { Text(label) },
                        selected = label == "Chat",
                        onClick = {
                            Log.d("NavDrawer", "Clicked item: $label")
                            when (label) {
                                "Chat" -> {
                                    coroutineScope.launch {
                                        Log.d("NavDrawer", "Closing drawer and navigating to Chat")
                                        try { drawerState.close() } catch (_: Exception) {}
                                        navController?.let { nc ->
                                            Log.d("NavDrawer", "Calling navController.navigate(\"chat\")")
                                            nc.navigate("chat") {
                                                // Evitar múltiples instancias y restaurar estado si existía
                                                launchSingleTop = true
                                                restoreState = true
                                                // Guardar estado al hacer pop para permitir restoreState
                                                popUpTo(nc.graph.startDestinationId) { saveState = true }
                                            }
                                        }
                                    }
                                }
                                "Cerrar sesión" -> {
                                    coroutineScope.launch {
                                        // cerrar drawer, limpiar sesión y cache, y navegar a login
                                        try {
                                            drawerState.close()
                                        } catch (_: Exception) { }
                                        try {
                                            vm.reset()
                                        } catch (_: Exception) { }
                                        try {
                                            app.container.session.clear()
                                        } catch (_: Exception) { }
                                        try {
                                            app.container.chatRepository.clearWelcomeCache()
                                        } catch (_: Exception) { }
                                        navController?.navigate("login") { popUpTo("login") { inclusive = true } }
                                    }
                                }
                                else -> {
                                    // rutas simples: navegar al label en lowercase si existe
                                    // puedes personalizar el mapeo de rutas aquí
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
                            placeholder = { Text("Escribe un mensaje…") }
                        )
                        Button(
                            onClick = { if (!ui.loading && input.isNotBlank()) { vm.sendMessage(input); input = "" } },
                            modifier = Modifier.padding(start = 8.dp)
                        ) { Text(if (ui.loading) "…" else "Enviar") }
                    }
                }
            }
        ) { innerPadding ->
            val showScrollToBottom by remember { derivedStateOf { listState.firstVisibleItemIndex < ui.messages.lastIndex - 2 } }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val lastIsAssistant = ui.messages.lastOrNull()?.role == "assistant"
                val hasExtras = ui.items.isNotEmpty() || ui.uiSuggestions.isNotEmpty()  // ✅ ACTUALIZADO
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(ui.messages) { index, m ->
                        val isAssistant = m.role == "assistant"
                        val isLast = index == ui.messages.lastIndex
                        val showExtras = isAssistant && isLast && (ui.items.isNotEmpty() || ui.uiSuggestions.isNotEmpty())  // ✅ ACTUALIZADO

                        val userBubbleColor = Color(0xFFDCF8C6)
                        val otherBubbleColor = Color(0xFFFFFFFF)
                        val bubbleBg = if (!isAssistant) userBubbleColor else otherBubbleColor
                        val textColor = Color(0xFF000000)
                        val align = if (!isAssistant) Alignment.CenterEnd else Alignment.CenterStart
                        // Esquinas asimétricas estilo WhatsApp: la esquina cercana a la "cola" menos redondeada
                        val bubbleShape = if (isAssistant) {
                            // Asistente (izquierda): esquina inferior izquierda menos redondeada
                            RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomEnd = 12.dp,
                                bottomStart = 4.dp
                            )
                        } else {
                            // Usuario (derecha): esquina inferior derecha menos redondeada
                            RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = 12.dp,
                                bottomEnd = 4.dp
                            )
                        }

                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = align) {
                            BoxWithConstraints(Modifier.fillMaxWidth()) {
                                val maxBubbleWidth = this.maxWidth * 0.8f
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
                                            if (ui.items.isNotEmpty()) {
                                                val typeLabel = ui.type?.let { "Resultados (${it})" } ?: "Resultados:"
                                                Text(typeLabel, color = textColor)
                                                CompactList(items = ui.items, summaryFields = ui.summaryFields, onOpenDetails = { detailsItem = it })
                                            }
                                            if (ui.uiSuggestions.isNotEmpty()) {  // ✅ ACTUALIZADO
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .horizontalScroll(rememberScrollState()),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    ui.uiSuggestions.take(5).forEach { suggestion ->
                                                        SuggestionChip(
                                                            onClick = { if (!ui.loading) vm.sendMessage(suggestion.displayText) }, 
                                                            enabled = !ui.loading, 
                                                            label = { Text(suggestion.displayText, maxLines = 1, overflow = TextOverflow.Ellipsis) }
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

                    // Si llegaron extras (items/suggestions) pero no hay una burbuja final del asistente que los muestre,
                    // renderizarlos como bloque aparte al final de la conversación.
                    if (hasExtras && !lastIsAssistant) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (ui.items.isNotEmpty()) {
                                    val typeLabel = ui.type?.let { "Resultados (${it})" } ?: "Resultados:"
                                    Text(typeLabel, color = Color(0xFF000000))
                                    CompactList(items = ui.items, summaryFields = ui.summaryFields, onOpenDetails = { detailsItem = it })
                                }
                                if (ui.uiSuggestions.isNotEmpty()) {  // ✅ ACTUALIZADO
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        ui.uiSuggestions.take(5).forEach { suggestion ->
                                            SuggestionChip(
                                                onClick = { if (!ui.loading) vm.sendMessage(suggestion.displayText) }, 
                                                enabled = !ui.loading, 
                                                label = { Text(suggestion.displayText, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (showScrollToBottom && ui.messages.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                        Button(onClick = {
                            val lastIndex = ui.messages.lastIndex
                            if (lastIndex >= 0) {
                                coroutineScope.launch { listState.animateScrollToItem(lastIndex) }
                            }
                        }, modifier = Modifier.padding(12.dp)) { Text("Bajar") }
                    }
                }
            }
        }

        if (detailsItem != null) {
            ModalBottomSheet(onDismissRequest = { detailsItem = null }) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    detailsItem!!.forEach { (k, v) ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text(text = "$k:", modifier = Modifier.weight(0.4f))
                            Text(text = (v?.toString() ?: ""), modifier = Modifier.weight(0.6f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactList(
    items: List<Map<String, Any?>>, 
    summaryFields: List<String>,
    onOpenDetails: (Map<String, Any?>) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9F9F9))
            .padding(8.dp)
            .heightIn(max = 280.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(items) { item ->
            val keys = item.keys.toList()
            fun findKeyIgnoreCase(target: String): String? = keys.firstOrNull { it.equals(target, ignoreCase = true) }
            fun valueOf(key: String?): String? = key?.let { k -> item[keys.firstOrNull { it.equals(k, ignoreCase = true) }]?.toString() }

            val summary = summaryFields.take(2)
            val (primaryKey, secondaryKey) = if (summary.isNotEmpty()) {
                val first = summary.getOrNull(0)?.let { findKeyIgnoreCase(it) }
                val second = summary.getOrNull(1)?.let { findKeyIgnoreCase(it) }
                first to second
            } else {
                // Heurística simple
                fun pick(vararg candidates: String): String? = candidates.firstOrNull { c -> keys.any { it.equals(c, ignoreCase = true) } }
                pick("title", "nombre", "name", "full_name", "displayName", "email") to
                    pick("description", "rol", "role", "estado", "status", "ciudad", "city", "telefono", "phone", "codigo", "id")
            }

            val primary = valueOf(primaryKey) ?: item.values.mapNotNull { it?.toString() }.firstOrNull() ?: "(sin datos)"
            val secondary = valueOf(secondaryKey) ?: item.values.mapNotNull { it?.toString() }.distinct().drop(1).firstOrNull()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = primary,
                    modifier = Modifier.weight(0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text(
                    text = secondary ?: "",
                    modifier = Modifier.weight(0.35f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = { onOpenDetails(item) }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Ver detalles")
                }
            }
        }
    }
}
