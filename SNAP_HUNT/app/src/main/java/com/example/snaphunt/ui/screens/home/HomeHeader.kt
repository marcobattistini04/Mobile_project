package com.example.snaphunt.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.snaphunt.data.user.UserLogInData
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.ui.theme.ThemeActions
import com.example.snaphunt.ui.theme.ThemeState
import com.example.snaphunt.presentation.sign_in.GoogleAuthUiClient


@Composable
fun HomeHeader(authViewModel: AuthViewModel, themeState: ThemeState, themeActions: ThemeActions) {
    val state by authViewModel.state.collectAsState()
    val user = state.user
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "SnapHunt",
            style = MaterialTheme.typography.headlineMedium
        )
        if (user == null) {
            Text(
                text = "Welcome User 👋",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Text(
                text = "Welcome " + user!!.username.toString() + "👋",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}