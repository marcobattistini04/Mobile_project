package com.example.snaphunt.ui.screens.profile

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.ui.components.AppBar
import com.example.snaphunt.ui.components.SignInScreen
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState

@Composable
fun ProfileContent(authViewModel: AuthViewModel, navigationController: NavHostController, themeState: SettingsState, themeActions: SettingsActions) {
    val state by authViewModel.state.collectAsStateWithLifecycle()
    val ctx = LocalContext.current
    var alreadyShown = false
    LaunchedEffect(key1 = state.isSignInSuccessful) {
        if (state.isSignInSuccessful && !alreadyShown) {
            alreadyShown = true
            Toast.makeText(
                ctx,
                "Sign In successful",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val user = state.user

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
                        themeState,
                        themeActions
                    )
                }
                item { AboutUser(user, themeState, themeActions) }
                item {
                    QuickActions(
                        authViewModel,
                        navigationController,
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
                    val activity = ctx as Activity
                    authViewModel.onSignIn(activity)
                }
            )
        }
    }
}