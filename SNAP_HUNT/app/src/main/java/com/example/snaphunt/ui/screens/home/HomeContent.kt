package com.example.snaphunt.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.snaphunt.ui.components.AppBar
import com.example.snaphunt.ui.theme.ThemeActions
import com.example.snaphunt.ui.theme.ThemeState

@Composable
fun HomeContent(navigationController: NavHostController, themeState: ThemeState, themeActions: ThemeActions) {
    Scaffold(
        topBar = { AppBar(title = "SnapHunt", navigationController) }
    ) {contentPadding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(contentPadding).padding(12.dp).fillMaxSize()
        ) {
            item { HomeHeader(themeState, themeActions) }
            item { QuickActions(themeState, themeActions) }
            item { AboutApp(themeState, themeActions) }
        }
    }

}