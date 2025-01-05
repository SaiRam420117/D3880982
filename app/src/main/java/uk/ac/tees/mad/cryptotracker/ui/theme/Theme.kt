package uk.ac.tees.mad.cryptotracker.ui.theme

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryMagenta,
    onPrimary = TextWhite,
    secondary = SecondaryViolet,
    onSecondary = TextWhite,
    background = BackgroundDarkViolet,
    surface = SurfaceDark,
    onSurface = TextWhite,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryMagenta,
    onPrimary = Color.White,
    secondary = SecondaryViolet,
    onSecondary = Color.White,
    background = Color.White,
    surface = Color(0xFFF2F2F2),
    onSurface = Color.Black,
    error = ErrorRed,
    onError = Color.White
)


@Composable
fun CryptoTrackerTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    }

    var darkTheme by remember {
        mutableStateOf(sharedPreferences.getBoolean("DarkTheme", false))
    }

    val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == "DarkTheme") {
                darkTheme = prefs.getBoolean(key, false)
            }
        }

    DisposableEffect(sharedPreferences) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        }
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
                context
            )
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}