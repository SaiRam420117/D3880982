package uk.ac.tees.mad.cryptotracker.models

sealed class CoinResponseState {
    data object Initial : CoinResponseState()
    data object Loading : CoinResponseState()
    data class Success(val coins: CoinDetails) : CoinResponseState()
    data class Error(val message: String) : CoinResponseState()
}