package com.example.academiaapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Diálogo para ver las anotaciones existentes de un alumno en una sesión
 * Muestra todas las anotaciones EXCEPTO las de tipo "Ausencia"
 */
@Composable
fun VerAnotacionesDialog(
    alumnoNombre: String,
    anotaciones: List<Map<String, Any?>>,
    onDismiss: () -> Unit
) {
    // Filtrar anotaciones excluyendo "Ausencia"
    val anotacionesFiltradas = anotaciones.filter { 
        (it["tipo_anotacion"] as? String) != "Ausencia" 
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Título
                Text(
                    text = "Anotaciones de $alumnoNombre",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))

                // Lista de anotaciones
                if (anotacionesFiltradas.isEmpty()) {
                    Text(
                        text = "No hay anotaciones para este alumno",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(anotacionesFiltradas) { anotacion ->
                            AnotacionItem(anotacion)
                        }
                    }
                }
                
                // Botón Cerrar
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

/**
 * Item individual de anotación
 */
@Composable
private fun AnotacionItem(anotacion: Map<String, Any?>) {
    val tipo = anotacion["tipo_anotacion"] as? String ?: "Sin tipo"
    val descripcion = anotacion["descripcion"] as? String ?: ""
    val timestamp = anotacion["timestamp"] as? String ?: ""
    
    // Formatear timestamp para mostrarlo más legible
    val fechaFormateada = try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = LocalDateTime.parse(timestamp, formatter)
        val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        dateTime.format(displayFormatter)
    } catch (e: Exception) {
        timestamp
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = tipo,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = fechaFormateada,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Diálogo para crear una nueva anotación
 * Permite seleccionar tipo y escribir descripción
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaAnotacionDialog(
    alumnoNombre: String,
    onConfirm: (tipo: String, descripcion: String) -> Unit,
    onDismiss: () -> Unit
) {
    var textoAnotacion by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf("Evaluacion") }
    var expanded by remember { mutableStateOf(false) }
    var errorTexto by remember { mutableStateOf(false) }
    
    val tiposAnotacion = listOf("Evaluacion", "Comportamiento", "Observacion", "Otros")
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Título
                Text(
                    text = "Nueva anotación para $alumnoNombre",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))

                // Selector de tipo
                Text(
                    text = "Tipo de anotación",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    OutlinedTextField(
                        value = tipoSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        tiposAnotacion.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    tipoSeleccionado = tipo
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                // Campo de texto
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = textoAnotacion,
                    onValueChange = { 
                        textoAnotacion = it
                        errorTexto = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = { Text("Escribe aquí la anotación...") },
                    isError = errorTexto,
                    supportingText = if (errorTexto) {
                        { Text("El texto es obligatorio") }
                    } else null,
                    maxLines = 6
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            if (textoAnotacion.trim().isEmpty()) {
                                errorTexto = true
                            } else {
                                onConfirm(tipoSeleccionado, textoAnotacion.trim())
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Enviar")
                    }
                }
            }
        }
    }
}
