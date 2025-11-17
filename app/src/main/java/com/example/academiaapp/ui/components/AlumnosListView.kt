package com.example.academiaapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Componente reutilizable para mostrar lista de alumnos de una sesión
 * 
 * @param alumnos Lista de alumnos con sus datos
 * @param sesionEditable Si la sesión permite editar ausencias (false si está completada)
 * @param listaPasada Si la lista ya fue pasada
 * @param alumnosInscritos Total de alumnos inscritos en el curso
 * @param onAusenciaChanged Callback cuando se marca/desmarca checkbox de ausencia
 * @param onPasarLista Callback cuando se pulsa botón "Pasar lista" con lista de IDs ausentes
 * @param onNuevaAnotacion Callback cuando se pulsa "Nueva" anotación para un alumno
 * @param onVerAnotaciones Callback cuando se pulsa "Ver" anotaciones de un alumno
 */
@Composable
fun AlumnosListView(
    alumnos: List<AlumnoItem>,
    sesionEditable: Boolean,
    listaPasada: Boolean,
    alumnosInscritos: Int,
    ausenciasMap: Map<Int, Boolean>,
    onAusenciaChanged: (alumnoId: Int, ausente: Boolean) -> Unit,
    onPasarLista: (ausentes: List<Int>) -> Unit,
    onNuevaAnotacion: (alumnoId: Int) -> Unit,
    onVerAnotaciones: (alumnoId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Tabla de alumnos (sin card de alumnos inscritos)
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                // Cabecera de tabla
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Alumno",
                        modifier = Modifier.weight(2f),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "Ausencia",
                        modifier = Modifier.weight(2f),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Anotaciones",
                        modifier = Modifier.weight(3f),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                
                Divider()
                
                // Lista de alumnos
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(alumnos) { alumno ->
                        AlumnoRow(
                            alumno = alumno,
                            sesionEditable = sesionEditable,
                            listaPasada = listaPasada,
                            ausente = ausenciasMap[alumno.id] ?: (alumno.asistio == false),
                            onAusenciaChanged = { ausente ->
                                onAusenciaChanged(alumno.id, ausente)
                            },
                            onNuevaAnotacion = { onNuevaAnotacion(alumno.id) },
                            onVerAnotaciones = { onVerAnotaciones(alumno.id) }
                        )
                        if (alumno != alumnos.last()) {
                            Divider()
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Botón "Pasar lista" (solo si sesión editable - puede venir alguien tarde)
        if (sesionEditable) {
            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "📋 Pasar lista",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
    
    // Diálogo de confirmación
    if (showConfirmDialog) {
        val ausentes = ausenciasMap.filter { it.value }.keys.toList()
        val presentes = alumnos.size - ausentes.size
        
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar pasar lista") },
            text = {
                Column {
                    Text("¿Confirma que desea pasar lista con los siguientes datos?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Presentes: $presentes/${alumnos.size} alumnos", fontWeight = FontWeight.Bold)
                    Text("• Ausentes: ${ausentes.size} alumnos", fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        onPasarLista(ausentes)
                    }
                ) {
                    Text("Sí, pasar lista")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Fila individual de alumno en la tabla
 */
@Composable
private fun AlumnoRow(
    alumno: AlumnoItem,
    sesionEditable: Boolean,
    listaPasada: Boolean,
    ausente: Boolean,
    onAusenciaChanged: (Boolean) -> Unit,
    onNuevaAnotacion: () -> Unit,
    onVerAnotaciones: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nombre del alumno
        Text(
            text = alumno.nombre,
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.bodyMedium
        )
        
        // Columna Ausencia
        Box(
            modifier = Modifier.weight(2f),
            contentAlignment = Alignment.Center
        ) {
            if (!sesionEditable) {
                // Sesión PROGRAMADA o COMPLETADA (no editable) → "---" o Checkbox readonly
                if (!listaPasada) {
                    Text(
                        text = "---",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Checkbox(
                        checked = ausente,
                        onCheckedChange = null,
                        enabled = false
                    )
                }
            } else {
                // Sesión EN CURSO (editable) → Checkbox activo (puede venir alguien tarde)
                Checkbox(
                    checked = ausente,
                    onCheckedChange = onAusenciaChanged
                )
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Columna Anotaciones
        Row(
            modifier = Modifier.weight(3f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "Ver" si tiene anotaciones - DESHABILITADO en sesiones programadas
            if (alumno.tieneAnotaciones) {
                Text(
                    text = "Ver",
                    color = if (!listaPasada && !sesionEditable) {
                        Color.Gray  // Gris para sesiones programadas
                    } else {
                        MaterialTheme.colorScheme.primary  // Azul para sesiones iniciadas/completadas
                    },
                    modifier = if (!listaPasada && !sesionEditable) {
                        Modifier  // Sin clickable para sesiones programadas
                    } else {
                        Modifier.clickable { onVerAnotaciones() }  // Clickable para sesiones iniciadas/completadas
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(12.dp))
            } else {
                Text(
                    text = "---",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            
            // "Nueva" - DESHABILITADO en sesiones programadas
            Text(
                text = "Nueva",
                color = if (!listaPasada && !sesionEditable) {
                    Color.Gray  // Gris para sesiones programadas
                } else {
                    MaterialTheme.colorScheme.primary  // Azul para sesiones iniciadas/completadas
                },
                modifier = if (!listaPasada && !sesionEditable) {
                    Modifier  // Sin clickable para sesiones programadas
                } else {
                    Modifier.clickable { onNuevaAnotacion() }  // Clickable para sesiones iniciadas/completadas
                },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Data class para representar un alumno en la lista
 */
data class AlumnoItem(
    val id: Int,
    val nombre: String,
    val asistio: Boolean?,  // null = no marcado, true = presente, false = ausente
    val tieneAnotaciones: Boolean
)
