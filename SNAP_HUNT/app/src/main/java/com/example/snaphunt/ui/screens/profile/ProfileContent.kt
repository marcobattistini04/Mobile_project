package com.example.snaphunt.ui.screens.profile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.ui.components.AppBar
import com.example.snaphunt.ui.components.SignInScreen
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState
import kotlinx.coroutines.launch

@Composable
fun ProfileContent(authViewModel: AuthViewModel, navigationController: NavHostController, themeState: SettingsState, themeActions: SettingsActions) {

    val state by authViewModel.state.collectAsState()
    val ctx = LocalContext.current
    LaunchedEffect(key1 = state.isSignInSuccessful) {
        if (state.isSignInSuccessful) {
            Toast.makeText(
                ctx,
                "Sign In successful",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val user = state.user
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        val intent = result.data ?: return@rememberLauncherForActivityResult
        authViewModel.onSignIn(intent)
    }

    Scaffold(
        topBar = { AppBar(title = "Personal Space", navigationController) }
    ) { contentPadding ->

        if (user != null) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(contentPadding).padding(12.dp).fillMaxSize()
            ) {
                item {
                    ProfileHeader(
                        authViewModel,
                        user,
                        scope,
                        launcher,
                        themeState,
                        themeActions
                    )
                }
                item { AboutUser(user, themeState, themeActions) }
                item {
                    QuickActions(
                        authViewModel,
                        navigationController,
                        user,
                        scope,
                        launcher,
                        themeState,
                        themeActions
                    )
                }
            }

        } else {
            SignInScreen(
                state = state,
                motivation = "view your profile screen",
                onSignInClick = {
                    scope.launch {
                        val intentSender = authViewModel.getSignInIntent()
                        intentSender?.let {
                            launcher.launch(
                                IntentSenderRequest.Builder(it).build()
                            )
                        }
                    }
                }
            )
        }
    }
}