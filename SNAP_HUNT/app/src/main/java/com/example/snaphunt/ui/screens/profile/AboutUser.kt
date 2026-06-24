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
import com.example.snaphunt.data.user.UserStats
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState

@Composable
fun AboutUser(userLogInData: UserLogInData, stats: UserStats, themeState: SettingsState, themeActions: SettingsActions) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Total Challenges: ${stats.totalChallenges}")
        Text("Challenges won: ${stats.wonChallenges}")
        Text("Challenges lost: ${stats.lostChallenges}")
        Text("Challenges skipped: ${stats.skippedChallenges}")
        Text("Total Points earned: ${stats.totalPoints}")
        Text("Total Additional Objects found: ${stats.totalAdditionalObjects}")
        Text("Ai Model average confidence on total challenges: ${stats.meanAIConfidenceOnTotal}")
        Text("Ai Model average confidence on won challenges: ${stats.meanAIConfidenceOnSuccess}")
    }
}