package com.example.academiaapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Componente que muestra la lista de alumnos de una sesión específica
 * Incluye cabecera con información de la sesión y tabla con alumnos, asistencia y anotaciones
 * 
 * @param sesionInfo Mapa con información de la sesión (hora, curso, aula, profesor, etc.)
 * @param alumnos Lista de alumnos con sus datos de asistencia y anotaciones
 * @param onVerAnotaciones Callback cuando se pulsa "Ver" anotaciones de un alumno
 * @param onNuevaAnotacion Callback cuando se pulsa "Nueva" anotación para un alumno
 */
@Composable
fun AlumnosSesionTable(
    sesionInfo: Map<String, Any?>,
    alumnos: List<Map<String, Any?>>,
    onVerAnotaciones: (alumnoId: Int) -> Unit = {},
    onNuevaAnotacion: (alumnoId: Int) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ===============================================
        // CABECERA DE SESIÓN (simplificada - info completa en mensaje)
        // ===============================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Título: Curso
                Text(
                    text = sesionInfo["curso"] as? String ?: "Curso",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                // Asistencia (contador principal)
                val asistieron = (sesionInfo["alumnos_asistieron"] as? Number)?.toInt() ?: 0
                val total = (sesionInfo["alumnos_total"] as? Number)?.toInt() ?: 0
                val listaPasada = sesionInfo["lista_pasada"] as? Boolean ?: false
                
                if (listaPasada) {
                    Text(
                        text = "Lista pasada: $asistieron/$total",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    Text(
                        text = "Lista pendiente",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFF6F00)
                    )
                }
            }
        }
        
        // ===============================================
        // TABLA DE ALUMNOS
        // ===============================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Cabecera de la tabla
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(vertical = 8.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Alumno",
                        modifier = Modifier.weight(3f),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = "Asistencia",
                        modifier = Modifier.weight(2f),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = "Anotaciones",
                        modifier = Modifier.weight(2f),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }
                
                Spacer(Modifier.height(4.dp))
                
                // Filas de alumnos
                alumnos.forEachIndexed { index, alumno ->
                    if (index > 0) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            thickness = 0.5.dp,
                            color = Color(0xFFE0E0E0)
                        )
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Columna 1: Nombre del alumno
                        Text(
                            text = alumno["nombre"] as? String ?: "Alumno",
                            modifier = Modifier.weight(3f),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF212121)
                        )
                        
                        // Columna 2: Asistencia (check o cruz) - AMPLIADA
                        Box(
                            modifier = Modifier.weight(2f),
                            contentAlignment = Alignment.Center
                        ) {
                            val asistio = alumno["asistio"] as? Boolean
                            
                            when (asistio) {
                                true -> {
                                    // Asistió: Check verde
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Asistió",
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                false -> {
                                    // No asistió: Cruz roja
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "No asistió",
                                        tint = Color(0xFFF44336),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                null -> {
                                    // Sin pasar lista: Guion gris
                                    Text(
                                        text = "-",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                        
                        // Columna 3: Anotaciones (indicador + botones)
                        Row(
                            modifier = Modifier.weight(2f),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val tieneAnotaciones = alumno["tiene_anotaciones"] as? Boolean ?: false
                            val alumnoId = (alumno["id"] as? Number)?.toInt() ?: 0
                            
                            // Indicador visual
                            Icon(
                                imageVector = if (tieneAnotaciones) Icons.Default.Warning else Icons.Default.CheckCircle,
                                contentDescription = if (tieneAnotaciones) "Tiene anotaciones" else "Sin anotaciones",
                                tint = if (tieneAnotaciones) Color(0xFFFF9800) else Color(0xFFBDBDBD),
                                modifier = Modifier.size(16.dp)
                            )
                            
                            // Botón "Ver" (solo si tiene anotaciones)
                            if (tieneAnotaciones) {
                                TextButton(
                                    onClick = { onVerAnotaciones(alumnoId) },
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text(
                                        text = "Ver",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            
                            // Botón "Nueva"
                            TextButton(
                                onClick = { onNuevaAnotacion(alumnoId) },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text(
                                    text = "Nueva",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                
                // Mensaje si no hay alumnos
                if (alumnos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay alumnos matriculados en este curso",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// ===============================================
// PREVIEW
// ===============================================

@Preview(showBackground = true, backgroundColor = 0xFFECE5DD)
@Composable
private fun AlumnosSesionTablePreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AlumnosSesionTable(
                sesionInfo = mapOf(
                    "sesion_id" to 1,
                    "hora_inicio" to "08:00",
                    "hora_fin" to "10:00",
                    "curso" to "Matemáticas 1º ESO",
                    "aula" to "Aula 3",
                    "profesor" to "María García",
                    "fecha" to "2025-10-22",
                    "alumnos_total" to 7,
                    "alumnos_asistieron" to 6,
                    "lista_pasada" to true
                ),
                alumnos = listOf(
                    mapOf(
                        "id" to 4,
                        "nombre" to "Luis García García",
                        "asistio" to true,
                        "tiene_anotaciones" to false
                    ),
                    mapOf(
                        "id" to 12,
                        "nombre" to "Carmen Rodríguez Fernández",
                        "asistio" to true,
                        "tiene_anotaciones" to true
                    ),
                    mapOf(
                        "id" to 20,
                        "nombre" to "Roberto Fernández López",
                        "asistio" to false,
                        "tiene_anotaciones" to false
                    ),
                    mapOf(
                        "id" to 28,
                        "nombre" to "Andrés López Martínez",
                        "asistio" to true,
                        "tiene_anotaciones" to true
                    ),
                    mapOf(
                        "id" to 36,
                        "nombre" to "Jorge Martínez Sánchez",
                        "asistio" to true,
                        "tiene_anotaciones" to false
                    ),
                    mapOf(
                        "id" to 44,
                        "nombre" to "Víctor Sánchez Pérez",
                        "asistio" to true,
                        "tiene_anotaciones" to false
                    ),
                    mapOf(
                        "id" to 52,
                        "nombre" to "Iván Pérez Gómez",
                        "asistio" to true,
                        "tiene_anotaciones" to false
                    )
                ),
                onVerAnotaciones = { println("Ver anotaciones alumno: $it") },
                onNuevaAnotacion = { println("Nueva anotación alumno: $it") }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFECE5DD)
@Composable
private fun AlumnosSesionTableSinListaPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AlumnosSesionTable(
                sesionInfo = mapOf(
                    "sesion_id" to 5,
                    "hora_inicio" to "15:30",
                    "hora_fin" to "17:30",
                    "curso" to "Física 2º ESO",
                    "aula" to "Laboratorio 1",
                    "profesor" to "Juan Pérez",
                    "fecha" to "2025-10-24",
                    "alumnos_total" to 7,
                    "alumnos_asistieron" to 0,
                    "lista_pasada" to false
                ),
                alumnos = listOf(
                    mapOf(
                        "id" to 5,
                        "nombre" to "Luis García García",
                        "asistio" to null,
                        "tiene_anotaciones" to false
                    ),
                    mapOf(
                        "id" to 13,
                        "nombre" to "Carmen Rodríguez Fernández",
                        "asistio" to null,
                        "tiene_anotaciones" to false
                    ),
                    mapOf(
                        "id" to 21,
                        "nombre" to "Roberto Fernández López",
                        "asistio" to null,
                        "tiene_anotaciones" to false
                    )
                ),
                onVerAnotaciones = { println("Ver anotaciones alumno: $it") },
                onNuevaAnotacion = { println("Nueva anotación alumno: $it") }
            )
        }
    }
}
