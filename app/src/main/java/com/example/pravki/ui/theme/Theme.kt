package com.example.pravki.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
//    primary = Purple200,
//    primaryVariant = Purple700,
//    secondary = Teal200,
//    onSurface = DarkText,
//    onSecondary = HintColor,
//    background = Color.White,
//    onPrimary = Color.White,
//    onBackground = Color.White,
//    onError = Color.Red,

    primary = Purple200,
    primaryVariant = BlueVariant,
    secondary = YellowVariant,
    background = Color.Black,
    onPrimary = YellowVariant,
    onSecondary = HintColor,
    onBackground = YellowVariant,

    onSurface = Color.Red,
)

private val LightColorPalette = lightColors(
    primary = Color.White,
    primaryVariant = BlueVariant,
    secondary = Purple200,
    background = Color.White,
    onPrimary = Purple200,
    onSecondary = Color.Black,
    onBackground = Purple200,

    onSurface = Color.Red,//Purple200,

//    primary = Color.Green,
//    primaryVariant = Color.Green,
//    secondary = Color.Cyan,
//    secondaryVariant = Color.Blue,
//    background = Color.Yellow,
//    surface = Color.Red,
//    error = Color.Magenta,
//    onPrimary = Color.Green,
//    onSecondary = Color.White,
//    onBackground = Color.DarkGray,
//    onSurface = Color.LightGray,
//    onError = Color.Black,

    /*
    primary,
    primaryVariant,
    secondary,
    secondaryVariant,
    background,
    surface,
    error,
    onPrimary,
    onSecondary,
    onBackground,
    onSurface,
    onError,
    */

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun PravkiTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}