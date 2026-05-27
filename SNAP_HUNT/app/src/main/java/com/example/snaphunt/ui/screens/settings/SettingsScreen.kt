package com.example.snaphunt.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.snaphunt.data.models.AppTheme
import com.example.snaphunt.data.models.ColorPalette
import com.example.snaphunt.ui.components.AppBar
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState

@Composable
fun SettingsScreen(navigationController: NavHostController, settingsState: SettingsState, settingsActions: SettingsActions) {
    Scaffold(
        topBar = { AppBar(title = "Settings", navigationController) }
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(contentPadding).padding(16.dp).fillMaxSize().verticalScroll(
                rememberScrollState()
            )
        ) {

            Spacer(modifier = Modifier.size(32.dp))

            Text(
                "Enable SnapHunt Events notifications",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            RadioListItem(
                label = "Yes, I want to receive notifications",
                selected = settingsState.notification,
                onClick = { settingsActions.setEnableNotifications(true)}
            )

            RadioListItem(
                label = "No, do not send me notifications",
                selected = !settingsState.notification,
                onClick = { settingsActions.setEnableNotifications(false)}
            )

            Spacer(modifier = Modifier.size(24.dp))

            Text(
                "Theme",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            AppTheme.entries.forEach { theme ->
                RadioListItem(
                    label = theme.toString(),
                    selected = theme == settingsState.theme,
                    onClick = { settingsActions.setTheme(theme) }
                )
            }
            Spacer(modifier = Modifier.size(24.dp))

            Text(
                "Color Palette",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            ColorPalette.entries.forEach { palette ->
                RadioListItem(
                    label = palette.name,
                    selected = palette == settingsState.palette,
                    onClick = { settingsActions.setPalette(palette) }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Dynamic Color",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )

            RadioListItem(
                label = "Use system colors (Material You)",
                selected = settingsState.dynamicColor,
                onClick = { settingsActions.setDynamicColor(true) }
            )

            RadioListItem(
                label = "Use custom palette",
                selected = !settingsState.dynamicColor,
                onClick = { settingsActions.setDynamicColor(false) }
            )
        }
    }
}

@Composable
fun RadioListItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}