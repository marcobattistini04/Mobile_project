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

                // Se l'utente NON è loggato (userId è null), stop.
                val userId = auth.currentSessionOrNull()?.user?.id ?: return@collectLatest

                //l'utente è registrato e online: avvia il sync
                if (online) {
                    val pendingList = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        pendingAttemptDao.getPending()
                    }
                    for (entity in pendingList) {
                        val success = syncManager.syncAttempt(entity.toDomain(), userId)
                        if (success) {
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                                // sincornizzazione con successo, si elimina la foto in pending
                                pendingAttemptDao.delete(entity)

                                // Elimina la miniatura dal telefono
                                val thumbFile = File(entity.localThumbnailPath)
                                if (thumbFile.exists()) thumbFile.delete()
                            }
                        }
                    }
                }
            }
        }
    }

    // Questa è la funzione che chiami dalla UI quando l'utente scatta la foto
    fun onPhotoTaken(attempt: PendingAttempt) {
        viewModelScope.launch {
            println("=== [DEBUG_SNAP] FUNZIONE AVVIATA ===")
            val session = auth.currentSessionOrNull()
            println("[DEBUG_SNAP] Sessione recuperata: $session")
            val userId = auth.currentSessionOrNull()?.user?.id
            println("[DEBUG_SNAP] User ID estratto: $userId")

            if (userId == null) {
                // MODALITÀ DEMO / UTENTE NON LOGGATO:
                //In futuro funzionalità demo
                println("[DEBUG_SNAP] 🛑 STOP: l'userId è NULL! Entro in modalità DEMO ed esco.")
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    val thumbFile = File(attempt.localThumbnailPath)
                    if (thumbFile.exists()) thumbFile.delete()
                }
                return@launch
            }

            println("[DEBUG_SNAP] ✅ Utente Loggato. Controllo lo stato della rete...")
            println("[DEBUG_SNAP] Rete Online? = ${isOnline.value}")

            // SE L'UTENTE È LOGGATO si fa subito sync
            println("[DEBUG_SNAP] 🚀 Avvio syncManager.syncAttempt()...")
            val successo = syncManager.syncAttempt(attempt, userId)
            println("[DEBUG_SNAP] Risultato del sync = $successo")

            if (successo) {
                println("[DEBUG_SNAP] 🎉 SUCCESS: Upload completato. Cancello la miniatura.")
                // Si elimina subito la miniatura temporanea.
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    val thumbFile = File(attempt.localThumbnailPath)
                    if (thumbFile.exists()) thumbFile.delete()
                }
            } else {
                println("[DEBUG_SNAP] ⚠️ FALLIMENTO: Il sync ha restituito false. Salvo in Room.")
                // Se l'upload online fallisce, si salva i dati su Room, compresa la miniatura, la quale verrà cancellata in seguito
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    pendingAttemptDao.insert(attempt.toEntity().copy(synced = false))
                }
            }
            println("=== [DEBUG_SNAP] FUNZIONE TERMINATA ===")
        }
    }
}