package com.example.academiaapp.ui.components

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.RowScope
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.toArgb

@Suppress("DEPRECATION")
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showNavIcon: Boolean = true,
    onNavClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    height: Dp = 56.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    centerTitle: Boolean = false
) {
    Log.d("AppTopBar", "Composing AppTopBar(title='$title', showNavIcon=$showNavIcon)")
    // Pintar la statusBar con el mismo color del top bar para evitar aumentar el layout
    val view = LocalView.current
    SideEffect {
        try {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = backgroundColor.toArgb()
                // Ajustar iconos de status bar según luminancia: true => dark icons on light background
                val isLightBg = (backgroundColor.red * 0.2126f + backgroundColor.green * 0.7152f + backgroundColor.blue * 0.0722f) > 0.6f
                try {
                    val controller = WindowCompat.getInsetsController(window, view)
                    controller.isAppearanceLightStatusBars = isLightBg
                } catch (e: Exception) {
                    Log.w("AppTopBar", "No se pudo ajustar isAppearanceLightStatusBars: ${e.message}")
                }
            } else {
                Log.w("AppTopBar", "No hay window disponible desde el LocalView.context")
            }
        } catch (e: Throwable) {
            Log.e("AppTopBar", "Error en SideEffect al setear statusBarColor", e)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(height)
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            if (showNavIcon) {
                IconButton(onClick = onNavClick) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Abrir menú")
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (centerTitle) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    color = contentColor,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    color = contentColor,
                    textAlign = TextAlign.Start
                )
            }

            // acciones opcionales a la derecha
            Row { actions() }
        }
    }
}
