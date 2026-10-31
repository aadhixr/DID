package com.diskwala.recreated.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val DarkBackground = Color(0xFF0F0F13)
val DarkSurface = Color(0xFF181820)
val DarkCard = Color(0xFF22222E)
val AccentPurple = Color(0xFF7C4DFF)
val AccentCyan = Color(0xFF00E5FF)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFAAAAAA)

val DarkThemeColors = darkColorScheme(
    primary = AccentPurple,
    secondary = AccentCyan,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)
