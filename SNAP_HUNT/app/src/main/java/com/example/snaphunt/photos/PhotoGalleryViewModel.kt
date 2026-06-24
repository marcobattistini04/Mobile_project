package com.example.snaphunt.photos

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.snaphunt.data.user.UserChallengeItem
import com.example.snaphunt.network.NetworkMonitor
import com.example.snaphunt.data.user.UserStats
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PhotoGalleryViewModel(
    private val postgrest: Postgrest,
    private val storage: Storage,
    private val networkMonitor: NetworkMonitor
): ViewModel() {
    private val _challengeState = MutableStateFlow<List<UserChallengeItem>>(emptyList())
    val challengeState = _challengeState.asStateFlow()

    val isOnline = networkMonitor.isOnline

    private val _selectedChallenge = MutableStateFlow<UserChallengeItem?>(null)
    val selectedChallenge = _selectedChallenge.asStateFlow()

    fun loadUserChallenges(userId: String) {
        viewModelScope.launch {
            try {
                val rawList = postgrest.from("challenge_results")
                    .select {
                        filter { eq("user_id", userId) }
                    }.decodeList<UserChallengeItem>()

                val updatedList = rawList.map { item ->
                    val validUrl = if (!item.storagePath.isNullOrBlank()) {
                        storage.from("challenge-images").publicUrl(item.storagePath)
                    } else {
                        null
                    }

                    item.copy(storagePath = validUrl ?: item.storagePath)
                }

                _challengeState.value = updatedList
            } catch (e: Exception) {
                Log.e("SupabaseDebug", "Error: ${e.message}")
            }
        }
    }

    fun loadChallengeById(id: String) {
        viewModelScope.launch {
            try {
                val item = postgrest.from("challenge_results")
                    .select { filter { eq("id", id) } }
                    .decodeSingle<UserChallengeItem>()

                val validUrl = if (!item.storagePath.isNullOrBlank()) {
                    storage.from("challenge-images").publicUrl(item.storagePath)
                } else {
                    null
                }

                _selectedChallenge.value = item.copy(storagePath = validUrl ?: item.storagePath)
            } catch (e: Exception) {
                Log.e("SupabaseDebug", "Error: ${e.message}")
            }
        }
    }

    val stats: StateFlow<UserStats> = _challengeState
        .map { list ->
            val list = _challengeState.value
            var wonCount = 0
            var lostCount = 0
            var skippedCount = 0
            var totalPoints = 0
            var totalAddObjects = 0
            var sumConfidences = 0.0
            var countConfidencesOnSuccess = 0
            var countConfidencesOnTotal = 0

            for (item in list) {
                if (item.success) {
                    wonCount++
                    totalPoints += item.points
                    totalAddObjects += item.additionalObjects
                    countConfidencesOnSuccess ++
                } else if(!item.skipped){
                    lostCount++
                } else {
                    skippedCount++
                }
                if (item.aiConfidence != null) {
                    sumConfidences += item.aiConfidence
                }
                countConfidencesOnTotal ++
            }
            UserStats(
                totalChallenges = list.size,
                wonChallenges = wonCount,
                lostChallenges = lostCount,
                skippedChallenges = skippedCount,
                totalPoints = totalPoints,
                totalAdditionalObjects = totalAddObjects,
                meanAIConfidenceOnSuccess = if (countConfidencesOnSuccess > 0) (sumConfidences / countConfidencesOnSuccess).toFloat() else 0.0f,
                meanAIConfidenceOnTotal = if (countConfidencesOnTotal > 0) (sumConfidences / countConfidencesOnTotal).toFloat() else 0.0f
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserStats()
        )

}