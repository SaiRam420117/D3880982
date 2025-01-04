package uk.ac.tees.mad.cryptotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.cryptotracker.ui.auth.AuthScreen
import uk.ac.tees.mad.cryptotracker.ui.coindetail.CryptoDetailScreen
import uk.ac.tees.mad.cryptotracker.ui.home.HomeScreen
import uk.ac.tees.mad.cryptotracker.ui.profile.ProfileScreen
import uk.ac.tees.mad.cryptotracker.ui.theme.CryptoTrackerTheme
import uk.ac.tees.mad.cryptotracker.ui.splash.SplashScreen
import uk.ac.tees.mad.cryptotracker.ui.watchlist.WatchlistScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoTrackerTheme {
                CryptoTrackerApp()
            }
        }
    }
}

@Composable
fun CryptoTrackerApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("auth") {
            AuthScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("crypto_details/{cryptoId}") { backStackEntry ->
            val cryptoId = backStackEntry.arguments?.getString("cryptoId") ?: return@composable
            CryptoDetailScreen(
                cryptoId = cryptoId,
                navController = navController
            )
        }
        composable("watchlist") {
            WatchlistScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
    }
}