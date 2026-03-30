package com.example.giga_chat_pet.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = White,
    primaryContainer = Purple90,
    onPrimaryContainer = Purple10,
    secondary = PurpleGrey80,
    onSecondary = White,
    secondaryContainer = PurpleGrey90,
    onSecondaryContainer = PurpleGrey10,
    tertiary = Pink80,
    onTertiary = White,
    tertiaryContainer = Pink90,
    onTertiaryContainer = Pink10,
    error = Red80,
    onError = White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = DarkBackground,
    onBackground = White,
    surface = DarkSurface,
    onSurface = White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = PurpleGrey30
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = White,
    primaryContainer = Purple90,
    onPrimaryContainer = Purple10,
    secondary = PurpleGrey40,
    onSecondary = White,
    secondaryContainer = PurpleGrey90,
    onSecondaryContainer = PurpleGrey10,
    tertiary = Pink40,
    onTertiary = White,
    tertiaryContainer = Pink90,
    onTertiaryContainer = Pink10,
    error = Red40,
    onError = White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = LightBackground,
    onBackground = DarkOnBackground,
    surface = LightSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = PurpleGrey90,
    onSurfaceVariant = PurpleGrey30
)

@Composable
fun GigachatpetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}