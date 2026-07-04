package com.example.snaphunt

import android.content.Context
import android.os.Bundle
import org.koin.androidx.compose.KoinAndroidContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.snaphunt.data.user.UserChallengeItem
import com.example.snaphunt.image_recognition.ObjectDetectionViewModel
import com.example.snaphunt.photos.PhotoGalleryViewModel
import com.example.snaphunt.photos.PhotoSyncViewModel
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.ui.screens.graphs.GraphScreen
import com.example.snaphunt.ui.screens.home.HomeScreen
import com.example.snaphunt.ui.screens.photo_gallery.PhotoDetailsScreen
import com.example.snaphunt.ui.screens.photo_gallery.PhotoGalleryScreen
import com.example.snaphunt.ui.screens.profile.ProfileScreen
import com.example.snaphunt.ui.screens.settings.SettingsScreen
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState
import com.example.snaphunt.user_settings.SettingsViewModel
import com.example.snaphunt.ui.theme.SnapHuntTheme
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.android.ext.android.getKoin
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            clearTempFiles(applicationContext)
        }
        enableEdgeToEdge()
        val supabase = getKoin().get<SupabaseClient>()
        lifecycleScope.launch {
            supabase.auth.loadFromStorage()
        }
        setContent {
            KoinAndroidContext {
                val objectDetectionViewModel: ObjectDetectionViewModel = koinViewModel<ObjectDetectionViewModel>()
                val photoSyncViewModel: PhotoSyncViewModel = koinViewModel<PhotoSyncViewModel>()
                val authViewModel: AuthViewModel  = koinViewModel <AuthViewModel>()
                val settingsViewModel: SettingsViewModel = koinViewModel <SettingsViewModel>()
                val photoGalleryViewModel: PhotoGalleryViewModel = koinViewModel <PhotoGalleryViewModel> ()
                val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
                val authState by authViewModel.state.collectAsStateWithLifecycle()

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
                val themeState by settingsViewModel.state.collectAsStateWithLifecycle()
                SnapHuntTheme(
                    theme = themeState.theme,
                    palette = themeState.palette,
                    dynamicColor = themeState.dynamicColor
                ) {
                    val navController = rememberNavController()
                    if(authState.isInitializing) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        NavGraph(
                            authViewModel,
                            photoGalleryViewModel,
                            photoSyncViewModel,
                            objectDetectionViewModel,
                            navController,
                            themeState,
                            settingsViewModel.actions
                        )
                    }
                }
            }
        }
    }
}

sealed interface SnapHuntRoute{

    @Serializable data object HomeScreen: SnapHuntRoute
    @Serializable data object ProfileScreen: SnapHuntRoute
    @Serializable data object PhotoGalleryScreen
    @Serializable data class PhotoDetails(val challengeId: String)
    @Serializable data object GraphScreen: SnapHuntRoute
    @Serializable data object SettingsScreen: SnapHuntRoute

    //@Serializable data class SnapShotResultScreen(val traverId: String): SnapHuntRoute
    //@Serializable data object TraitsScreen: SnapHuntRoute
}

@Composable
fun NavGraph(
    authViewModel: AuthViewModel,
    photoGalleryViewModel: PhotoGalleryViewModel,
    photoSyncViewModel: PhotoSyncViewModel,
    objectDetectionViewModel: ObjectDetectionViewModel,
    navigationController: NavHostController, themeState: SettingsState,
    themeActions: SettingsActions
) {
    NavHost(
        navController = navigationController,
        startDestination = SnapHuntRoute.HomeScreen
    ) {

        composable<SnapHuntRoute.HomeScreen> {
            HomeScreen(objectDetectionViewModel, photoSyncViewModel, photoGalleryViewModel, authViewModel, navigationController, themeState, themeActions)
        }

        composable<SnapHuntRoute.ProfileScreen> {
            ProfileScreen(authViewModel, photoGalleryViewModel, navigationController, themeState, themeActions)
        }

        composable<SnapHuntRoute.GraphScreen> {
            GraphScreen(authViewModel, photoGalleryViewModel, navigationController, themeState, themeActions)
        }

        composable<SnapHuntRoute.PhotoGalleryScreen> {
            PhotoGalleryScreen(authViewModel, photoGalleryViewModel,  navigationController)
        }

        composable<SnapHuntRoute.PhotoDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<SnapHuntRoute.PhotoDetails>()
            PhotoDetailsScreen(navigationController, photoGalleryViewModel, route.challengeId)

        }

        composable<SnapHuntRoute.SettingsScreen> {
            SettingsScreen(navigationController, themeState, themeActions)
        }
    }
}


// clear all tmp_images obtained during the photo hunt sequence at the system-folder sdcard/Android/data/com.example.snaphunt/cache
fun clearTempFiles(context: Context) {
    val cacheDir = context.externalCacheDir ?: return
    val files = cacheDir.listFiles { _, name -> name.startsWith("snaphunt_tmp") }

    files?.forEach { file ->
        try {
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}