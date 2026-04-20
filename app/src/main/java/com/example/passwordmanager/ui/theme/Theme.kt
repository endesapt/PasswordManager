package com.example.passwordmanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightScheme = lightColorScheme(
    primary = Moss,
    onPrimary = Snow,
    secondary = Coral,
    onSecondary = Snow,
    background = Snow,
    onBackground = Ink,
    surface = Sand,
    onSurface = Ink,
)

private val DarkScheme = darkColorScheme(
    primary = Sand,
    onPrimary = Ink,
    secondary = Coral,
    background = Slate,
    onBackground = Snow,
    surface = Ink,
    onSurface = Snow,
)

@Composable
fun PasswordManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = Typography,
        content = content,
    )
}
