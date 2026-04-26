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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.snaphunt.data.models.AppTheme
import com.example.snaphunt.data.models.ColorPalette
import com.example.snaphunt.ui.components.AppBar
import com.example.snaphunt.ui.theme.ThemeActions
import com.example.snaphunt.ui.theme.ThemeState

@Composable
fun SettingsScreen(navigationController: NavHostController, themeState: ThemeState, themeActions: ThemeActions) {
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
                "Theme",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            AppTheme.entries.forEach { theme ->
                RadioListItem(
                    label = theme.toString(),
                    selected = theme == themeState.theme,
                    onClick = { themeActions.setTheme(theme) }
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
                    selected = palette == themeState.palette,
                    onClick = { themeActions.setPalette(palette) }
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
                selected = themeState.dynamicColor,
                onClick = { themeActions.setDynamicColor(true) }
            )

            RadioListItem(
                label = "Use custom palette",
                selected = !themeState.dynamicColor,
                onClick = { themeActions.setDynamicColor(false) }
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