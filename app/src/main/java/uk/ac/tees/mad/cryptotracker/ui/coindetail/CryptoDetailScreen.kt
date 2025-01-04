package uk.ac.tees.mad.cryptotracker.ui.coindetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CryptoDetailScreen(
    cryptoId: String,
    navController: NavHostController,
    viewModel: CryptoDetailsViewModel = viewModel()
) {

    LaunchedEffect(cryptoId) {
        viewModel.fetchCoinDetails(cryptoId)
    }

}