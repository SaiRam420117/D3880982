package uk.ac.tees.mad.cryptotracker.ui.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.ac.tees.mad.cryptotracker.models.ProfileState
import uk.ac.tees.mad.cryptotracker.models.UserProfile

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _userProfile = MutableStateFlow<ProfileState>(ProfileState.Initial)
    val userProfile = _userProfile.asStateFlow()

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        _userProfile.value = ProfileState.Loading
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                documentSnapshot.toObject(UserProfile::class.java)?.let {
                    _userProfile.value = ProfileState.Success(it)
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun updateUserProfile(username: String, email: String) {
        val userId = auth.currentUser?.uid ?: return
        val updatedProfile = mapOf("username" to username, "email" to email)
        _userProfile.value = ProfileState.Loading
        firestore.collection("users").document(userId).update(updatedProfile)
            .addOnSuccessListener {
                _userProfile.value =
                    ProfileState.Success(UserProfile(username = username, email = email))
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }
}
