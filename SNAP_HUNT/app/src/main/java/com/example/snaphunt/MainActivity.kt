package com.example.snaphunt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.ui.screens.graphs.GraphScreen
import com.example.snaphunt.ui.screens.home.HomeScreen
import com.example.snaphunt.ui.screens.profile.ProfileScreen
import com.example.snaphunt.ui.screens.settings.SettingsScreen
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState
import com.example.snaphunt.user_settings.SettingsViewModel
import com.example.snaphunt.ui.theme.SnapHuntTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val authViewModel: AuthViewModel  = koinViewModel()
            val settingsViewModel = koinViewModel <SettingsViewModel>()
            val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_STOP) {
                        settingsViewModel.demandSyncToCloud()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
            LaunchedEffect(Unit) {
                authViewModel.restore()
            }
            val themeState by settingsViewModel.state.collectAsStateWithLifecycle()
            SnapHuntTheme(
                theme = themeState.theme,
                palette = themeState.palette,
                dynamicColor = themeState.dynamicColor
            ) {
                val navController = rememberNavController()
                NavGraph(authViewModel, navController, themeState, settingsViewModel.actions)
            }
        }
    }
}

sealed interface SnapHuntRoute{
    @Serializable data object GraphScreen: SnapHuntRoute
    @Serializable data object HomeScreen: SnapHuntRoute
    @Serializable data object ProfileScreen: SnapHuntRoute
    @Serializable data object SettingsScreen: SnapHuntRoute

    //@Serializable data class SnapShotResultScreen(val traverId: String): SnapHuntRoute
    //@Serializable data object TraitsScreen: SnapHuntRoute
}

@Composable
fun NavGraph(authViewModel: AuthViewModel, navigationController: NavHostController, themeState: SettingsState, themeActions: SettingsActions) {
    NavHost(
        navController = navigationController,
        startDestination = SnapHuntRoute.HomeScreen
    ) {

        composable<SnapHuntRoute.GraphScreen> {
            GraphScreen(authViewModel, navigationController, themeState, themeActions)
        }

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