package com.example.academiaapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.academiaapp.AcademiaApp
import com.example.academiaapp.R
import com.example.academiaapp.ui.viewmodels.LoginUiState
import com.example.academiaapp.ui.viewmodels.LoginViewModel
import com.example.academiaapp.ui.viewmodels.LoginViewModelFactory

@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val app = LocalContext.current.applicationContext as AcademiaApp
    val factory = remember { LoginViewModelFactory(app.container.loginRepository) }
    val loginViewModel: LoginViewModel = viewModel(factory = factory)
    val uiState by loginViewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberUser by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            navController.navigate("chat") { popUpTo("login") { inclusive = true } }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Fondo con imagen (usa un recurso local, por ejemplo R.drawable.academia_bg)
        Image(
            painter = painterResource(id = R.drawable.academia_bg),
            contentDescription = "Fondo academia",
            modifier = Modifier.fillMaxSize(),
            alignment = Alignment.Center
        )
        // Card central
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
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
                Text(
                    text = "Iniciar sesión",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black // Mejor contraste
                )
                OutlinedTextField(
                    value = uiState.username,
                    onValueChange = { loginViewModel.onUsernameChange(it) },
                    label = { Text("Usuario", color = Color.Black) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black) // Texto claro al escribir
                )
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { loginViewModel.onPasswordChange(it) },
                    label = { Text("Contraseña", color = Color.Black) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black), // Texto claro al escribir
                    trailingIcon = {
                        val icon = if (passwordVisible) R.drawable.ic_eye else R.drawable.ic_eye_off
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(id = icon),
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = Color.Black // Icono sin transparencia
                            )
                        }
                    }
                )
                if (uiState.error != null) {
                    Text(uiState.error ?: "", color = Color(0xFFB00020))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.White, shape = RoundedCornerShape(4.dp))
                            .border(2.dp, Color.Black, shape = RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Checkbox(
                            checked = rememberUser,
                            onCheckedChange = { rememberUser = it },
                            colors = androidx.compose.material3.CheckboxDefaults.colors(
                                checkedColor = Color.Black,
                                checkmarkColor = Color.White,
                                uncheckedColor = Color.White
                            ),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "Recordar usuario",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                TextButton(onClick = { /* lógica para recuperar contraseña */ }) {
                    Text(
                        text = "¿Olvidaste la contraseña?",
                        color = Color(0xFF1565C0), // Azul oscuro
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                val isFormFilled = uiState.username.isNotBlank() && uiState.password.isNotBlank()
                Button(
                    onClick = {
                        if (isFormFilled) {
                            loginViewModel.onLoginClick()
                        } else {
                            // Aquí podrías mostrar un mensaje de error o simplemente no hacer nada
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.loading,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = if (isFormFilled) Color(0xFF1565C0) else Color(0xFF1565C0).copy(alpha = 0.5f),
                        contentColor = Color.White
                    )
                ) {
                    Text(if (uiState.loading) "Accediendo..." else "Acceder")
                }
            }
        }
    }
}
