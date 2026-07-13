package com.example.snaphunt.ui.screens.profile.badge

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.snaphunt.R

@Composable
fun BadgeItem(badgeType: BadgeType, isUnlocked: Boolean) {

    val imageRes = when (badgeType) {
        BadgeType.FIRST_PHOTO -> {
            if (isUnlocked) R.drawable.badge_1 else R.drawable.badge_1n
        }
        BadgeType.TECH_EXPERT -> {
            if (isUnlocked) R.drawable.badge_2 else R.drawable.badge_2n
        }
        BadgeType.ANIMAL_LOVER -> {
            if (isUnlocked) R.drawable.badge_3 else R.drawable.badge_3n
        }
        BadgeType.HIGH_CONFIDENCE -> {
            if (isUnlocked) R.drawable.badge_6 else R.drawable.badge_6n
        }
        BadgeType.URBAN_EXPLORER -> {
            if (isUnlocked) R.drawable.badge_7 else R.drawable.badge_7n
        }
        BadgeType.CHALLENGE_MASTER -> {
            if (isUnlocked) R.drawable.badge_8 else R.drawable.badge_8n
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(4.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = badgeType.title,
            modifier = Modifier.size(85.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = badgeType.title,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}