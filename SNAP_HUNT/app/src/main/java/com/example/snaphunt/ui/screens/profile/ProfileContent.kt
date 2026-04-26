package com.example.snaphunt.ui.screens.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.snaphunt.SnapHuntRoute
import com.example.snaphunt.ui.components.AppBar
import com.example.snaphunt.ui.theme.ThemeActions
import com.example.snaphunt.ui.theme.ThemeState

@Composable
fun ProfileContent(navigationController: NavHostController, themeState: ThemeState, themeActions: ThemeActions) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.tertiary,
                onClick = {navigationController.navigate(SnapHuntRoute.AddUser)}
            ) {
                Icon(Icons.Outlined.Add, "Add Travel")
            }
        },
        topBar = { AppBar(title = "Profile", navigationController) }
    ) {contentPadding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(contentPadding).padding(12.dp).fillMaxSize()
        ) {
            item { ProfileHeader(themeState, themeActions) }
            item { AboutUser(themeState, themeActions) }
            item { QuickActions(themeState, themeActions) }
        }
    }
}