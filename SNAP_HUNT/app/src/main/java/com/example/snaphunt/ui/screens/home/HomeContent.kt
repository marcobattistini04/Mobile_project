package com.example.snaphunt.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.snaphunt.image_recognition.ObjectDetectionViewModel
import com.example.snaphunt.photos.PhotoSyncViewModel
import com.example.snaphunt.photos.ScreenState
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.ui.components.AppBar
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState

@Composable
fun HomeContent(
    objectDetectionViewModel: ObjectDetectionViewModel,
    photoSyncViewModel: PhotoSyncViewModel,
    authViewModel: AuthViewModel,
    navigationController: NavHostController,
    themeState: SettingsState,
    themeActions: SettingsActions) {
    val uiState by photoSyncViewModel.uiState.collectAsState()

    val canNavigate = uiState !is ScreenState.CameraActive

    Scaffold(
        topBar = { AppBar(isNavigationEnabled = canNavigate, title = "SnapHunt", navigationController) }
    ) {contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            HomeHeader(authViewModel, photoSyncViewModel, themeState, themeActions)
            QuickActions(
                objectDetectionViewModel,
                photoSyncViewModel,
                authViewModel,
                themeState,
                themeActions
            )
            AboutApp(themeState, themeActions)
        }
    }

}