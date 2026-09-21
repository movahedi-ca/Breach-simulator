package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color(0xFF001A24),
    primaryContainer = Color(0xFF003648),
    onPrimaryContainer = Color(0xFFB8EAFF),
    secondary = CyberBlue,
    onSecondary = Color(0xFF002030),
    secondaryContainer = Color(0xFF0D3249),
    onSecondaryContainer = Color(0xFFCEEBFF),
    tertiary = CyberIndigo,
    onTertiary = Color.White,
    background = SlateDark,
    onBackground = TextPrimary,
    surface = SlateCard,
    onSurface = TextPrimary,
    surfaceVariant = SlateCardBorder,
    onSurfaceVariant = TextSecondary,
    error = AlertRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}
