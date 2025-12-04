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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.academiaapp.ui.viewmodels.ChatUiState
import com.example.academiaapp.ui.viewmodels.ChatViewModel

/**
 * Modo del formulario de alumno
 */
enum class FormMode {
    ALTA,           // Dar de alta un nuevo alumno
    MODIFICACION    // Modificar datos de un alumno existente
}

/**
 * Formulario unificado para alta y modificación de alumnos.
 * 
 * Este componente refactorizado reemplaza AlumnoAltaForm y AlumnoModificacionForm,
 * eliminando ~250 líneas de código duplicado.
 * 
 * @param mode Modo del formulario (ALTA o MODIFICACION)
 * @param formSpec Especificación del formulario con cursos disponibles y datos del alumno
 * @param onSubmit Callback para enviar el formulario (firma adaptada según el modo)
 * @param onCancel Callback para cancelar el formulario
 * @param vm ViewModel que gestiona el estado del formulario
 * @param ui Estado actual de la UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumnoForm(
    mode: FormMode,
    formSpec: Map<String, Any?>,
    onSubmit: (Map<String, Any?>) -> Unit,
    onSubmitWithId: ((Map<String, Any?>, Int, Map<String, Any?>) -> Unit)? = null,
    onCancel: () -> Unit,
    vm: ChatViewModel,
    ui: ChatUiState
) {
    // Extraer cursos disponibles y datos del alumno desde formSpec
    val cursos = (formSpec["cursos_disponibles"] as? List<Map<String, Any?>>) ?: emptyList()
    val alumnoData = if (mode == FormMode.MODIFICACION) {
        (formSpec["alumno_data"] as? Map<String, Any?>) ?: emptyMap()
    } else {
        emptyMap()
    }

    // Inicializar datos según el modo
    LaunchedEffect(mode) {
        if (mode == FormMode.MODIFICACION && alumnoData.isNotEmpty()) {
            // Pre-cargar datos del alumno existente
            val cursoIdInicial = (alumnoData["curso_id"] as? Number)?.toInt()
            vm.initCursoLabelModificacion(cursos, cursoIdInicial)
            vm.initCursoSeleccionadoModificacion(cursoIdInicial)
            vm.initCursoNombreModificacion(cursos, cursoIdInicial)
            vm.initNombreModificacion(alumnoData)
            vm.initTelefonoModificacion(alumnoData)
            vm.initEmailModificacion(alumnoData)
            vm.initDniModificacion(alumnoData)
            vm.initDireccionModificacion(alumnoData)
            vm.initFechaModificacion(alumnoData)
        }
        // Para ALTA, los campos ya están vacíos por defecto en ChatUiState
    }

    // Focus requesters para cada campo obligatorio
    val nombreFocusRequester = remember { FocusRequester() }
    val telefonoFocusRequester = remember { FocusRequester() }
    val fechaFocusRequester = remember { FocusRequester() }

    // Obtener valores según el modo
    val nombre = if (mode == FormMode.ALTA) ui.nombreAlta else ui.nombreModificacion
    val telefono = if (mode == FormMode.ALTA) ui.telefonoAlta else ui.telefonoModificacion
    val fecha = if (mode == FormMode.ALTA) ui.fechaAlta else ui.fechaModificacion
    val email = if (mode == FormMode.ALTA) ui.emailAlta else ui.emailModificacion
    val dni = if (mode == FormMode.ALTA) ui.dniAlta else ui.dniModificacion
    val direccion = if (mode == FormMode.ALTA) ui.direccionAlta else ui.direccionModificacion
    val cursoLabel = if (mode == FormMode.ALTA) ui.cursoLabelAlta else ui.cursoLabelModificacion
    val expandedCursos = if (mode == FormMode.ALTA) ui.expandedCursosAlta else ui.expandedCursosModificacion
    val cursoSeleccionado = if (mode == FormMode.ALTA) ui.cursoSeleccionadoAlta else ui.cursoSeleccionadoModificacion
    val cursoNombre = if (mode == FormMode.ALTA) ui.cursoNombreAlta else ui.cursoNombreModificacion
    val showConfirmDialog = if (mode == FormMode.ALTA) ui.showConfirmDialogAlta else ui.showConfirmDialogModificacion

    // Función de validación
    fun validateAndShowDialog() {
        var ok = true
        var firstErrorField: FocusRequester? = null

        if (nombre.isBlank()) {
            vm.setNombreError(true)
            ok = false
            if (firstErrorField == null) firstErrorField = nombreFocusRequester
        }
        if (telefono.isBlank()) {
            vm.setTelefonoError(true)
            ok = false
            if (firstErrorField == null) firstErrorField = telefonoFocusRequester
        }
        val fechaRegex = "^\\d{2}/\\d{2}/\\d{4}$".toRegex()
        if (!fechaRegex.matches(fecha)) {
            vm.setFechaError(true)
            ok = false
            if (firstErrorField == null) firstErrorField = fechaFocusRequester
        }

        if (!ok) {
            firstErrorField?.requestFocus()
            return
        }

        // Si validó bien, mostrar diálogo
        if (mode == FormMode.ALTA) {
            vm.showConfirmDialogAlta()
        } else {
            vm.showConfirmDialogModificacion()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Title
        Text(
            text = if (mode == FormMode.ALTA) "Formulario de alta" else "Formulario de modificación",
            style = MaterialTheme.typography.titleMedium
        )

        // Nombre (required)
        OutlinedTextField(
            value = nombre,
            onValueChange = { 
                if (mode == FormMode.ALTA) {
                    vm.setNombreAlta(it)
                } else {
                    vm.setNombreModificacion(it)
                }
                if (it.isNotBlank()) vm.setNombreError(false)
            },
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
            value = telefono,
            onValueChange = { 
                if (mode == FormMode.ALTA) {
                    vm.setTelefonoAlta(it)
                } else {
                    vm.setTelefonoModificacion(it)
                }
                if (it.isNotBlank()) vm.setTelefonoError(false)
            },
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
            value = fecha,
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
                if (mode == FormMode.ALTA) {
                    vm.setFechaAlta(masked)
                } else {
                    vm.setFechaModificacion(masked)
                }
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
        OutlinedTextField(
            value = email,
            onValueChange = { 
                if (mode == FormMode.ALTA) {
                    vm.setEmailAlta(it)
                } else {
                    vm.setEmailModificacion(it)
                }
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = dni,
            onValueChange = { 
                if (mode == FormMode.ALTA) {
                    vm.setDniAlta(it)
                } else {
                    vm.setDniModificacion(it)
                }
            },
            label = { Text("DNI") },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = direccion,
            onValueChange = { 
                if (mode == FormMode.ALTA) {
                    vm.setDireccionAlta(it)
                } else {
                    vm.setDireccionModificacion(it)
                }
            },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )

        // Curso (optional) - Dropdown Menu
        if (cursos.isNotEmpty()) {
            ExposedDropdownMenuBox(
                expanded = expandedCursos,
                onExpandedChange = { 
                    if (mode == FormMode.ALTA) {
                        vm.setExpandedCursosAlta(it)
                    } else {
                        vm.setExpandedCursosModificacion(it)
                    }
                }
            ) {
                OutlinedTextField(
                    value = cursoLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Curso (opcional)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCursos) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = expandedCursos,
                    onDismissRequest = { 
                        if (mode == FormMode.ALTA) {
                            vm.setExpandedCursosAlta(false)
                        } else {
                            vm.setExpandedCursosModificacion(false)
                        }
                    }
                ) {
                    cursos.forEach { c ->
                        val id = (c["id"] as? Number)?.toInt()
                        val label = c["display_text"] as? String ?: ""
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                if (mode == FormMode.ALTA) {
                                    vm.setCursoSeleccionadoAlta(id)
                                    vm.setCursoNombreAlta(label)
                                    vm.setCursoLabelAlta(label)
                                    vm.setExpandedCursosAlta(false)
                                } else {
                                    vm.setCursoSeleccionadoModificacion(id)
                                    vm.setCursoNombreModificacion(label)
                                    vm.setCursoLabelModificacion(label)
                                    vm.setExpandedCursosModificacion(false)
                                }
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Botones estilizados como sugerencias
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botón Submit
            Row(modifier = Modifier.fillMaxWidth()) {
                SuggestionChip(
                    onClick = { validateAndShowDialog() },
                    label = { Text(if (mode == FormMode.ALTA) "Alta" else "Modificar") },
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
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { 
                if (mode == FormMode.ALTA) {
                    vm.hideConfirmDialogAlta()
                } else {
                    vm.hideConfirmDialogModificacion()
                }
            },
            title = { Text(if (mode == FormMode.ALTA) "Confirmar alta" else "Confirmar modificación") },
            text = { Text("¿Has verificado todos los datos del alumno?") },
            confirmButton = {
                TextButton(onClick = {
                    if (mode == FormMode.ALTA) {
                        vm.hideConfirmDialogAlta()
                    } else {
                        vm.hideConfirmDialogModificacion()
                    }
                    
                    // Build form map
                    val formMap = mapOf(
                        "nombre" to nombre,
                        "email" to email,
                        "dni" to dni,
                        "telefono" to telefono,
                        "fecha_nacimiento" to fecha,
                        "direccion" to direccion,
                        "curso_id" to cursoSeleccionado,
                        "curso_nombre" to cursoNombre
                    )
                    
                    if (mode == FormMode.ALTA) {
                        onSubmit(formMap)
                    } else {
                        // Para modificación, enviar también el ID del alumno
                        val alumnoId = (alumnoData["id"] as? Number)?.toInt() ?: -1
                        onSubmitWithId?.invoke(formMap, alumnoId, alumnoData)
                    }
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    if (mode == FormMode.ALTA) {
                        vm.hideConfirmDialogAlta()
                    } else {
                        vm.hideConfirmDialogModificacion()
                    }
                }) {
                    Text("No")
                }
            }
        )
    }
}
