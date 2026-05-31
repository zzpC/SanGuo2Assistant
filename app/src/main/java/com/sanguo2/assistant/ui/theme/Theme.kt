package com.sanguo2.assistant.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Red800,
    onPrimary = White,
    primaryContainer = Red100,
    onPrimaryContainer = Red900,
    secondary = Gold700,
    onSecondary = Dark900,
    secondaryContainer = Gold100,
    onSecondaryContainer = Gold800,
    tertiary = Dark700,
    onTertiary = White,
    tertiaryContainer = Blue100,
    onTertiaryContainer = Dark900,
    background = Gray100,
    onBackground = Dark900,
    surface = White,
    onSurface = Dark900,
    surfaceVariant = Gray200,
    onSurfaceVariant = Gray600,
    outline = Gray400,
    error = Red700,
    onError = White,
    errorContainer = Red100,
    onErrorContainer = Red900
)

private val DarkColorScheme = darkColorScheme(
    primary = Red400,
    onPrimary = Dark900,
    primaryContainer = Red700,
    onPrimaryContainer = Red100,
    secondary = Gold400,
    onSecondary = Dark900,
    secondaryContainer = Gold800,
    onSecondaryContainer = Gold100,
    tertiary = Blue200,
    onTertiary = Dark900,
    tertiaryContainer = Dark700,
    onTertiaryContainer = Blue100,
    background = Dark900,
    onBackground = Gray200,
    surface = Dark800,
    onSurface = Gray200,
    surfaceVariant = Dark700,
    onSurfaceVariant = Gray400,
    outline = Gray600,
    error = Red400,
    onError = Dark900,
    errorContainer = Red700,
    onErrorContainer = Red100
)

@Composable
fun SanGuo2AssistantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
