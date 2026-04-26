package com.andresilva.greecetripplanner.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Seed = Color(0xFF1C69D4)

private val LightColors = lightColorScheme(
    primary = Seed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4E3FF),
    onPrimaryContainer = Color(0xFF001B3E),
    secondary = Color(0xFF555F71),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD9E3F8),
    onSecondaryContainer = Color(0xFF121C2B),
    tertiary = Color(0xFF00823B),
    onTertiary = Color.White,
    error = Color(0xFFC91432),
    onError = Color.White,
    background = Color(0xFFF2F2F2),
    onBackground = Color(0xFF262626),
    surface = Color.White,
    onSurface = Color(0xFF262626),
    surfaceVariant = Color(0xFFE6E6E6),
    onSurfaceVariant = Color(0xFF666666),
    outline = Color(0xFFD6D6D6),
    outlineVariant = Color(0xFFB2B2B2),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF4A8DE6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0B4396),
    onPrimaryContainer = Color(0xFFD4E3FF),
    secondary = Color(0xFFBDC7DC),
    onSecondary = Color(0xFF273141),
    secondaryContainer = Color(0xFF3D4758),
    onSecondaryContainer = Color(0xFFD9E3F8),
    tertiary = Color(0xFF2EBD6B),
    onTertiary = Color.White,
    error = Color(0xFFE84060),
    onError = Color.White,
    background = Color(0xFF111111),
    onBackground = Color.White,
    surface = Color(0xFF1A1A1A),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFB2B2B2),
    outline = Color(0xFF4D4D4D),
    outlineVariant = Color(0xFF666666),
)

@Composable
fun GreeceTripTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
