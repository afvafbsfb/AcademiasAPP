package com.example.academiaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.academiaapp.ui.screens.ChatScreen
import com.example.academiaapp.ui.screens.LoginScreen
import com.example.academiaapp.ui.theme.AcademiaAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AcademiaAPPTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") { LoginScreen(navController = navController) }
                    composable("chat?fromLogin={fromLogin}") { backStackEntry ->
                        val fromLogin = backStackEntry.arguments?.getString("fromLogin")?.toBooleanStrictOrNull() ?: false
                        ChatScreen(fromLogin = fromLogin, navController = navController)
                    }
                    // fallback for direct route without query param
                    composable("chat") { ChatScreen(fromLogin = false, navController = navController) }
                }
            }
        }
    }
}
