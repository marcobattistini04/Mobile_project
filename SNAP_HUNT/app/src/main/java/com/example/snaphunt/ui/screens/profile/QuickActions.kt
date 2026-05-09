package com.example.snaphunt.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.snaphunt.SnapHuntRoute
import com.example.snaphunt.data.user.UserLogInData
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.ui.components.ActionButton
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState

@Composable
fun QuickActions(authViewModel: AuthViewModel,
                 navigationController: NavHostController,
                 user: UserLogInData,
                 themeState: SettingsState,
                 themeActions: SettingsActions) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionButton("Show graphs", onClick = {navigationController.navigate(SnapHuntRoute.GraphScreen)})
        ActionButton("What are traits?", onClick = { })
    }

}