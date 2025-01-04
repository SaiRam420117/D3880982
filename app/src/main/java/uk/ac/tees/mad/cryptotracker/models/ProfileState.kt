package uk.ac.tees.mad.cryptotracker.models

sealed class ProfileState {
    data object Initial : ProfileState()
    data object Loading : ProfileState()
    data class Success(val userProfile: UserProfile) : ProfileState()
    data class Error(val message: String) : ProfileState()
}