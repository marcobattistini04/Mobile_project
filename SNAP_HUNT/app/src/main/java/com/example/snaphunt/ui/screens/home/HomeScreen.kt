package com.example.snaphunt.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState

@Composable
fun HomeScreen(authViewModel: AuthViewModel, navigationController: NavHostController, themeState: SettingsState, themeActions: SettingsActions) {
    HomeContent(authViewModel, navigationController, themeState, themeActions)
}