package com.example.academiaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaapp.AcademiaApp
import com.example.academiaapp.R
import com.example.academiaapp.ui.viewmodels.ChatViewModel
import com.example.academiaapp.ui.viewmodels.ChatViewModelFactory

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatScreen(fromLogin: Boolean = false) {
    val app = LocalContext.current.applicationContext as AcademiaApp
    val factory = remember { ChatViewModelFactory(app.container.chatRepository) }
    val vm: ChatViewModel = viewModel(factory = factory)
    val ui by vm.ui.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(fromLogin) {
        if (fromLogin && ui.messages.isEmpty()) {
            vm.loadWelcome()
        }
    }

    var detailsItem by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var input by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe un mensaje…") }
                )
                Button(
                    onClick = { vm.sendMessage(input); input = "" },
                    enabled = !ui.loading && input.isNotBlank(),
                    modifier = Modifier.padding(start = 8.dp)
                ) { Text(if (ui.loading) "…" else "Enviar") }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(ui.messages) { index, m ->
                val isAssistant = m.role == "assistant"
                val isLast = index == ui.messages.lastIndex
                val showExtras = isAssistant && isLast && (ui.items.isNotEmpty() || ui.suggestions.isNotEmpty())

                val bg = if (!isAssistant) Color(0xFFE3F2FD) else Color(0xFFF1F8E9)
                val align = if (!isAssistant) Alignment.CenterEnd else Alignment.CenterStart
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = align) {
                    Card(colors = CardDefaults.cardColors(containerColor = bg), shape = RoundedCornerShape(12.dp)) {
                        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(m.text)
                            if (showExtras) {
                                if (ui.items.isNotEmpty()) {
                                    val typeLabel = ui.type?.let { "Resultados (${it})" } ?: "Resultados:"
                                    Text(typeLabel)
                                    CompactList(
                                        items = ui.items,
                                        summaryFields = ui.summaryFields,
                                        onOpenDetails = { detailsItem = it }
                                    )
                                }
                                if (ui.suggestions.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        ui.suggestions.take(5).forEach { s ->
                                            SuggestionChip(
                                                onClick = { vm.sendMessage(s) },
                                                label = { Text(s, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (ui.error != null) {
                item { Text(ui.error ?: "", color = Color(0xFFB00020)) }
            }
        }
    }

    // BottomSheet de detalles con todos los campos
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

@Composable
private fun CompactList(
    items: List<Map<String, Any?>>, 
    summaryFields: List<String>,
    onOpenDetails: (Map<String, Any?>) -> Unit
) {
    // Lista con altura acotada y scroll interno para no romper el layout
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
                    Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription = "Ver detalles")
                }
            }
        }
    }
}

