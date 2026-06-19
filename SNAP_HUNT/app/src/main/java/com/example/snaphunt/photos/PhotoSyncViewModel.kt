package com.example.snaphunt.photos

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaphunt.data.local.PendingAttemptDao
import com.example.snaphunt.data.local.toDomain
import com.example.snaphunt.data.local.toEntity
import com.example.snaphunt.image_recognition.DailyObjects
import com.example.snaphunt.network.NetworkMonitor
import io.github.jan.supabase.auth.Auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

sealed class ScreenState {
    data object Idle : ScreenState()
    data class ChallengeProposed(val challenge: DailyObjects) : ScreenState()
    data class CameraActive(val challenge: DailyObjects) : ScreenState()
}

class PhotoSyncViewModel(
    private val networkMonitor: NetworkMonitor,
    private val syncManager: SyncManager,
    private val pendingAttemptDao: PendingAttemptDao,
    private val auth: Auth
) : ViewModel() {

    val isOnline = networkMonitor.isOnline
    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Idle)
    val uiState: StateFlow<ScreenState> = _uiState.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _savingButtonEnabled = MutableStateFlow(true)
    val savingButtonEnabled = _savingButtonEnabled.asStateFlow()

    private val _currentChallenge = MutableStateFlow(DailyObjects.DINING_TABLE)

    init {
        observeNetworkAndSync()
    }

    fun resetToIdle() {
        _uiState.value = ScreenState.Idle
        println("[DEBUG_SNAP] State reset to IDLE")
    }
    fun startNewChallenge() {
        val current = _currentChallenge.value
        val nextOptions = DailyObjects.entries.filter { it != current }
        if (nextOptions.isNotEmpty()) {
            _currentChallenge.value = nextOptions.random()
        }
        _uiState.value = ScreenState.ChallengeProposed(_currentChallenge.value)
    }

    fun rejectChallenge(challenge: DailyObjects) {
        val failedAttempt = PendingAttempt(
            id = UUID.randomUUID().toString(),
            challengeId = UUID.randomUUID().toString(),
            challengeText = challenge.keyword,
            localThumbnailPath = null,
            createdAt = System.currentTimeMillis(),
            success = false,
            skipped = true,
            points = 0,
            additionalObjects = 0
        )
        onPhotoTaken(failedAttempt)
        _uiState.value = ScreenState.Idle
    }

    fun acceptChallenge(challenge: DailyObjects) {
        _uiState.value = ScreenState.CameraActive(challenge)
        _savingButtonEnabled.value = true
    }

    private fun observeNetworkAndSync() {
        viewModelScope.launch {
            networkMonitor.isOnline.collectLatest { online ->

                val userId = auth.currentSessionOrNull()?.user?.id ?: return@collectLatest

                if (online) {
                    val pendingList = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        pendingAttemptDao.getPending()
                    }
                    for (entity in pendingList) {
                        val success = syncManager.syncAttempt(entity.toDomain(), userId)
                        if (success) {
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                                pendingAttemptDao.delete(entity)

                                if(entity.localThumbnailPath != null) {
                                    val thumbFile = File(entity.localThumbnailPath)
                                    if (thumbFile.exists()) thumbFile.delete()
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    fun onPhotoTaken(attempt: PendingAttempt) {
        _isProcessing.value = true
        _savingButtonEnabled.value = false
        viewModelScope.launch {
            println("[DEBUG_SNAP] function initialized")
            val session = auth.currentSessionOrNull()
            println("[DEBUG_SNAP] session recovered: $session")
            val userId = auth.currentSessionOrNull()?.user?.id
            println("[DEBUG_SNAP] User ID extracted: $userId")

            if (userId == null) {
                println("[DEBUG_SNAP] STOP: userId is NULL! Entering in DEMO modality and exit.")
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    if(attempt.localThumbnailPath != null) {
                        val thumbFile = File(attempt.localThumbnailPath)
                        if (thumbFile.exists()) thumbFile.delete()
                    }

                }
                _isProcessing.value = false
                return@launch
            }

            println("[DEBUG_SNAP] User logged in. Checking network state...")
            println("[DEBUG_SNAP] Network online? = ${isOnline.value}")

            println("[DEBUG_SNAP] syncAttempt()...")
            val success = syncManager.syncAttempt(attempt, userId)
            println("[DEBUG_SNAP] sync result = $success")

            if (success) {
                println("[DEBUG_SNAP] SUCCESS: Upload completed. Deleting thumbnail if exists.")
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    if(attempt.localThumbnailPath != null) {
                        val thumbFile = File(attempt.localThumbnailPath)
                        if (thumbFile.exists()) thumbFile.delete()
                    }
                }
            } else {
                println("[DEBUG_SNAP] FAILURE: sync returned false. Saving in Room.")
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    pendingAttemptDao.insert(attempt.toEntity().copy(synced = false))
                }
            }
            println("[DEBUG_SNAP] function terminated")
            _isProcessing.value = false
        }
    }
}