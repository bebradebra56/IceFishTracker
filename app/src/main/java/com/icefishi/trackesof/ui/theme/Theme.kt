package com.icefishi.trackesof.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val IceFishColorScheme = lightColorScheme(
    primary = DeepBlue,
    onPrimary = SnowWhite,
    primaryContainer = LightIceBlue,
    onPrimaryContainer = DeepBlue,
    
    secondary = WaterBlue,
    onSecondary = SnowWhite,
    secondaryContainer = IceShadow,
    onSecondaryContainer = DarkWater,
    
    tertiary = AccentBlue,
    onTertiary = SnowWhite,
    
    background = LightIceBlue,
    onBackground = DeepBlue,
    
    surface = FrostedWhite,
    onSurface = DeepBlue,
    surfaceVariant = IceBlue,
    onSurfaceVariant = DarkWater,
    
    outline = IceShadow,
    outlineVariant = DarkIceBlue
)

@Composable
fun IceFishTrackerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = IceFishColorScheme,
        typography = Typography,
        content = content
    )
}