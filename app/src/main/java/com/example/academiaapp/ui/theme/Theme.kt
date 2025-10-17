package com.example.academiaapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Material 3 color schemes (WhatsApp-inspired palette)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF128C7E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD0F0EA),
    onPrimaryContainer = Color(0xFF083A35),

    secondary = Color(0xFF25D366),
    onSecondary = Color(0xFF003017),
    secondaryContainer = Color(0xFFCFF7E0),
    onSecondaryContainer = Color(0xFF083A24),

    surface = Color(0xFFECE5DD),
    onSurface = Color(0xFF111111),
    surfaceVariant = Color(0xFFE8E8E8),
    onSurfaceVariant = Color(0xFF222222),

    background = Color(0xFFECE5DD),
    onBackground = Color(0xFF111111),

    outline = Color(0xFFB0B0B0)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1EBEA5),
    onPrimary = Color(0xFF00110E),
    primaryContainer = Color(0xFF0D3B36),
    onPrimaryContainer = Color(0xFFD0F0EA),

    secondary = Color(0xFF2EE39F),
    onSecondary = Color(0xFF00170C),
    secondaryContainer = Color(0xFF0E3C2A),
    onSecondaryContainer = Color(0xFFCFF7E0),

    surface = Color(0xFF0B141A),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF1F2C34),
    onSurfaceVariant = Color(0xFFE0E0E0),

    background = Color(0xFF0B141A),
    onBackground = Color(0xFFE0E0E0),

    outline = Color(0xFF5A6A70)
)

// App-specific semantic colors (centralized tokens)
data class AppColors(
    val chatUserBubbleBg: Color,
    val chatUserBubbleText: Color,
    val chatAssistantBubbleBg: Color,
    val chatAssistantBubbleText: Color,
    val chatBackground: Color,
    val bottomBarSurface: Color,
    val topAppBarTitle: Color
)

val LocalAppColors = staticCompositionLocalOf {
    AppColors(
        chatUserBubbleBg = Color(0xFFDCF8C6),
        chatUserBubbleText = Color(0xFF000000),
        chatAssistantBubbleBg = Color(0xFFFFFFFF),
        chatAssistantBubbleText = Color(0xFF000000),
        chatBackground = Color(0xFFECE5DD),
        bottomBarSurface = Color(0xFFE8E8E8),
        topAppBarTitle = Color(0xFF111111)
    )
}

@Composable
fun AcademiaAPPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val appColors = if (darkTheme) {
        AppColors(
            chatUserBubbleBg = Color(0xFFDCF8C6),
            chatUserBubbleText = Color(0xFF000000),
            chatAssistantBubbleBg = Color(0xFFFFFFFF),
            chatAssistantBubbleText = Color(0xFF000000),
            chatBackground = DarkColorScheme.surface,
            bottomBarSurface = DarkColorScheme.surfaceVariant,
            topAppBarTitle = DarkColorScheme.onSurface
        )
    } else {
        AppColors(
            chatUserBubbleBg = Color(0xFFDCF8C6),
            chatUserBubbleText = Color(0xFF000000),
            chatAssistantBubbleBg = Color(0xFFFFFFFF),
            chatAssistantBubbleText = Color(0xFF000000),
            chatBackground = LightColorScheme.surface,
            bottomBarSurface = LightColorScheme.surfaceVariant,
            topAppBarTitle = LightColorScheme.onSurface
        )
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}