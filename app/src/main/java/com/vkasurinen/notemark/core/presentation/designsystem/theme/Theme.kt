package com.vkasurinen.notemark.core.presentation.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBrand,
    onPrimary = OnPrimaryBrand,
    primaryContainer = PrimaryBrand10,

    surface = Surface,
    surfaceVariant = SurfaceLowest,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,

    error = Error,

    // Optional - set these if needed
    secondary = PrimaryBrand,
    onSecondary = OnPrimaryBrand12,
    tertiary = PrimaryBrand,

    // Set other defaults as needed
    background = Surface,
    onBackground = OnSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBrand,
    onPrimary = OnPrimaryBrand,
    primaryContainer = PrimaryBrand10,

    surface = Color(0xFF121212),
    surfaceVariant = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFFA0A0A0),

    error = Error,

    // Optional - same as light unless different
    secondary = PrimaryBrand,
    onSecondary = OnPrimaryBrand12,
    tertiary = PrimaryBrand,

    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0)
)

@Composable
fun NoteMarkTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = NoteMarkTypography,
        content = content
    )
}

//@Composable
//fun NoteMarkTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    dynamicColor: Boolean = true,
//    content: @Composable () -> Unit
//) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = NoteMarkTypography,
//        content = content
//    )
//}