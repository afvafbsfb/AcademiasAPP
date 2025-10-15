package com.example.academiaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.academiaapp.ui.screens.login.LoginScreen
import com.example.academiaapp.ui.theme.AcademiaAppTheme

class AcademiaAppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AcademiaAppTheme {
                LoginScreen(onLoginSuccess = { /* Navegación futura */ })
            }
        }
    }
}

