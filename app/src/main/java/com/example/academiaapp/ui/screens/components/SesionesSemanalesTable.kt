package com.example.academiaapp.ui.screens.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Renderizador de tabla para vista semanal de sesiones
 * Muestra día, cantidad de clases y botón para ver detalles
 */
@Composable
fun SesionesSemanalesTable(
    items: List<Map<String, Any?>>,
    expandedDay: String?,
    onToggleExpandedDay: (String?) -> Unit
) {
    

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Cabecera de la tabla
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Día",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(0.4f)
            )
            Text(
                text = "Clases",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(0.3f)
            )
            // Espacio para el botón de detalles
            Spacer(modifier = Modifier.weight(0.3f))
        }

        HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

        // Filas de datos
        items.forEach { diaData ->
            val dia = diaData["dia"] as? String ?: ""
            val cantidad = (diaData["cantidad"] as? Number)?.toInt() ?: 0
            @Suppress("UNCHECKED_CAST")
            val horarios = (diaData["horarios"] as? List<Map<String, Any?>>) ?: emptyList()

            Column(modifier = Modifier.fillMaxWidth()) {
                // Fila principal
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (expandedDay == dia) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            else Color.Transparent
                        )
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dia,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(0.4f)
                    )
                    Text(
                        text = "$cantidad",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(0.3f)
                    )
                    
                    // Botón para expandir/contraer detalles
                    if (cantidad > 0) {
                        IconButton(
                            onClick = { 
                                onToggleExpandedDay(if (expandedDay == dia) null else dia)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (expandedDay == dia) Icons.Default.UnfoldMore else Icons.Default.Search,
                                contentDescription = if (expandedDay == dia) "Ocultar horarios" else "Ver horarios",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(0.3f))
                    }
                }

                // Detalles expandidos
                if (expandedDay == dia && horarios.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Horarios del $dia:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        horarios.forEach { horario ->
                            val horaInicio = horario["hora_inicio"] as? String ?: ""
                            val horaFin = horario["hora_fin"] as? String ?: ""
                            val cursoId = (horario["curso_id"] as? Number)?.toInt() ?: 0
                            val aulaId = (horario["aula_id"] as? Number)?.toInt() ?: 0
                            
                            // Obtener nombres desde el horario si están disponibles
                            val cursoNombre = when {
                                horario["curso"] != null -> horario["curso"] as? String
                                horario["curso_nombre"] != null -> horario["curso_nombre"] as? String
                                else -> "Curso #$cursoId"
                            } ?: "Curso #$cursoId"
                            
                            val aulaNombre = when {
                                horario["aula"] != null -> horario["aula"] as? String
                                horario["aula_nombre"] != null -> horario["aula_nombre"] as? String
                                else -> "Aula #$aulaId"
                            } ?: "Aula #$aulaId"
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "$horaInicio - $horaFin",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "$cursoNombre",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "$aulaNombre",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                if (dia != items.last()["dia"]) {
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
                }
            }
        }
    }
}
