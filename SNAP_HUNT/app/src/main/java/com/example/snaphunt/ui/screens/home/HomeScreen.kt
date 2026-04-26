package com.example.snaphunt.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.snaphunt.ui.theme.ThemeActions
import com.example.snaphunt.ui.theme.ThemeState

@Composable
fun HomeScreen(navigationController: NavHostController, themeState: ThemeState, themeActions: ThemeActions) {
    HomeContent(navigationController, themeState, themeActions)
}