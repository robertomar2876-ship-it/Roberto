package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val AcademicLightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    primaryContainer = NavyContainer,
    onPrimaryContainer = OnNavyContainer,
    secondary = SoftBlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = SoftBlueContainer,
    onSecondaryContainer = OnSoftBlueContainer,
    tertiary = SageTertiary,
    onTertiary = Color.White,
    tertiaryContainer = SageContainer,
    onTertiaryContainer = OnSageContainer,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    error = AlertErrorLight,
    onError = Color.White,
    errorContainer = AlertErrorContainerLight,
    onErrorContainer = Color(0xFF410E0B)
)

private val AcademicDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = OnDarkPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = Color(0xFFE1EEFA),
    secondary = DarkSecondary,
    onSecondary = Color(0xFF0A2438),
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = Color(0xFFD6E9F6),
    tertiary = DarkTertiary,
    onTertiary = Color(0xFF0F2B1C),
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = Color(0xFFD5EBDF),
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkOutline,
    outlineVariant = Color(0xFF283A50),
    error = AlertErrorDark,
    onError = Color(0xFF601410),
    errorContainer = AlertErrorContainerDark,
    onErrorContainer = Color(0xFFF9DEDC)
)

private val HighContrastLightScheme = AcademicLightColorScheme.copy(
    primary = HighContrastLightPrimary,
    onPrimary = Color.White,
    background = HighContrastLightBg,
    onBackground = HighContrastLightText,
    surface = HighContrastLightSurface,
    onSurface = HighContrastLightText,
    onSurfaceVariant = Color(0xFF1A2A3D),
    outline = Color(0xFF2B3E54)
)

private val HighContrastDarkScheme = AcademicDarkColorScheme.copy(
    primary = HighContrastDarkPrimary,
    onPrimary = Color.Black,
    background = HighContrastDarkBg,
    onBackground = HighContrastDarkText,
    surface = HighContrastDarkSurface,
    onSurface = HighContrastDarkText,
    onSurfaceVariant = Color(0xFFE2EBF5),
    outline = Color(0xFF9BB2C9)
)

val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(26.dp)
)

@Composable
fun PsicologiaParaTodosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrast: Boolean = false,
    fontScale: Float = 1.0f,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        highContrast && darkTheme -> HighContrastDarkScheme
        highContrast && !darkTheme -> HighContrastLightScheme
        darkTheme -> AcademicDarkColorScheme
        else -> AcademicLightColorScheme
    }
    val typography = remember(fontScale) { buildAppTypography(fontScale) }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = AppShapes,
        content = content
    )
}
