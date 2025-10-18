package uk.co.developmentanddinosaurs.stego.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Orange500 = Color(0xFFFF9800)
private val Orange700 = Color(0xFFF57C00)
private val Orange300 = Color(0xFFFFB74D)

private val OrangeLightColorScheme = lightColorScheme(
    primary = Orange500,
    onPrimary = Color.White,
    primaryContainer = Orange300,
    onPrimaryContainer = Color(0xFF2B1700),

    secondary = Orange300,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFD79B),
    onSecondaryContainer = Color(0xFF2B1700),

    tertiary = Color(0xFFFF7043),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDBCF),
    onTertiaryContainer = Color(0xFF2B1700),

    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF0E0E6),
    onSurfaceVariant = Color(0xFF4F444A),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF827379),
    inverseOnSurface = Color(0xFF323034),
    inverseSurface = Color(0xFF323034),
    inversePrimary = Orange300,
    surfaceTint = Orange500,
    outlineVariant = Color(0xFFD3C2C8),
    scrim = Color(0xFF000000),
)

private val OrangeDarkColorScheme = darkColorScheme(
    primary = Orange300,
    onPrimary = Color(0xFF472A00),
    primaryContainer = Orange700,
    onPrimaryContainer = Color(0xFFFFCC80),

    secondary = Orange300,
    onSecondary = Color(0xFF472A00),
    secondaryContainer = Color(0xFF653E00),
    onSecondaryContainer = Color(0xFFFFD79B),

    tertiary = Orange300,
    onTertiary = Color(0xFF472A00),
    tertiaryContainer = Color(0xFF653E00),
    onTertiaryContainer = Color(0xFFFFDBCF),

    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E6),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E6),
    surfaceVariant = Color(0xFF4F444A),
    onSurfaceVariant = Color(0xFFD3C2C8),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFF9C8D93),
    inverseOnSurface = Color(0xFF1C1B1F),
    inverseSurface = Color(0xFFE6E1E6),
    inversePrimary = Orange500,
    surfaceTint = Orange300,
    outlineVariant = Color(0xFF4F444A),
    scrim = Color(0xFF000000),
)

@Composable
fun StegoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> OrangeDarkColorScheme
        else -> OrangeLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}