package uk.ac.tees.mad.cryptotracker.models

sealed class ApiResponseState {
    data object Initial : ApiResponseState()
    data object Loading : ApiResponseState()
    data class Success(val coins: List<Coin>) : ApiResponseState()
    data class Error(val message: String) : ApiResponseState()
}