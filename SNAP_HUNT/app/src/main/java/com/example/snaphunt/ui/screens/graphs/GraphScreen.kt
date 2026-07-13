package com.example.snaphunt.ui.screens.graphs

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.snaphunt.photos.PhotoGalleryViewModel
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.ui.components.AppBar
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState
import androidx.compose.foundation.layout.size
import com.example.snaphunt.graphs.GraphsViewModel

@Composable
fun GraphScreen(
    authViewModel: AuthViewModel,
    photoGalleryViewModel: PhotoGalleryViewModel,
    navigationController: NavHostController,
    themeState: SettingsState,
    themeActions: SettingsActions,
    graphsViewModel: GraphsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val ctx = LocalContext.current
    val state by authViewModel.state.collectAsState()
    val stats by photoGalleryViewModel.stats.collectAsState()
    val isOnline by photoGalleryViewModel.isOnline.collectAsState()
    val rawChallenges by photoGalleryViewModel.challengeState.collectAsState()
    val user = state.user

    val weeklyPoints by graphsViewModel.weeklyPoints.collectAsState()

    LaunchedEffect(rawChallenges) {
        graphsViewModel.updateChallenges(rawChallenges)
    }

    LaunchedEffect(state.user?.userId, isOnline) {
        if (isOnline) {
            state.user?.userId?.let { photoGalleryViewModel.loadUserChallenges(it) }
        }
        if (!isOnline) {
            Toast.makeText(
                ctx,
                "Unable to sync User stats",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        topBar = { AppBar(isNavigationEnabled = true, title = "${user!!.username} Stats", navigationController) }
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            fun Float.toPercent() = "%.1f%%".format(this * 100)

            Text(
                text = "Game Overview",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .align(Alignment.Start)
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                StatBox(label = "Total Challenges", value = "${stats.totalChallenges}", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                StatBox(label = "Total Points earned", value = "${stats.totalPoints}", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                StatBox(label = "Additional Objects", value = "${stats.totalAdditionalObjects}", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                StatBox(label = "Ai Model average", value = stats.meanAIConfidenceOnTotal.toPercent(), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                StatBox(label = "Ai accuracy on won challenges", value = stats.meanAIConfidenceOnSuccess.toPercent(), modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Distribution of challenges results",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .align(Alignment.Start)
            )

            val data = listOf(
                "Won" to stats.wonChallenges.toFloat(),
                "Lost" to stats.lostChallenges.toFloat(),
                "Skipped" to stats.skippedChallenges.toFloat()
            )

            ClickablePieChart(
                data = data,
                modifier = Modifier
                    .size(300.dp)
                    .padding(vertical = 16.dp)
            ) { clickedLabel ->
                Toast.makeText(
                    ctx,
                    "You clicked the section: $clickedLabel",
                    Toast.LENGTH_SHORT
                ).show()
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "AI recognition accuracy",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.Start)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GaugeChart(
                    percentage = stats.meanAIConfidenceOnTotal,
                    label = "Overall\naverage",
                    modifier = Modifier.size(135.dp)
                )

                Spacer(modifier = Modifier.width(25.dp))

                GaugeChart(
                    percentage = stats.meanAIConfidenceOnSuccess,
                    label = "Only won\nchallenges",
                    modifier = Modifier.size(135.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Weekly activity",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp)
                    .align(Alignment.Start)
            )

            StatsLineChart(
                points = weeklyPoints,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
        }
    }
}

@Composable
fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}