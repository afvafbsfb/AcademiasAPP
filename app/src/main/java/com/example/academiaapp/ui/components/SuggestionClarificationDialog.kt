package com.example.academiaapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.academiaapp.data.remote.dto.Suggestion

/**
 * Diálogo para pedir al usuario que complete la información de una sugerencia
 * antes de enviarla al backend.
 * 
 * Se usa cuando needsClarification() devuelve true:
 * - type="Registro" (siempre)
 * - type="Generica" que NO sea de listado (buscar, filtrar, etc.)
 */
@Composable
fun SuggestionClarificationDialog(
    suggestion: Suggestion,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit
) {
    // Inicializar con el texto de la sugerencia seguido de ": "
    var editedText by remember { mutableStateOf("${suggestion.displayText}: ") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Completar información",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column {
                Text(
                    text = "Por favor, completa tu mensaje con la mayor descripción posible para que te pueda responder adecuadamente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                OutlinedTextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),  // 5 líneas aprox
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (editedText.isNotBlank()) {
                                onSend(editedText)
                            }
                        }
                    ),
                    maxLines = 5,
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (editedText.isNotBlank()) {
                        onSend(editedText)
                    }
                },
                enabled = editedText.isNotBlank()
            ) {
                Text("Enviar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
