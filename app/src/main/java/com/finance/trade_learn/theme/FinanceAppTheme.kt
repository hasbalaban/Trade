package com.finance.trade_learn.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val LightColorPalette = lightColors(
    primary = Color.LightGray, // bottomshhet background
    onPrimary = Color.Black,

    secondary = Color.Red,
    onSecondary = Color.Black, // text color

    surface = Color.White,
    onSurface = Color.Black
)

private val DarkColorPalette = darkColors(
    primary = Color.White,
    onPrimary = Color.White,

    secondary = Color.Magenta,
    onSecondary = Color.White,

    surface = Color.Black,
    onSurface = Color.White
)

@Composable
fun FinanceAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors : Colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}