package com.tecsup.stockmanager.ui.theme

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

// Colores oscuro mode definidos localmente para evitar referencia circular
private val OnBackgroundDark = Color(0xFFFFF3EE)
private val OnSurfaceDark    = Color(0xFFFFF3EE)

private val EsquemaClaro = lightColorScheme(
    primary            = CoralPrimary,
    onPrimary          = WarmWhite,
    primaryContainer   = CoralLight,
    onPrimaryContainer = TextDark,
    secondary          = CoralLight,
    onSecondary        = WarmWhite,
    background         = WarmBg,
    onBackground       = TextDark,
    surface            = WarmWhite,
    onSurface          = TextDark,
    onSurfaceVariant   = TextMutedWarm,
    error              = Rojo,
    onError            = WarmWhite
)

private val EsquemaOscuro = darkColorScheme(
    primary            = CoralLight,
    onPrimary          = WarmBgDark,
    primaryContainer   = CoralDark,
    onPrimaryContainer = WarmWhite,
    secondary          = CoralPrimary,
    onSecondary        = WarmBgDark,
    background         = WarmBgDark,
    onBackground       = OnBackgroundDark,
    surface            = WarmSurfDark,
    onSurface          = OnSurfaceDark,
    onSurfaceVariant   = TextMutedWarm,
    error              = RojoClaro,
    onError            = Rojo
)

@Composable
fun StockManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> EsquemaOscuro
        else      -> EsquemaClaro
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}