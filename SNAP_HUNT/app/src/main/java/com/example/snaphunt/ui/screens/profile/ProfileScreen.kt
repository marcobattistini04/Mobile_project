package com.example.snaphunt.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.snaphunt.photos.PhotoGalleryViewModel
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    photoGalleryViewModel: PhotoGalleryViewModel,
    navigationController: NavHostController,
    themeState: SettingsState,
    themeActions: SettingsActions
) {
    ProfileContent(
        authViewModel,
        photoGalleryViewModel,
        navigationController,
        themeState,
        themeActions
    )
}