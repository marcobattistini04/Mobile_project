package com.example.snaphunt.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.snaphunt.photos.PhotoSyncViewModel
import com.example.snaphunt.photos.ScreenState
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState


@Composable
fun HomeHeader(
    authViewModel: AuthViewModel,
    photoSyncViewModel: PhotoSyncViewModel,
    themeState: SettingsState,
    themeActions: SettingsActions
) {
    val state by authViewModel.state.collectAsStateWithLifecycle()
    val uiState by photoSyncViewModel.uiState.collectAsStateWithLifecycle()
    val user = state.user
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.size(24.dp))
        if (user == null) {
            Text(
                text = "Welcome User 👋",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            when (val state = uiState) {
                is ScreenState.Idle -> {
                    Text(
                        text = "Welcome " + user.username.toString() + "👋",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {}
            }
        }
    }
}