package com.example.snaphunt.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.snaphunt.data.user.UserLogInData
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState

@Composable
fun AboutUser(userLogInData: UserLogInData, themeState: SettingsState, themeActions: SettingsActions) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "PlaceHolder. Here will be visualized the user's traits.",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "If there is no active profile, the user will se 'create profile and start taking snaps in order to see your stats'",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}