package com.example.academiaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.academiaapp.AcademiaApp
import com.example.academiaapp.R
import com.example.academiaapp.domain.Result
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordRequiredScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val app = LocalContext.current.applicationContext as AcademiaApp
    val usuariosRepo = app.container.usuariosRepository
    val scope = rememberCoroutineScope()
    
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFEEF2FF), Color(0xFFE8F5E9))
                )
            )
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .fillMaxWidth(0.9f),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_eye),
                    contentDescription = "Cambiar contraseña",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = "Cambio de contraseña obligatorio",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Estás usando una contraseña temporal. Por seguridad, debes cambiarla ahora.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                // Contraseña actual
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Contraseña actual", color = Color.Black) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    trailingIcon = {
                        val icon = if (currentPasswordVisible) R.drawable.ic_eye else R.drawable.ic_eye_off
                        IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                            Icon(
                                painter = painterResource(id = icon),
                                contentDescription = if (currentPasswordVisible) "Ocultar" else "Mostrar",
                                tint = Color.Black
                            )
                        }
                    }
                )

                // Nueva contraseña
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva contraseña", color = Color.Black) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    trailingIcon = {
                        val icon = if (newPasswordVisible) R.drawable.ic_eye else R.drawable.ic_eye_off
                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                            Icon(
                                painter = painterResource(id = icon),
                                contentDescription = if (newPasswordVisible) "Ocultar" else "Mostrar",
                                tint = Color.Black
                            )
                        }
                    }
                )

                // Confirmar contraseña
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar nueva contraseña", color = Color.Black) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    trailingIcon = {
                        val icon = if (confirmPasswordVisible) R.drawable.ic_eye else R.drawable.ic_eye_off
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                painter = painterResource(id = icon),
                                contentDescription = if (confirmPasswordVisible) "Ocultar" else "Mostrar",
                                tint = Color.Black
                            )
                        }
                    }
                )

                if (error != null) {
                    Text(
                        text = error ?: "",
                        color = Color(0xFFB00020),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }

                // Validaciones mínimas
                Text(
                    text = "• La contraseña debe tener al menos 6 caracteres\n• No puede ser igual a tu email\n• Debe ser diferente de la contraseña actual",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        error = null
                        when {
                            currentPassword.isBlank() -> error = "Ingresa tu contraseña actual"
                            newPassword.length < 6 -> error = "La nueva contraseña debe tener al menos 6 caracteres"
                            newPassword != confirmPassword -> error = "Las contraseñas no coinciden"
                            newPassword == currentPassword -> error = "La nueva contraseña debe ser diferente"
                            else -> {
                                loading = true
                                scope.launch {
                                    when (val result = usuariosRepo.changeMyPassword(currentPassword, newPassword)) {
                                        is Result.Success -> {
                                            loading = false
                                            // Navegar al chat después del cambio exitoso
                                            navController.navigate("chat?fromLogin=true") {
                                                popUpTo("change_password_required") { inclusive = true }
                                            }
                                        }
                                        is Result.Error -> {
                                            loading = false
                                            error = result.message
                                        }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(if (loading) "Cambiando..." else "Cambiar contraseña")
                }

                Text(
                    text = "No podrás usar la aplicación hasta que cambies tu contraseña.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFB00020),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
