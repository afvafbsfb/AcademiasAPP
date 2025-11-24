package com.example.academiaapp.ui.screens.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.academiaapp.ui.viewmodels.ChatUiState
import com.example.academiaapp.ui.viewmodels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumnoAltaForm(
    formSpec: Map<String, Any?>,
    onSubmit: (Map<String, Any?>) -> Unit,
    onCancel: () -> Unit,
    vm: ChatViewModel,
    ui: ChatUiState
) {
    // Extraer cursos disponibles si vienen
    val cursos = (formSpec["cursos_disponibles"] as? List<Map<String, Any?>>) ?: emptyList()

    // Diálogo de confirmación
    

    // Focus requesters para cada campo obligatorio
    val nombreFocusRequester = remember { FocusRequester() }
    val telefonoFocusRequester = remember { FocusRequester() }
    val fechaFocusRequester = remember { FocusRequester() }

    // Función de validación
    fun validateAndShowDialog() {
        var ok = true
        var firstErrorField: FocusRequester? = null

        if (ui.nombreAlta.isBlank()) {
            vm.setNombreError(true)
            ok = false
            if (firstErrorField == null) firstErrorField = nombreFocusRequester
        }
        if (ui.telefonoAlta.isBlank()) {
            vm.setTelefonoError(true)
            ok = false
            if (firstErrorField == null) firstErrorField = telefonoFocusRequester
        }
        val fechaRegex = "^\\d{2}/\\d{2}/\\d{4}$".toRegex()
        if (!fechaRegex.matches(ui.fechaAlta)) {
            vm.setFechaError(true)
            ok = false
            if (firstErrorField == null) firstErrorField = fechaFocusRequester
        }

        if (!ok) {
            firstErrorField?.requestFocus()
            return
        }

        // Si validó bien, mostrar diálogo
        vm.showConfirmDialogAlta()
    }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Title
        Text("Formulario de alta", style = MaterialTheme.typography.titleMedium)

        // Nombre (required)
        OutlinedTextField(
            value = ui.nombreAlta,
            onValueChange = { vm.setNombreAlta(it); if (it.isNotBlank()) vm.setNombreError(false) },
            label = { Text("Nombre completo *") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(nombreFocusRequester)
                .background(if (ui.nombreError) Color(0xFFFFE0B2) else Color.Transparent),
            singleLine = true,
            isError = ui.nombreError
        )

        // Telefono (required)
        OutlinedTextField(
            value = ui.telefonoAlta,
            onValueChange = { vm.setTelefonoAlta(it); if (it.isNotBlank()) vm.setTelefonoError(false) },
            label = { Text("Teléfono *") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(telefonoFocusRequester)
                .background(if (ui.telefonoError) Color(0xFFFFE0B2) else Color.Transparent),
            singleLine = true,
            isError = ui.telefonoError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        // Fecha nacimiento (required) with simple mask
        OutlinedTextField(
            value = ui.fechaAlta,
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
                vm.setFechaAlta(masked)
                if (masked.isNotBlank()) vm.setFechaError(false)
            },
            label = { Text("Fecha de nacimiento (DD/MM/AAAA) *") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(fechaFocusRequester)
                .background(if (ui.fechaError) Color(0xFFFFE0B2) else Color.Transparent),
            singleLine = true,
            isError = ui.fechaError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // Email, DNI, Dirección (optional)
        OutlinedTextField(value = ui.emailAlta, onValueChange = { vm.setEmailAlta(it) }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = ui.dniAlta, onValueChange = { vm.setDniAlta(it) }, label = { Text("DNI") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = ui.direccionAlta, onValueChange = { vm.setDireccionAlta(it) }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())

        // Curso (optional) - Dropdown Menu
        if (cursos.isNotEmpty()) {
            ExposedDropdownMenuBox(
                expanded = ui.expandedCursosAlta,
                onExpandedChange = { vm.setExpandedCursosAlta(it) }
            ) {
                OutlinedTextField(
                    value = ui.cursoLabelAlta,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Curso (opcional)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ui.expandedCursosAlta) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = ui.expandedCursosAlta,
                    onDismissRequest = { vm.setExpandedCursosAlta(false) }
                ) {
                    cursos.forEach { c ->
                        val id = (c["id"] as? Number)?.toInt()
                        val label = c["display_text"] as? String ?: ""
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                vm.setCursoSeleccionadoAlta(id)
                                vm.setCursoNombreAlta(label)
                                vm.setCursoLabelAlta(label)
                                vm.setExpandedCursosAlta(false)
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
                    colors = SuggestionChipDefaults.suggestionChipColors(
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
                    colors = SuggestionChipDefaults.suggestionChipColors(
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
    if (ui.showConfirmDialogAlta) {
        AlertDialog(
            onDismissRequest = { vm.hideConfirmDialogAlta() },
            title = { Text("Confirmar alta") },
            text = { Text("¿Has verificado todos los datos del alumno?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.hideConfirmDialogAlta()
                    // Build form map
                    val formMap = mapOf(
                        "nombre" to ui.nombreAlta,
                        "email" to ui.emailAlta,
                        "dni" to ui.dniAlta,
                        "telefono" to ui.telefonoAlta,
                        "fecha_nacimiento" to ui.fechaAlta,
                        "direccion" to ui.direccionAlta,
                        "curso_id" to ui.cursoSeleccionadoAlta,
                        "curso_nombre" to ui.cursoNombreAlta
                    )
                    onSubmit(formMap)
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { vm.hideConfirmDialogAlta() }) {
                    Text("No")
                }
            }
        )
    }
}
