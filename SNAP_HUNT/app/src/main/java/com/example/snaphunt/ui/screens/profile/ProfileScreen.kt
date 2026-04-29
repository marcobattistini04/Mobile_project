package com.example.snaphunt.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.ui.theme.ThemeActions
import com.example.snaphunt.ui.theme.ThemeState

@Composable
fun ProfileScreen(authViewModel: AuthViewModel, navigationController: NavHostController, themeState: ThemeState, themeActions: ThemeActions) {
    ProfileContent(authViewModel, navigationController, themeState, themeActions)
}