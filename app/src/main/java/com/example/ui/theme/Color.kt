package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val NovaObsidian = Color(0xFF05060D)
val NovaNavyDeep = Color(0xFF0A0E1E)
val NovaNavySurface = Color(0xFF10152B)
val NovaNavyCard = Color(0xFF161C38)
val NovaNavyElevated = Color(0xFF1E264A)

val NovaViolet = Color(0xFF8B5CF6)
val NovaPurple = Color(0xFFA855F7)
val NovaIndigo = Color(0xFF6366F1)
val NovaLavender = Color(0xFFC4B5FD)
val NovaMagentaGlow = Color(0xFFD946EF)
val NovaCyanAccent = Color(0xFF38BDF8)

val NovaTextPrimary = Color(0xFFF8FAFC)
val NovaTextSecondary = Color(0xFF94A3B8)
val NovaTextMuted = Color(0xFF64748B)
val NovaOutline = Color(0xFF28315A)
val NovaOutlineSoft = Color(0x338B5CF6)

val NovaSuccess = Color(0xFF34D399)
val NovaWarning = Color(0xFFFBBF24)
val NovaError = Color(0xFFF87171)

// Light fallback palette (used only if user toggles Dark Cosmic Mode off in Settings)
val NovaLightBackground = Color(0xFFF5F3FF)
val NovaLightSurface = Color(0xFFFFFFFF)
val NovaLightSurfaceVariant = Color(0xFFEDE9FE)
val NovaLightTextPrimary = Color(0xFF0F172A)
val NovaLightTextSecondary = Color(0xFF475569)

val NovaPrimaryGradient = Brush.linearGradient(
    colors = listOf(NovaIndigo, NovaViolet, NovaPurple)
)

val NovaCardBorderGradient = Brush.linearGradient(
    colors = listOf(
        NovaViolet.copy(alpha = 0.55f),
        NovaIndigo.copy(alpha = 0.25f),
        NovaPurple.copy(alpha = 0.45f)
    )
)

val NovaHeroOverlayGradient = Brush.verticalGradient(
    colors = listOf(
        Color.Transparent,
        NovaObsidian.copy(alpha = 0.68f),
        NovaObsidian.copy(alpha = 0.96f)
    )
)
