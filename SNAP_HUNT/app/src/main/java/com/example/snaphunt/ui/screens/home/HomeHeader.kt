package com.example.snaphunt.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.snaphunt.ui.theme.ThemeActions
import com.example.snaphunt.ui.theme.ThemeState

@Composable
fun HomeHeader(themeState: ThemeState, themeActions: ThemeActions) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "SnapHunt",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Welcome User 👋",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}