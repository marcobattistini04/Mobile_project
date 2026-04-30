package com.example.snaphunt.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.snaphunt.SnapHuntRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(title: String, navigationController: NavHostController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                title,
                fontWeight = FontWeight.Medium,
            )
        },
        actions = {
            if (title != "Settings") {
                IconButton(onClick = { navigationController.navigate(SnapHuntRoute.SettingsScreen)}) {
                    Icon(Icons.Outlined.Settings, "Settings")
                }
            }
            if(title != "Profile") {
                IconButton(onClick = {navigationController.navigate(SnapHuntRoute.ProfileScreen)}) {
                    Icon(Icons.Outlined.AccountCircle, "Personal Space")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        navigationIcon = {
            if(navigationController.previousBackStackEntry != null) {
                IconButton(onClick = {navigationController.navigateUp()}) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        "Go Back"
                    )
                }
            }
        }
    )
}