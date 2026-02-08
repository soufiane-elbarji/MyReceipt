
package com.myreceipt.presentation.ui.theme

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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

// Dark Color Scheme - Apple-inspired
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF007AFF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0055B3),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF5856D6),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3D3C99),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF34C759),
    onTertiary = Color.White,
    background = Color(0xFF000000),
    onBackground = Color(0xFFF2F2F7),
    surface = Color(0xFF1C1C1E),
    onSurface = Color(0xFFF2F2F7),
    surfaceVariant = Color(0xFF2C2C2E),
    onSurfaceVariant = Color(0xFF8E8E93),
    outline = Color(0xFF48484A),
    outlineVariant = Color(0xFF3A3A3C),
    error = Color(0xFFFF3B30),
    onError = Color.White
)

// Light Color Scheme - Apple-inspired
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF007AFF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF5856D6),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE5E4FF),
    onSecondaryContainer = Color(0xFF1A1A59),
    tertiary = Color(0xFF34C759),
    onTertiary = Color.White,
    background = Color(0xFFF2F2F7),
    onBackground = Color(0xFF1C1C1E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1C1E),
    surfaceVariant = Color(0xFFF2F2F7),
    onSurfaceVariant = Color(0xFF8E8E93),
    outline = Color(0xFFC6C6C8),
    outlineVariant = Color(0xFFE5E5EA),
    error = Color(0xFFFF3B30),
    onError = Color.White
)

// Category colors for spending visualization
val CategoryColors = mapOf(
    "Groceries" to Color(0xFF34C759),
    "Dining" to Color(0xFFFF9500),
    "Shopping" to Color(0xFFFF2D55),
    "Transportation" to Color(0xFF007AFF),
    "Healthcare" to Color(0xFF5856D6),
    "Entertainment" to Color(0xFFAF52DE),
    "Utilities" to Color(0xFF00C7BE),
    "Other" to Color(0xFF8E8E93)
)

/**
 * Get color for a spending category.
 */
fun getCategoryColor(category: String): Color {
    return CategoryColors[category] ?: CategoryColors["Other"]!!
}

/**
 * MyReceipt Theme.
 */
@Composable
fun MyReceiptTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    // Animate color transitions
    val animatedColorScheme = colorScheme.copy(
        primary = animateColorAsState(colorScheme.primary, tween(300), label = "primary").value,
        background = animateColorAsState(colorScheme.background, tween(300), label = "background").value,
        surface = animateColorAsState(colorScheme.surface, tween(300), label = "surface").value,
        onBackground = animateColorAsState(colorScheme.onBackground, tween(300), label = "onBackground").value,
        onSurface = animateColorAsState(colorScheme.onSurface, tween(300), label = "onSurface").value
    )
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
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
