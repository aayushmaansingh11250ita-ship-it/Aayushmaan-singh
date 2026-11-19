package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val NovaDarkColorScheme = darkColorScheme(
    primary = NovaViolet,
    onPrimary = Color.White,
    primaryContainer = NovaNavyElevated,
    onPrimaryContainer = NovaLavender,
    secondary = NovaPurple,
    onSecondary = Color.White,
    secondaryContainer = NovaNavyCard,
    onSecondaryContainer = NovaLavender,
    tertiary = NovaCyanAccent,
    onTertiary = NovaObsidian,
    background = NovaObsidian,
    onBackground = NovaTextPrimary,
    surface = NovaNavyDeep,
    onSurface = NovaTextPrimary,
    surfaceVariant = NovaNavyCard,
    onSurfaceVariant = NovaTextSecondary,
    outline = NovaOutline,
    outlineVariant = NovaOutlineSoft,
    error = NovaError,
    onError = NovaObsidian
)

private val NovaLightColorScheme = lightColorScheme(
    primary = NovaIndigo,
    onPrimary = Color.White,
    primaryContainer = NovaLightSurfaceVariant,
    onPrimaryContainer = NovaIndigo,
    secondary = NovaPurple,
    onSecondary = Color.White,
    background = NovaLightBackground,
    onBackground = NovaLightTextPrimary,
    surface = NovaLightSurface,
    onSurface = NovaLightTextPrimary,
    surfaceVariant = NovaLightSurfaceVariant,
    onSurfaceVariant = NovaLightTextSecondary,
    outline = Color(0xFFCBD5E1),
    error = Color(0xFFDC2626),
    onError = Color.White
)

val NovaShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
fun NovaAiTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) NovaDarkColorScheme else NovaLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = NovaTypography,
        shapes = NovaShapes,
        content = content
    )
}
