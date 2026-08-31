package com.souqrodina.accounting.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryNavy,
    onPrimary = Color.White,
    primaryContainer = PrimaryNavyLight,
    onPrimaryContainer = PrimaryNavyDark,
    secondary = AccentEmerald,
    onSecondary = Color.White,
    background = BackgroundCoolGray,
    surface = SurfaceWhite,
    onSurface = TextSlateDark,
    surfaceVariant = Color(0xFFF1F5F9),
    outline = BorderSlate
)

object SouqRodinaTheme {
    val colors: androidx.compose.material3.ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme
}

@Composable
fun SouqRodinaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}