package com.zirnik.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Renkler docs/tasarim.md tablolarından; dinamik renk bilerek kapalı (marka paleti sabit).
private val Light = lightColorScheme(
    primary = Color(0xFF6B1E34), onPrimary = Color.White,
    primaryContainer = Color(0xFFEBCDD3), onPrimaryContainer = Color(0xFF3A0C1B),
    secondary = Color(0xFF7A5A10), onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1DFAE), onSecondaryContainer = Color(0xFF3B2A00),
    tertiary = Color(0xFF2F5D4A), onTertiary = Color.White,
    background = Color(0xFFF6EFE8), onBackground = Color(0xFF241519),
    surface = Color(0xFFFFFBF7), onSurface = Color(0xFF241519), surfaceTint = Color(0xFFFFFBF7),
    surfaceVariant = Color(0xFFEADFD8), onSurfaceVariant = Color(0xFF5C4A4F),
    outline = Color(0xFF85727A), outlineVariant = Color(0xFFDCCFC8),
    error = Color(0xFFB3261E), onError = Color.White,
)

private val Dark = darkColorScheme(
    primary = Color(0xFFE8A8B8), onPrimary = Color(0xFF3A0C1B),
    primaryContainer = Color(0xFF5A1A2E), onPrimaryContainer = Color(0xFFFFD9E0),
    secondary = Color(0xFFD9B65F), onSecondary = Color(0xFF2E2000),
    secondaryContainer = Color(0xFF4A3A10), onSecondaryContainer = Color(0xFFF1DFAE),
    tertiary = Color(0xFF8FC9AE), onTertiary = Color(0xFF0E2E20),
    background = Color(0xFF1A1014), onBackground = Color(0xFFEFE0E3),
    surface = Color(0xFF261A1F), onSurface = Color(0xFFEFE0E3), surfaceTint = Color(0xFF261A1F),
    surfaceVariant = Color(0xFF3A2A30), onSurfaceVariant = Color(0xFFCDBABF),
    outline = Color(0xFF9A858C), outlineVariant = Color(0xFF4A383F),
    error = Color(0xFFFFB4A8), onError = Color(0xFF5C1008),
)

/** Gelir/gider renkleri tema rolü değil; ikisi de yalnızca renkle değil +/− işaretiyle de ayrılır. */
class FlowColors(val income: Color, val expense: Color)

val LocalFlow = staticCompositionLocalOf { FlowColors(Color(0xFF1E6B3A), Color(0xFFA93A1A)) }

@Composable
fun ZirnikTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val flow = if (dark) FlowColors(Color(0xFF6FCB8E), Color(0xFFFF8F70)) else FlowColors(Color(0xFF1E6B3A), Color(0xFFA93A1A))
    CompositionLocalProvider(LocalFlow provides flow) {
        MaterialTheme(
            colorScheme = if (dark) Dark else Light,
            shapes = Shapes(
                small = RoundedCornerShape(8.dp), medium = RoundedCornerShape(12.dp),
                large = RoundedCornerShape(20.dp), extraLarge = RoundedCornerShape(28.dp),
            ),
            content = content,
        )
    }
}
