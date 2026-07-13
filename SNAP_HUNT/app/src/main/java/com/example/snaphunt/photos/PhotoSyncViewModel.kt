package com.example.snaphunt.photos

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaphunt.data.local.PendingAttemptDao
import com.example.snaphunt.data.local.toDomain
import com.example.snaphunt.data.local.toEntity
import com.example.snaphunt.image_recognition.DailyObjects
import com.example.snaphunt.image_recognition.DetectionResults
import com.example.snaphunt.network.NetworkMonitor
import io.github.jan.supabase.auth.Auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

sealed class ScreenState {
    data object Idle : ScreenState()
    data class ChallengeProposed(val challenge: DailyObjects) : ScreenState()
    data class CameraActive(val challenge: DailyObjects) : ScreenState()
    data class PhotoPreview(val uri: Uri, val challenge: DailyObjects) : ScreenState()
    data object Analyzing : ScreenState()
}

class PhotoSyncViewModel(
    private val networkMonitor: NetworkMonitor,
    private val syncManager: SyncManager,
    private val pendingAttemptDao: PendingAttemptDao,
    private val auth: Auth,
    private val imageStorageManager: ImageStorageManager
) : ViewModel() {

    val isOnline = networkMonitor.isOnline
    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Idle)
    val uiState: StateFlow<ScreenState> = _uiState.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _isAnalysisPerformed = MutableStateFlow(false)
    val isAnalysisPerformed = _isAnalysisPerformed.asStateFlow()

    private val _savingButtonEnabled = MutableStateFlow(true)
    val savingButtonEnabled = _savingButtonEnabled.asStateFlow()

    private val _currentChallenge = MutableStateFlow(DailyObjects.DINING_TABLE)

    private val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        observeNetworkAndSync()
    }

    fun showUIMessage(message: String) {
        viewModelScope.launch {
            _uiEvent.send(message)
        }
    }

    fun resetToIdle() {
        _uiState.value = ScreenState.Idle
        _isAnalysisPerformed.value = false
    }
    fun startNewChallenge() {
        val current = _currentChallenge.value
        val nextOptions = DailyObjects.entries.filter { it != current }
        if (nextOptions.isNotEmpty()) {
            _currentChallenge.value = nextOptions.random()
        }
        _uiState.value = ScreenState.ChallengeProposed(_currentChallenge.value)
    }

    fun onCameraCancelled() {
        _uiState.update {
            if (it is ScreenState.CameraActive) {
                ScreenState.ChallengeProposed(it.challenge)
            } else {
                ScreenState.Idle
            }
        }
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
        _isAnalysisPerformed.value = true
    }

    fun onPhotoCaptured(uri: Uri, challenge: DailyObjects) {
        _uiState.value = ScreenState.PhotoPreview(uri, challenge)
    }

    fun onAnalysisTerminated() {
        _isAnalysisPerformed.value = false
    }

    fun processAndSave(bitmap: Bitmap, results: DetectionResults, challenge: DailyObjects) {
        _isProcessing.value = true
        _savingButtonEnabled.value = false

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val thumbnailBitmap = imageStorageManager.createThumbnail(bitmap, maxSize = 512)
                val thumbnailFile = imageStorageManager.saveLocalImage(thumbnailBitmap)
                val attempt = PendingAttempt(
                    id = UUID.randomUUID().toString(),
                    challengeId = UUID.randomUUID().toString(),
                    challengeText = "Find a... ${challenge.keyword}",
                    success = results.success,
                    skipped = false,
                    aiLabel = results.aiLabel,
                    aiConfidence = results.aiConfidence,
                    localThumbnailPath = thumbnailFile.absolutePath,
                    createdAt = System.currentTimeMillis(),
                    points = results.points,
                    additionalObjects = results.additionalObjects
                )

                onPhotoTaken(attempt)

            } catch (e: Exception) {
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun saveToGallery(bitmap: Bitmap) {
        _isProcessing.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val success = imageStorageManager.saveImageToGallery(bitmap)
            if(success) {
                showUIMessage("Photo saved in phone gallery!")
            } else {
                showUIMessage("Error occurred during photo saving!")
            }
            withContext(Dispatchers.Main) {
                _isProcessing.value = false
                _uiState.value = ScreenState.Idle
            }
        }
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
            val userId = auth.currentSessionOrNull()?.user?.id

            // if not user is singed in, delete the thumbnail and stop
            if (userId == null) {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    if(attempt.localThumbnailPath != null) {
                        val thumbFile = File(attempt.localThumbnailPath)
                        if (thumbFile.exists()) thumbFile.delete()
                    }

                }
                _isProcessing.value = false
                return@launch
            }

            val success = syncManager.syncAttempt(attempt, userId)

            // if the upload session return success, delete the thumbnail and stop
            if (success) {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    if(attempt.localThumbnailPath != null) {
                        val thumbFile = File(attempt.localThumbnailPath)
                        if (thumbFile.exists()) thumbFile.delete()
                    }
                }
            } else { // if the upload session returned error, save the attempt in Room in order to do a new loading attempt in the future
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    pendingAttemptDao.insert(attempt.toEntity().copy(synced = false))
                }
            }
            _isProcessing.value = false
        }
    }
}