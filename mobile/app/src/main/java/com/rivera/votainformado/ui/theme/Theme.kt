package com.rivera.votainformado.ui.theme

import android.app.Activity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


private val LightColorSchemeModern = lightColorScheme(
    // Primarios - Azul institucional
    primary = InstitutionalBlue,
    onPrimary = NeutralWhite,
    primaryContainer = Color(0xFFD3E4FD),
    onPrimaryContainer = InstitutionalBlueDark,

    // Secundarios - Verde cívico
    secondary = CivicGreen,
    onSecondary = NeutralWhite,
    secondaryContainer = Color(0xFFC8E6C9),
    onSecondaryContainer = CivicGreenDark,

    // Terciarios - Ámbar para acentos
    tertiary = AccentAmber,
    onTertiary = NeutralWhite,
    tertiaryContainer = Color(0xFFFFE0B2),
    onTertiaryContainer = Color(0xFFE65100),

    // Estados
    error = ErrorRed,
    onError = NeutralWhite,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    // Fondos y superficies
    background = NeutralLight,
    onBackground = NeutralDark,
    surface = NeutralWhite,
    onSurface = NeutralDark,
    surfaceVariant = NeutralGray,
    onSurfaceVariant = NeutralMedium,

    // Outline y otros
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    scrim = Color(0xFF000000),
    inverseSurface = NeutralDark,
    inverseOnSurface = NeutralWhite,
    inversePrimary = InstitutionalBlueLight
)

private val DarkColorSchemeModern = darkColorScheme(
    // Primarios
    primary = InstitutionalBlueLight,
    onPrimary = Color(0xFF003258),
    primaryContainer = InstitutionalBlueDark,
    onPrimaryContainer = Color(0xFFD3E4FD),

    // Secundarios
    secondary = CivicGreenLight,
    onSecondary = Color(0xFF003300),
    secondaryContainer = CivicGreenDark,
    onSecondaryContainer = Color(0xFFC8E6C9),

    // Terciarios
    tertiary = Color(0xFFFFB74D),
    onTertiary = Color(0xFF4E2700),
    tertiaryContainer = Color(0xFFE65100),
    onTertiaryContainer = Color(0xFFFFE0B2),

    // Estados
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    // Fondos y superficies
    background = DarkBg,
    onBackground = Color(0xFFE3E8EF),
    surface = DarkSurf,
    onSurface = Color(0xFFE3E8EF),
    surfaceVariant = DarkSurfVar,
    onSurfaceVariant = Color(0xFFC5CAD3),

    // Outline y otros
    outline = Color(0xFF8F949E),
    outlineVariant = Color(0xFF43474E),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE3E8EF),
    inverseOnSurface = DarkBg,
    inversePrimary = InstitutionalBlue
)

@Composable
fun VotaInformadoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Cambiado a false para usar nuestros colores personalizados
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorSchemeModern // Cambiar a DarkColorSchemeModern para versión 2
        else -> LightColorSchemeModern // Cambiar a LightColorSchemeModern para versión 2
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TypographyEnhanced,
        content = content
    )
}