package com.example.academiaapp.ui.screens.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Renderizador de cards híbridas para sesiones del día
 * Muestra cada clase como una card con estado, horario, curso y acciones contextuales
 */
@Composable
fun SesionesDelDiaCards(
    items: List<Map<String, Any?>>,
    onVerAlumnos: (sesionId: Int) -> Unit = {},
    onVerAnotaciones: (sesionId: Int) -> Unit = {},
    onPasarLista: (sesionId: Int) -> Unit = {},
    onIniciar: (horarioId: Int) -> Unit = {}
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
                    // Línea 1: Horario + Aula (sin icono)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$horaInicio - $horaFin",
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
                                    onClick = {
                                        // 🔧 DEBUG: Ver qué contiene clase
                                        println("🔧 DEBUG onClick - clase completa: $clase")
                                        
                                        // ✅ Extraer ambos IDs disponibles
                                        val sesionId = (clase["sesion_id"] as? Number)?.toInt()
                                        val horarioId = (clase["id"] as? Number)?.toInt() ?: 0
                                        
                                        println("🔧 DEBUG onClick - sesionId=$sesionId, horarioId=$horarioId")
                                        
                                        // ✅ Para "Ver alumnos": priorizar sesionId, sino usar horarioId
                                        val idParaAlumnos = sesionId ?: horarioId
                                        
                                        println("🔧 DEBUG onClick - idParaAlumnos=$idParaAlumnos, accion=$accion")
                                        
                                        when (accion) {
                                            "Ver alumnos" -> onVerAlumnos(idParaAlumnos)
                                            "Ver anotaciones" -> onVerAnotaciones(sesionId ?: 0)
                                            "Pasar lista" -> onPasarLista(sesionId ?: 0)
                                            "Iniciar" -> onIniciar(horarioId)
                                        }
                                    },
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
