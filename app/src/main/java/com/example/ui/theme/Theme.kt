package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Nayab AI Dark Theme Palette
private val DarkColorScheme = darkColorScheme(
    primary = SindhiTealLight,
    onPrimary = Color(0xFF003735),
    primaryContainer = SindhiTealDark,
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = AjrakMaroonLight,
    onSecondary = Color.White,
    secondaryContainer = AjrakMaroonDark,
    onSecondaryContainer = Color(0xFFFFD8D8),
    tertiary = WarmAmber,
    onTertiary = Color(0xFF451A03),
    background = DarkSurface,
    onBackground = Color(0xFFF1F5F9),
    surface = DarkCard,
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8)
)

// Nayab AI Light Theme Palette
private val LightColorScheme = lightColorScheme(
    primary = SindhiTeal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Color(0xFF115E59),
    secondary = AjrakMaroon,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE4E6),
    onSecondaryContainer = Color(0xFF881337),
    tertiary = WarmAmber,
    onTertiary = Color.White,
    background = LightSurface,
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                @Suppress("DEPRECATION")
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
