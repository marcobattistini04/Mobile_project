package com.example.snaphunt.ui.screens.profile

import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.snaphunt.data.user.UserLogInData
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.ui.components.ActionButton
import com.example.snaphunt.ui.theme.ThemeActions
import com.example.snaphunt.ui.theme.ThemeState
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun QuickActions(authViewModel: AuthViewModel,
                 user: UserLogInData,
                 scope: CoroutineScope,
                 launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
                 themeState: ThemeState,
                 themeActions: ThemeActions) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionButton("Show graphs")
        ActionButton("What are traits?")
    }

}