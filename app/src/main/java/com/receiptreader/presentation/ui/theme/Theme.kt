package com.receiptreader.presentation.ui.theme

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * ReceiptVault Premium Color Palette
 * Apple-inspired minimalist luxury design
 */

// Primary Blue (Premium tech feel)
val PrimaryBlue = Color(0xFF007AFF)
val PrimaryBlueLight = Color(0xFF5AC8FA)
val PrimaryBlueDark = Color(0xFF0051A8)

// Accent - Mint Green for success/savings
val AccentMint = Color(0xFF34C759)
val AccentMintDark = Color(0xFF248A3D)

// Dark theme surfaces - Deep rich blacks
val DarkBackground = Color(0xFF000000)
val DarkSurface = Color(0xFF1C1C1E)
val DarkSurfaceElevated = Color(0xFF2C2C2E)
val DarkCard = Color(0xFF1C1C1E)

// Light theme surfaces - Warm whites
val LightBackground = Color(0xFFF2F2F7)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceElevated = Color(0xFFFFFFFF)
val LightCard = Color(0xFFFFFFFF)

// Text colors - High contrast
val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xFF8E8E93)
val TextTertiaryDark = Color(0xFF636366)
val TextPrimaryLight = Color(0xFF000000)
val TextSecondaryLight = Color(0xFF3C3C43)
val TextTertiaryLight = Color(0xFF8A8A8E)

// Category colors - Vibrant and distinct
val CategoryGroceries = Color(0xFF34C759)   // Green
val CategoryDining = Color(0xFFFF9500)      // Orange
val CategoryShopping = Color(0xFF007AFF)    // Blue
val CategoryTransportation = Color(0xFFAF52DE) // Purple
val CategoryHealthcare = Color(0xFFFF2D55)  // Pink
val CategoryEntertainment = Color(0xFF5856D6) // Indigo
val CategoryUtilities = Color(0xFF8E8E93)   // Gray
val CategoryOther = Color(0xFFFF3B30)       // Red

// Semantic colors
val Success = Color(0xFF34C759)
val Warning = Color(0xFFFF9500)
val Error = Color(0xFFFF3B30)
val Info = Color(0xFF007AFF)

/**
 * Dark color scheme - Premium dark mode
 */
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = Color.White,
    
    secondary = AccentMint,
    onSecondary = Color.White,
    secondaryContainer = AccentMintDark,
    onSecondaryContainer = Color.White,
    
    tertiary = PrimaryBlueLight,
    onTertiary = Color.Black,
    
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondaryDark,
    
    outline = Color(0xFF48484A),
    outlineVariant = Color(0xFF38383A),
    
    error = Error,
    onError = Color.White
)

/**
 * Light color scheme - Clean minimal light mode
 */
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryBlueLight.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryBlueDark,
    
    secondary = AccentMint,
    onSecondary = Color.White,
    secondaryContainer = AccentMint.copy(alpha = 0.2f),
    onSecondaryContainer = AccentMintDark,
    
    tertiary = PrimaryBlueDark,
    onTertiary = Color.White,
    
    background = LightBackground,
    onBackground = TextPrimaryLight,
    
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    
    surfaceVariant = Color(0xFFE5E5EA),
    onSurfaceVariant = TextSecondaryLight,
    
    outline = Color(0xFFD1D1D6),
    outlineVariant = Color(0xFFE5E5EA),
    
    error = Error,
    onError = Color.White
)

/**
 * Premium ReceiptVault Theme
 */
@Composable
fun ReceiptVaultTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    // Smooth color transitions
    val animatedColorScheme = colorScheme.copy(
        primary = animateColorAsState(
            targetValue = colorScheme.primary,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "primary"
        ).value,
        background = animateColorAsState(
            targetValue = colorScheme.background,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "background"
        ).value,
        surface = animateColorAsState(
            targetValue = colorScheme.surface,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "surface"
        ).value
    )
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    
    MaterialTheme(
        colorScheme = animatedColorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Get color for a spending category
 */
fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "groceries" -> CategoryGroceries
        "dining" -> CategoryDining
        "shopping" -> CategoryShopping
        "transportation" -> CategoryTransportation
        "healthcare" -> CategoryHealthcare
        "entertainment" -> CategoryEntertainment
        "utilities" -> CategoryUtilities
        else -> CategoryOther
    }
}
