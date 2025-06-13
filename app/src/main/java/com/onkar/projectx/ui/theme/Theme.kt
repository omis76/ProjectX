package com.onkar.projectx.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val BlackAndWhiteColorScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,

    secondary = Color.Gray,
    onSecondary = Color.Black,

    background = Color(0x00F6F6F6),
    onBackground = Color.Black,

    surface = Color(0xFF4B5A1E),
    onSurface = Color.White,

    error = Color.Red,
    onError = Color.White,
)

val ElevatedSurface = Color(0xFF2B2B2B)

@Composable
fun ProjectXTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BlackAndWhiteColorScheme,
        typography = Typography,
        content = content
    )
}