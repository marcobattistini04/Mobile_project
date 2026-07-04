package com.example.snaphunt.ui.screens.profile

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.snaphunt.photos.PhotoGalleryViewModel
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.presentation.sign_in.SignInEvent
import com.example.snaphunt.ui.components.AppBar
import com.example.snaphunt.ui.screens.profile.badge.BadgeEvaluator
import com.example.snaphunt.ui.screens.profile.badge.BadgeType
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState
import java.lang.Integer.sum

@Composable
fun ProfileContent(
    authViewModel: AuthViewModel,
    photoGalleryViewModel: PhotoGalleryViewModel,
    navigationController: NavHostController,
    themeState: SettingsState,
    themeActions: SettingsActions
) {
    val state by authViewModel.state.collectAsStateWithLifecycle()
    val isOnline by photoGalleryViewModel.isOnline.collectAsState()
    val allPhotos by photoGalleryViewModel.challengeState.collectAsState()
    val stats by photoGalleryViewModel.stats.collectAsState()
    val ctx = LocalContext.current

    LaunchedEffect(Unit) {
        authViewModel.events.collect { event ->
            when(event) {
                SignInEvent.SignInSuccess -> {
                    Toast.makeText(
                        ctx,
                        "Sign In successful",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    LaunchedEffect(state.user?.userId, isOnline) {
        if(isOnline) {
            state.user?.userId?.let { photoGalleryViewModel.loadUserChallenges(it)}
        }
        if (!isOnline) {
            Toast.makeText(
                ctx,
                "Unable to sync User traits",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val user = state.user

    Scaffold(
        topBar = { AppBar(isNavigationEnabled = true, title = "Personal Space", navigationController) }
    ) { contentPadding ->

        if (user != null) {
            val evaluator = remember { BadgeEvaluator() }
            val badgeStates = remember(allPhotos) { evaluator.calculateUnlockedBadges(allPhotos) }
            val totalUserPoints = stats.totalPoints
            val unlockedCount = badgeStates.values.count { it }
            val totalCount = BadgeType.entries.size

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(12.dp)
                    .wrapContentHeight()
            ) {
                item {
                    ProfileHeader(
                        authViewModel = authViewModel,
                        themeState = themeState,
                        themeActions = themeActions,
                        totalUserPoints = totalUserPoints,
                        unlockedCount = unlockedCount,
                        totalCount = totalCount
                    )
                }
                item {
                    AboutUser(
                        userLogInData = user,
                        stats = stats,
                        photos = allPhotos,
                        themeState = themeState,
                        themeActions = themeActions
                    )
                }
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