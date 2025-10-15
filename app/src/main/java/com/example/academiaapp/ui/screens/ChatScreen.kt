package com.example.academiaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaapp.AcademiaApp
import com.example.academiaapp.ui.viewmodels.ChatViewModel
import com.example.academiaapp.ui.viewmodels.ChatViewModelFactory

@Composable
fun ChatScreen() {
    val app = LocalContext.current.applicationContext as AcademiaApp
    val factory = remember { ChatViewModelFactory(app.container.chatRepository) }
    val vm: ChatViewModel = viewModel(factory = factory)
    val ui by vm.ui.collectAsState()

    LaunchedEffect(Unit) { vm.loadWelcome() }

    Column(Modifier.fillMaxSize()) {
        // Messages
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ui.messages) { m ->
                val bg = if (m.role == "user") Color(0xFFE3F2FD) else Color(0xFFF1F8E9)
                val align = if (m.role == "user") Alignment.CenterEnd else Alignment.CenterStart
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = align) {
                    Card(colors = CardDefaults.cardColors(containerColor = bg), shape = RoundedCornerShape(12.dp)) {
                        Text(m.text, modifier = Modifier.padding(12.dp))
                    }
                }
            }
        }

        // Items simple list (if any)
        if (ui.items.isNotEmpty()) {
            Column(Modifier.fillMaxWidth().background(Color(0xFFF9F9F9)).padding(8.dp)) {
                Text("Resultados:")
                ui.items.forEach { item ->
                    Text("• ${item.title ?: item.name ?: item.id ?: "item"}")
                }
            }
        }

        // Suggestions chips (simple buttons)
        if (ui.suggestions.isNotEmpty()) {
            Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ui.suggestions.take(3).forEach { s ->
                    Button(onClick = { vm.sendMessage(s) }) { Text(s) }
                }
            }
        }

        // Input
        var input by remember { mutableStateOf("") }
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
        ui.error?.let { Text(it, color = Color(0xFFB00020), modifier = Modifier.padding(8.dp)) }
    }
}

