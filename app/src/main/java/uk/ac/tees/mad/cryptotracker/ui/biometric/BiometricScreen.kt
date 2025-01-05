package uk.ac.tees.mad.cryptotracker.ui.biometric

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import uk.ac.tees.mad.cryptotracker.BiometricManagerUtil

@Composable
fun BiometricScreen(navController: NavController) {
    val context = LocalContext.current
    val biometricManagerUtil = remember { BiometricManagerUtil(context) }

    LaunchedEffect(Unit) {
        biometricManagerUtil.showBiometricPrompt(
            activity = context as FragmentActivity,
            onAuthenticationSuccess = {
                navController.navigate("home") {
                    popUpTo("biometric") { inclusive = true }
                }
            },
            onAuthenticationError = { errorMessage ->
                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                navController.navigate("auth") {
                    popUpTo("biometric") { inclusive = true }
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        TextButton(
            onClick = {
                biometricManagerUtil.showBiometricPrompt(
                    activity = context as FragmentActivity,
                    onAuthenticationSuccess = {
                        navController.navigate("home") {
                            popUpTo("biometric") { inclusive = true }
                        }
                    },
                    onAuthenticationError = { errorMessage ->
                        Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                        navController.navigate("auth") {
                            popUpTo("biometric") { inclusive = true }
                        }
                    }
                )
            }
        ) {
            Text("Authenticate")
        }
    }
}
