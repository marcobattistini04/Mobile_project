package com.example.snaphunt.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.snaphunt.SnapHuntRoute
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.presentation.sign_in.GoogleAuthUiClient
import com.example.snaphunt.ui.components.AppBar
import com.example.snaphunt.ui.components.SignInScreen
import com.example.snaphunt.ui.theme.ThemeActions
import com.example.snaphunt.ui.theme.ThemeState
import kotlinx.coroutines.launch

@Composable
fun ProfileContent(authViewModel: AuthViewModel, navigationController: NavHostController, themeState: ThemeState, themeActions: ThemeActions) {
    val state by authViewModel.state.collectAsState()
    val user = state.user
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        val intent = result.data ?: return@rememberLauncherForActivityResult
        authViewModel.onSignIn(intent)
    }

    if (user != null) {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        onClick = {} //TO DO!! SIGNINSCREEN MUST BE MODIFIED IT'S TOO EMPTY
                    ) {
                        Icon(Icons.Outlined.Add, "Add Travel")
                    }
                },
                topBar = { AppBar(title = "Profile", navigationController) }
            ) {contentPadding ->
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(contentPadding).padding(12.dp).fillMaxSize()
                ) {
                    item { ProfileHeader(user, themeState, themeActions) }
                    item { AboutUser(user, themeState, themeActions) }
                    item { QuickActions(user, themeState, themeActions) }
                }
            }
    } else {
        SignInScreen(
            state = state,
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