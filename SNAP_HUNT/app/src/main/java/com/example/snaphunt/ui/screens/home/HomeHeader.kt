package com.example.snaphunt.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.snaphunt.photos.PhotoGalleryViewModel
import com.example.snaphunt.photos.PhotoSyncViewModel
import com.example.snaphunt.photos.ScreenState
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.ui.components.MultiplierStreakCard
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState
import java.time.DayOfWeek
import com.example.snaphunt.ui.components.WelcomeFeatureCard


@Composable
fun HomeHeader(
    authViewModel: AuthViewModel,
    photoSyncViewModel: PhotoSyncViewModel,
    photoGalleryViewModel: PhotoGalleryViewModel,
    themeState: SettingsState,
    themeActions: SettingsActions
) {
    val state by authViewModel.state.collectAsStateWithLifecycle()
    val uiState by photoSyncViewModel.uiState.collectAsStateWithLifecycle()
    val rawChallenges by photoGalleryViewModel.challengeState.collectAsStateWithLifecycle()
    val multiplier by photoGalleryViewModel.weeklyMultiplier.collectAsStateWithLifecycle()

    val user = state.user
    LaunchedEffect(uiState, user) {
        if (uiState is ScreenState.Idle) {
            user?.userId?.let { userId ->
                photoGalleryViewModel.loadUserChallenges(userId)
            }
        }
    }

    val activeDays = remember(rawChallenges) {
        val today = java.time.LocalDate.now()
        val monday = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = today.with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        rawChallenges.mapNotNull { challenge ->
            try { java.time.LocalDate.parse(challenge.createdAt.substring(0, 10)) } catch (e: Exception) { null }
        }.filter { date ->
            !date.isBefore(monday) && !date.isAfter(sunday)
        }.map { date ->
            date.dayOfWeek
        }.toSet()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.size(8.dp))
        if (user == null) {
            Text(
                text = "Welcome User!",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                is ScreenState.Idle -> {
                    WelcomeFeatureCard()
                }
                else -> {}
            }
        } else {
            when (val state = uiState) {
                is ScreenState.Idle -> {
                    Text(
                        text = "Welcome " + user.username.toString() + "!",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MultiplierStreakCard(multiplier = multiplier, activeDays = activeDays)

                }
                else -> {}
            }
        }
    }
}