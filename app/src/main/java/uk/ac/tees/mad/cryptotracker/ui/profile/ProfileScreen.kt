package uk.ac.tees.mad.cryptotracker.ui.profile

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import uk.ac.tees.mad.cryptotracker.BiometricManagerUtil
import uk.ac.tees.mad.cryptotracker.models.ProfileState
import uk.ac.tees.mad.cryptotracker.models.UserProfile
import uk.ac.tees.mad.cryptotracker.ui.home.CoinList
import uk.ac.tees.mad.cryptotracker.ui.home.ErrorScreen
import uk.ac.tees.mad.cryptotracker.ui.home.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    if (userProfile is ProfileState.Success)
                        IconButton(
                            onClick = { showDialog = true }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                        }
                },
                navigationIcon = {

                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "back")
                    }

                }
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            when (userProfile) {
                is ProfileState.Error -> ErrorScreen((userProfile as ProfileState.Error).message) { viewModel.fetchUserProfile() }
                is ProfileState.Success -> ProfileContainer(
                    userProfile = (userProfile as ProfileState.Success).userProfile,

                )

                else -> LoadingScreen()
            }
        }
        if (showDialog && userProfile is ProfileState.Success) {
            EditProfileDialog(
                currentUsername = (userProfile as ProfileState.Success).userProfile.username,
                onDismiss = { showDialog = false },
                onSave = { username ->
                    viewModel.updateUserProfile(
                        username,
                        (userProfile as ProfileState.Success).userProfile.email
                    )
                    showDialog = false
                }
            )
        }
    }

}

@Composable
fun ProfileContainer(userProfile: UserProfile) {
    val context = LocalContext.current
    val sharedPreferences =
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val biometricManagerUtil = BiometricManagerUtil(context)
    val isFingerprintEnabled = remember {
        mutableStateOf(
            sharedPreferences.getBoolean("FingerprintEnabled", false)
        )
    }
    val isDarkTheme = remember {
        mutableStateOf(
            sharedPreferences.getBoolean("DarkTheme", false)
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(120.dp)
                .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "User Avatar",
                modifier = Modifier.fillMaxSize(),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Username: ${userProfile.username}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Email: ${userProfile.email}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

            FingerprintToggle(
                modifier = Modifier.weight(1f),
                isFingerprintEnabled = isFingerprintEnabled.value
            ) { isChecked ->
                if (biometricManagerUtil.isBiometricAvailable()) {
                    isFingerprintEnabled.value = isChecked
                    sharedPreferences.edit().putBoolean("FingerprintEnabled", isChecked)
                        .apply()
                }
            }
            ThemeToggle(
                modifier = Modifier.weight(1f),
                isDarkTheme = isDarkTheme.value
            ) { isChecked ->
                isDarkTheme.value = isChecked
                sharedPreferences.edit().putBoolean("DarkTheme", isChecked)
                    .apply()
            }
        }

    }
}

@Composable
fun EditProfileDialog(
    currentUsername: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var username by remember { mutableStateOf(currentUsername) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Edit Profile", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(username) }) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun FingerprintToggle(
    modifier: Modifier = Modifier,
    isFingerprintEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(6.dp),
        onClick = { onToggle(!isFingerprintEnabled) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Outlined.Fingerprint,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = isFingerprintEnabled,
                onCheckedChange = onToggle
            )
        }
    }
}

@Composable
fun ThemeToggle(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Outlined.DarkMode,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = isDarkTheme,
                onCheckedChange = onToggle
            )
        }
    }
}
