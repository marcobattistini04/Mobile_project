package com.example.snaphunt.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.snaphunt.data.user.UserChallengeItem
import com.example.snaphunt.data.user.UserLogInData
import com.example.snaphunt.data.user.UserStats
import com.example.snaphunt.ui.screens.profile.badge.BadgeItem
import com.example.snaphunt.ui.screens.profile.badge.BadgeEvaluator
import com.example.snaphunt.ui.screens.profile.badge.BadgeType
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState
import androidx.compose.foundation.layout.offset

@Composable
fun AboutUser(
    userLogInData: UserLogInData,
    stats: UserStats,
    photos: List<UserChallengeItem>,
    themeState: SettingsState,
    themeActions: SettingsActions
) {
    val evaluator = remember { BadgeEvaluator() }
    val badgeStates = remember(photos) { evaluator.calculateUnlockedBadges(photos) }

    val badgeList = remember { BadgeType.entries.toList() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 0.dp).padding(16.dp)
            .offset(y = (-20).dp)
    ) {
        //Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Your Badges",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 6.dp)        )

        val chunkSize = 3
        val badgeRows = badgeList.chunked(chunkSize)

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            badgeRows.forEach { rowBadges ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowBadges.forEach { badgeType ->
                        Row(modifier = Modifier.weight(1f)) {
                            val isUnlocked = badgeStates[badgeType] ?: false
                            BadgeItem(badgeType = badgeType, isUnlocked = isUnlocked)
                        }
                    }

                    if (rowBadges.size < chunkSize) {
                        repeat(chunkSize - rowBadges.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}