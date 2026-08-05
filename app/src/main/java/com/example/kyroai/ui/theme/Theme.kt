package com.example.kyroai.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

private val DarkColorScheme = darkColorScheme(
    primary = KyroAccentBlue,
    onPrimary = KyroTextPrimaryDark,
    primaryContainer = KyroAccentGlowSoft,
    onPrimaryContainer = KyroAccentBlue,
    secondary = KyroAccentBlueHover,
    background = KyroBlack,
    onBackground = KyroTextPrimaryDark,
    surface = KyroDarkSurface,
    onSurface = KyroTextPrimaryDark,
    surfaceVariant = KyroDarkCard,
    onSurfaceVariant = KyroTextSecondaryDark,
    outline = KyroDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = KyroAccentBlue,
    onPrimary = KyroTextPrimaryDark,
    primaryContainer = KyroAccentGlowSoft,
    onPrimaryContainer = KyroAccentBlue,
    secondary = KyroAccentBlueHover,
    background = KyroLightBg,
    onBackground = KyroTextPrimaryLight,
    surface = KyroLightSurface,
    onSurface = KyroTextPrimaryLight,
    surfaceVariant = KyroLightCard,
    onSurfaceVariant = KyroTextSecondaryLight,
    outline = KyroLightBorder
)

@Composable
fun KyroAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
