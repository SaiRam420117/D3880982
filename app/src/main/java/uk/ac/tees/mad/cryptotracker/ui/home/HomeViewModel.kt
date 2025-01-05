package uk.ac.tees.mad.cryptotracker.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.protobuf.Api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.ac.tees.mad.cryptotracker.data.api.CoinGeckoApi
import uk.ac.tees.mad.cryptotracker.models.ApiResponseState

class HomeViewModel : ViewModel() {
    private val api = Retrofit.Builder()
        .baseUrl("https://api.coingecko.com/api/v3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CoinGeckoApi::class.java)

    private val _coins = MutableStateFlow<ApiResponseState>(ApiResponseState.Initial)
    val coins = _coins.asStateFlow()

    init {
        fetchCoins()
    }

    fun fetchCoins() {
        viewModelScope.launch {
            _coins.value = ApiResponseState.Loading
            try {
                val response = api.getCoins()
                Log.d("HomeViewModel", "Fetched coins: $response")
                _coins.value = ApiResponseState.Success(response)
            } catch (e: Exception) {
                _coins.value = ApiResponseState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
