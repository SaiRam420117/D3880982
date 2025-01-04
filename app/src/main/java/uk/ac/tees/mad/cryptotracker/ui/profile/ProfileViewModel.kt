package uk.ac.tees.mad.cryptotracker.ui.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile = _userProfile.asS

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                documentSnapshot.toObject(UserProfile::class.java)?.let {
                    _userProfile.value = it
                }
            }
            .addOnFailureListener {
                // Handle failure
            }
    }

    fun updateUserProfile(username: String, email: String) {
        val userId = auth.currentUser?.uid ?: return
        val updatedProfile = hashMapOf("username" to username, "email" to email)

        firestore.collection("users").document(userId).update(updatedProfile)
            .addOnSuccessListener {
                _userProfile.value = UserProfile(username, email)
            }
            .addOnFailureListener {
                // Handle failure
            }
    }
}
