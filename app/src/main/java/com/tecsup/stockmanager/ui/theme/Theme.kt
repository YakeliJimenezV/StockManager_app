package com.tecsup.stockmanager.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val EsquemaClaro = lightColorScheme(
    primary = Verde60,
    onPrimary = SuperficieClaro,
    primaryContainer = Verde40,
    secondary = Ambar80,
    onSecondary = SuperficieClaro,
    background = FondoClaro,
    surface = SuperficieClaro,
    error = Rojo
)

private val EsquemaOscuro = darkColorScheme(
    primary = Verde40,
    onPrimary = FondoOscuro,
    primaryContainer = Verde60,
    secondary = Ambar40,
    onSecondary = FondoOscuro,
    background = FondoOscuro,
    surface = SuperficieOscuro,
    error = RojoClaro
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
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> EsquemaOscuro
        else -> EsquemaClaro
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}