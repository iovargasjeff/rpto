package com.example.roomdatabase.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RoomDatabaseColorScheme = lightColorScheme(
    primary = Color(0xFF275DAD),
    onPrimary = Color.White,
    secondary = Color(0xFF53657F),
    background = Color(0xFFF5F7FB),
    surface = Color(0xFFF5F7FB),
    surfaceContainer = Color(0xFFEAF0F8),
    surfaceContainerLow = Color(0xFFEEF3F9),
    error = Color(0xFFB3261E)
)

@Composable
fun RoomDatabaseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RoomDatabaseColorScheme,
        content = content
    )
}
