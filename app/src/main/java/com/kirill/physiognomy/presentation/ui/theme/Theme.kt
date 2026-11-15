package com.kirill.physiognomy.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.platform.LocalContext

enum class ThemeMode(val storageKey: String, val label: String) {
    System("system", "Системная"),
    Light("light", "Светлая"),
    Dark("dark", "Тёмная");

    companion object {
        fun fromKey(key: String?): ThemeMode = entries.firstOrNull { it.storageKey == key } ?: System
    }
}

@Immutable
class ThemeController internal constructor(private val preferences: SharedPreferences) {

    var mode by mutableStateOf(ThemeMode.fromKey(preferences.getString(KEY_THEME_MODE, null)))
        private set

    fun selectMode(newMode: ThemeMode) {
        if (mode == newMode) return
        mode = newMode
        preferences.edit().putString(KEY_THEME_MODE, newMode.storageKey).apply()
    }

    private companion object {
        const val PREFS_NAME = "physiognomy_settings"
        const val KEY_THEME_MODE = "theme_mode"
    }
}

@Composable
fun rememberThemeController(): ThemeController {
    val context = LocalContext.current
    return remember(context) {
        ThemeController(context.getSharedPreferences("physiognomy_settings", Context.MODE_PRIVATE))
    }
}

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7C6BFF),
    onPrimary = Color(0xFF15102E),
    primaryContainer = Color(0xFF2C2263),
    onPrimaryContainer = Color(0xFFE7E2FF),
    secondary = Color(0xFF9D8CFF),
    onSecondary = Color(0xFF15102E),
    secondaryContainer = Color(0xFF25203D),
    onSecondaryContainer = Color(0xFFDAD3FF),
    tertiary = Color(0xFF64D2C0),
    onTertiary = Color(0xFF04312B),
    background = Color(0xFF09090D),
    onBackground = Color(0xFFF1F0F7),
    surface = Color(0xFF111118),
    onSurface = Color(0xFFF1F0F7),
    surfaceVariant = Color(0xFF1B1B25),
    onSurfaceVariant = Color(0xFF9E9CAC),
    outline = Color(0xFF32323F),
    outlineVariant = Color(0xFF23232E),
    surfaceContainerLowest = Color(0xFF0C0C12),
    surfaceContainerLow = Color(0xFF121219),
    surfaceContainer = Color(0xFF16161F),
    surfaceContainerHigh = Color(0xFF1C1C26),
    surfaceContainerHighest = Color(0xFF23232E),
    inverseSurface = Color(0xFFE9E8F0),
    inverseOnSurface = Color(0xFF16161F),
    scrim = Color(0xFF000000),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF5A49D6),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE6E2FF),
    onPrimaryContainer = Color(0xFF1A1153),
    secondary = Color(0xFF6D5FD0),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEDE9FF),
    onSecondaryContainer = Color(0xFF221A4F),
    tertiary = Color(0xFF119C89),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF5F5FB),
    onBackground = Color(0xFF16151C),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF16151C),
    surfaceVariant = Color(0xFFF0EFF8),
    onSurfaceVariant = Color(0xFF5C5A6C),
    outline = Color(0xFFD4D2E3),
    outlineVariant = Color(0xFFE7E5F1),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFAFAFE),
    surfaceContainer = Color(0xFFF4F3FA),
    surfaceContainerHigh = Color(0xFFEEEDF6),
    surfaceContainerHighest = Color(0xFFE8E7F1),
    inverseSurface = Color(0xFF1B1B23),
    inverseOnSurface = Color(0xFFF3F2F9),
    scrim = Color(0xFF000000),
)

@Composable
fun ThemeMode.isDark(): Boolean = when (this) {
    ThemeMode.System -> isSystemInDarkTheme()
    ThemeMode.Light -> false
    ThemeMode.Dark -> true
}

@Composable
fun PhysiognomyTheme(
    mode: ThemeMode = ThemeMode.System,
    content: @Composable () -> Unit,
) {
    val dark = mode.isDark()
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
