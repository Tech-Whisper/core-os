package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkThemeColorScheme = DarkColorScheme

private val LightThemeColorScheme = DarkColorScheme // Reused for consistency since this is a futuristic dark OS mock

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme for cyber OS
  dynamicColor: Boolean = false, // Force custom polished theme
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkThemeColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
