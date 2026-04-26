package com.example.snaphunt.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun TutorialVideos() {
    Column(verticalArrangement = Arrangement.spacedBy(40.dp)) {
        TutorialCard("Take SnapShots")
        TutorialCard("Complete challenges")
        TutorialCard("Earn points and new unique traits")
    }
}