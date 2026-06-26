package com.example.snaphunt.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.example.snaphunt.ui.components.ExpandableSection
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState
import androidx.compose.foundation.layout.offset

@Composable
fun QuickActions(authViewModel: AuthViewModel,
                 navigationController: NavHostController,
                 themeState: SettingsState,
                 themeActions: SettingsActions) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .offset(y = (-60).dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        ExpandableSection("What are traits?",
            "• User achievements\n" +
                "• Clear representation of your SnapHunt progress\n" +
                "• They are dynamic and can be earned or lost at any moment\n" +
                "• Show them to your friends!"
        )
        ActionButton("Show stats", onClick = {navigationController.navigate(SnapHuntRoute.GraphScreen)})
        ActionButton("SnapHunt collection!", onClick = {navigationController.navigate(SnapHuntRoute.PhotoGalleryScreen)})
    }
}