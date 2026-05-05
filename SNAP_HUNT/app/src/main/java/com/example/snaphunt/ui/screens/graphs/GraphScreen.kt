package com.example.snaphunt.ui.screens.graphs

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.ui.components.AppBar
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState

@Composable
fun GraphScreen(authViewModel: AuthViewModel, navigationController: NavHostController, themeState: SettingsState, themeActions: SettingsActions) {
    val ctx = LocalContext.current
    val state by authViewModel.state.collectAsState()
    val user = state.user

    Scaffold(
        topBar = { AppBar(title = "${user!!.username} Stats", navigationController) }
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(contentPadding).padding(16.dp).fillMaxSize().verticalScroll(
                rememberScrollState()
            )
        ) {
            val data = listOf(
                "OK" to 73f,
                "NOT OK" to 27f
            )

            ClickablePieChart(data,
                modifier = Modifier.width(400.dp).height(300.dp)
            ) {
                clickedLabel ->
                Toast.makeText(
                ctx,
                "You have clicked $clickedLabel",
                Toast.LENGTH_SHORT
            ).show()
            }

        }
    }
}