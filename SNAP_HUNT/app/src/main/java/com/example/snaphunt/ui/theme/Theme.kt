package com.example.snaphunt.ui.theme

import android.app.Activity
import android.hardware.lights.Light
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.snaphunt.data.models.ColorPalette
import com.example.snaphunt.data.models.AppTheme

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC6),
    tertiary = Color(0xFFCF6679),

    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),

    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,

    onBackground = Color.White,
    onSurface = Color.White
)

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC6),
    tertiary = Color(0xFF018786),

    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF5F5F5),

    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,

    onBackground = Color.Black,
    onSurface = Color.Black
)

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */

@Composable
fun SnapHuntTheme(
    theme: AppTheme,
    // Dynamic color is available on Android 12+
    palette: ColorPalette,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val darkTheme = when (theme) {
        AppTheme.Light -> false
        AppTheme.Dark -> true
        AppTheme.System -> isSystemInDarkTheme()
    }
    val colorScheme = when {
        !dynamicColor -> {
            when (palette) {
                ColorPalette.Default ->
                    if (darkTheme) DarkColorScheme else LightColorScheme

                ColorPalette.Blue ->
                    if (darkTheme) DarkBluePalette else BluePalette

                ColorPalette.Green ->
                    if (darkTheme) DarkGreenPalette else GreenPalette

                ColorPalette.Orange ->
                    if (darkTheme) DarkOrangePalette else OrangePalette
            }
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        else -> {
            if (darkTheme) DarkColorScheme else LightColorScheme
        }
    }

    // Salta l'operazione se stiamo visualizzando il componente tramite dei devtools (es. preview)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect { // Esegue il blocco al termine di ogni recomposition
            val window = (view.context as Activity).window
// Cambio del colore della status bar per Android <= 14
            window.statusBarColor = colorScheme.primaryContainer.toArgb()
// Cambio del colore della status bar per Android 15+
            WindowCompat
                .getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}