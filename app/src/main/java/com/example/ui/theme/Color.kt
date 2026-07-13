package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val CoreBase = Color(0xFF09090B) // Dark grey 1 (Main base)
val CoreSurface = Color(0xFF18181B) // Dark grey 2 (Cards)
val CoreBorder = Color(0xFF27272A) // Dark grey 3 (Borders)
val MutedText = Color(0xFFA1A1AA) // Text
val CoreText = Color(0xFFFAFAFA)

val NeonBlue = Color(0xFF3B82F6) // Single primary accent color

val CyberRed = Color(0xFFEF4444) // Error 
val GreenSync = Color(0xFF10B981) // Success
val LightGrey = CoreText
val MutedGrey = MutedText
val DarkVoid = CoreBase
val GlassBg = CoreSurface
val CardBg = CoreSurface
val SurfaceContainer = CoreSurface
val SurfaceContainerHigh = CoreBorder

val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = NeonBlue,
    onPrimary = Color.White,
    background = CoreBase,
    onBackground = CoreText,
    surface = CoreSurface,
    onSurface = CoreText,
    surfaceVariant = CoreBorder,
    onSurfaceVariant = MutedText,
    error = CyberRed,
    onError = Color.White
)
