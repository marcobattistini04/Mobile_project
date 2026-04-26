package com.example.snaphunt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.snaphunt.ui.screens.home.HomeScreen
import com.example.snaphunt.ui.screens.profile.AddUser
import com.example.snaphunt.ui.screens.profile.ProfileScreen
import com.example.snaphunt.ui.screens.settings.SettingsScreen
import com.example.snaphunt.ui.theme.ThemeActions
import com.example.snaphunt.ui.theme.ThemeState
import com.example.snaphunt.ui.theme.ThemeViewModel
import com.example.snaphunt.ui.theme.SnapHuntTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                NavGraph(navController, themeState, themeViewModel.actions)
            }
        }
    }
}

sealed interface SnapHuntRoute{
    //@Serializable data object GraphScreen: SnapHuntRoute

    @Serializable data object AddUser
    @Serializable data object HomeScreen: SnapHuntRoute
    @Serializable data object ProfileScreen: SnapHuntRoute
    @Serializable data object SettingsScreen: SnapHuntRoute

    //@Serializable data class SnapShotResultScreen(val traverId: String): SnapHuntRoute
    //@Serializable data object TraitsScreen: SnapHuntRoute
    //@Serializable data object UserScreen: SnapHuntRoute
}

@Composable
fun NavGraph(navigationController: NavHostController, themeState: ThemeState, themeActions: ThemeActions) {
    NavHost(
        navController = navigationController,
        startDestination = SnapHuntRoute.HomeScreen
    ) {

        composable<SnapHuntRoute.AddUser> {
            AddUser(navigationController, themeState, themeActions)
        }

        composable<SnapHuntRoute.HomeScreen> {
            HomeScreen(navigationController, themeState, themeActions)
        }

        composable<SnapHuntRoute.ProfileScreen> {
            ProfileScreen(navigationController, themeState, themeActions)
        }

        composable<SnapHuntRoute.SettingsScreen> {
            SettingsScreen(navigationController, themeState, themeActions)
        }
    }
}