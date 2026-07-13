package com.example.snaphunt.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.snaphunt.SnapHuntRoute
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.ui.components.ActionButton
import com.example.snaphunt.ui.components.ExpandableSection
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState

@Composable
fun QuickActions(
    authViewModel: AuthViewModel,
    navigationController: NavHostController,
    themeState: SettingsState,
    themeActions: SettingsActions
) {
    val mainButtonBgColor = MaterialTheme.colorScheme.inverseSurface
    val mainButtonTextColor = MaterialTheme.colorScheme.inverseOnSurface

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .offset(y = (-60).dp),
    ) {
        ExpandableSection(
            "What are traits?",
            "• User achievements\n" +
                    "• Clear representation of your SnapHunt progress\n" +
                    "• They are dynamic and can be earned or lost at any moment\n" +
                    "• Show them to your friends!"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                text = "Show stats",
                onClick = { navigationController.navigate(SnapHuntRoute.GraphScreen) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = mainButtonBgColor,
                    contentColor = mainButtonTextColor
                )
            )

            ActionButton(
                text = "Gallery",
                onClick = { navigationController.navigate(SnapHuntRoute.PhotoGalleryScreen) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = mainButtonBgColor,
                    contentColor = mainButtonTextColor
                )
            )
        }
    }
}