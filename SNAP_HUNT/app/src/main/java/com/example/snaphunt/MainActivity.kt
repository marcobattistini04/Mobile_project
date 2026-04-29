package com.example.snaphunt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.snaphunt.data.repositories.AuthRepository
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.presentation.sign_in.AuthViewModelFactory
import com.example.snaphunt.presentation.sign_in.GoogleAuthUiClient
import com.example.snaphunt.ui.screens.home.HomeScreen
import com.example.snaphunt.ui.screens.profile.ProfileScreen
import com.example.snaphunt.ui.screens.settings.SettingsScreen
import com.example.snaphunt.ui.theme.ThemeActions
import com.example.snaphunt.ui.theme.ThemeState
import com.example.snaphunt.ui.theme.ThemeViewModel
import com.example.snaphunt.ui.theme.SnapHuntTheme
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(
            AuthRepository(
                applicationContext
            )
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel.restore()
        enableEdgeToEdge()
        setContent {
            val themeViewModel = koinViewModel <ThemeViewModel>()
            val themeState by themeViewModel.state.collectAsStateWithLifecycle()
            SnapHuntTheme(
                theme = themeState.theme,
                palette = themeState.palette,
                dynamicColor = themeState.dynamicColor
            ) {
                val navController = rememberNavController()
                NavGraph(authViewModel, navController, themeState, themeViewModel.actions)
            }
        }
    }
}

sealed interface SnapHuntRoute{
    //@Serializable data object GraphScreen: SnapHuntRoute
    @Serializable data object HomeScreen: SnapHuntRoute
    @Serializable data object ProfileScreen: SnapHuntRoute
    @Serializable data object SettingsScreen: SnapHuntRoute

    //@Serializable data class SnapShotResultScreen(val traverId: String): SnapHuntRoute
    //@Serializable data object TraitsScreen: SnapHuntRoute
    //@Serializable data object UserScreen: SnapHuntRoute
}

@Composable
fun NavGraph(authViewModel: AuthViewModel, navigationController: NavHostController, themeState: ThemeState, themeActions: ThemeActions) {
    NavHost(
        navController = navigationController,
        startDestination = SnapHuntRoute.HomeScreen
    ) {

        composable<SnapHuntRoute.HomeScreen> {
            HomeScreen(authViewModel, navigationController, themeState, themeActions)
        }

        composable<SnapHuntRoute.ProfileScreen> {
            ProfileScreen(authViewModel, navigationController, themeState, themeActions)
        }

        composable<SnapHuntRoute.SettingsScreen> {
            SettingsScreen(navigationController, themeState, themeActions)
        }
    }
}