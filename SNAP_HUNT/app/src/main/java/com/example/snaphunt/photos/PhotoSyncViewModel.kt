package com.example.snaphunt.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaphunt.data.local.PendingAttemptDao
import com.example.snaphunt.data.local.toDomain
import com.example.snaphunt.data.local.toEntity
import com.example.snaphunt.network.NetworkMonitor
import io.github.jan.supabase.auth.Auth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class PhotoSyncViewModel(
    private val networkMonitor: NetworkMonitor,
    private val syncManager: SyncManager,
    private val pendingAttemptDao: PendingAttemptDao,
    private val auth: Auth
) : ViewModel() {

    val isOnline = networkMonitor.isOnline

    init {
        observeNetworkAndSync()
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

                                val thumbFile = File(entity.localThumbnailPath)
                                if (thumbFile.exists()) thumbFile.delete()
                            }
                        }
                    }
                }
            }
        }
    }

    fun onPhotoTaken(attempt: PendingAttempt) {
        viewModelScope.launch {
            println("[DEBUG_SNAP] function initialized")
            val session = auth.currentSessionOrNull()
            println("[DEBUG_SNAP] session recovered: $session")
            val userId = auth.currentSessionOrNull()?.user?.id
            println("[DEBUG_SNAP] User ID extracted: $userId")

            if (userId == null) {
                println("[DEBUG_SNAP] STOP: userId is NULL! Entering in DEMO modality and exit.")
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    val thumbFile = File(attempt.localThumbnailPath)
                    if (thumbFile.exists()) thumbFile.delete()
                }
                return@launch
            }

            println("[DEBUG_SNAP] User logged in. Checking network state...")
            println("[DEBUG_SNAP] Network online? = ${isOnline.value}")

            println("[DEBUG_SNAP] syncAttempt()...")
            val success = syncManager.syncAttempt(attempt, userId)
            println("[DEBUG_SNAP] sync result = $success")

            if (success) {
                println("[DEBUG_SNAP] SUCCESS: Upload completed. Deleting thumbnail.")
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    val thumbFile = File(attempt.localThumbnailPath)
                    if (thumbFile.exists()) thumbFile.delete()
                }
            } else {
                println("[DEBUG_SNAP] FAILURE: sync returned false. Saving in Room.")
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    pendingAttemptDao.insert(attempt.toEntity().copy(synced = false))
                }
            }
            println("[DEBUG_SNAP] function terminated")
        }
    }
}