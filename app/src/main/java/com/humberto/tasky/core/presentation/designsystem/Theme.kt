package com.humberto.tasky.core.presentation.designsystem

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColorScheme = lightColorScheme(
    primary = TaskyBlack,
    onPrimary = TaskyWhite,
    secondary = TaskyLightGreen,
    onSecondary = TaskyBlack,
    tertiary = TaskyLight2,
    onTertiary = TaskyBlack,
    background = TaskyBlack,
    surface = TaskyWhite,
    onSurface = TaskyBlack,
    surfaceVariant = TaskyLight2,
    onSurfaceVariant = TaskyDarkGray,
    error = TaskyError

    /* Other default colors to override
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun TaskyTheme(
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    systemUiController.setStatusBarColor(
        color = TaskyBlack,
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}