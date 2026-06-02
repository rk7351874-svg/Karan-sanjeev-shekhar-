package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PremiumDarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    secondary = MintGreen,
    tertiary = NeonPurpleGlow,
    background = ObsidianBackground,
    surface = ObsidianSurface,
    onPrimary = TextCrispWhite,
    onSecondary = Color(0xFF052B10),
    onBackground = TextCrispWhite,
    onSurface = TextCrispWhite,
    outline = GlassBorderTint,
    surfaceVariant = GlassTint,
    onSurfaceVariant = TextMutedGrey
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force our premium dark mode by default
    dynamicColor: Boolean = false, // Disable dynamic colors to maintain custom artistic styling
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PremiumDarkColorScheme,
        typography = Typography,
        content = content
    )
}
