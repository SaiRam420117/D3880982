package uk.ac.tees.mad.cryptotracker.ui.coindetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.ac.tees.mad.cryptotracker.data.api.CoinGeckoApi
import uk.ac.tees.mad.cryptotracker.models.CoinResponseState

class CryptoDetailsViewModel : ViewModel() {
    private val api = Retrofit.Builder()
        .baseUrl("https://api.coingecko.com/api/v3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CoinGeckoApi::class.java)

    private val _coinDetails = MutableStateFlow<CoinResponseState>(CoinResponseState.Initial)
    val coinDetails = _coinDetails.asStateFlow()


    fun fetchCoinDetails(id: String) {
        viewModelScope.launch {
            _coinDetails.value = CoinResponseState.Loading
            try {
                val coinResponse = api.getCoinDetails(id)
                Log.d("CryptoDetailsViewModel", "Coin details: $coinResponse")
                _coinDetails.value = CoinResponseState.Success(coinResponse)
            } catch (e: Exception) {
                _coinDetails.value = CoinResponseState.Error(e.message ?: "Unknown error")
            }
        }
    }
}