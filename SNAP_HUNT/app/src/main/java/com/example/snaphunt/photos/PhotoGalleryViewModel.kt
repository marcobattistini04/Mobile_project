package com.example.snaphunt.photos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaphunt.data.user.UserChallengeItem
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhotoGalleryViewModel(
    private val postgrest: Postgrest,
    private val storage: Storage
): ViewModel() {
    private val _challengeState = MutableStateFlow<List<UserChallengeItem>>(emptyList())
    val challengeState = _challengeState.asStateFlow()

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
                    val url = storage.from("challenge-images").publicUrl(item.storagePath)
                    item.copy(storagePath = url)
                }

                _challengeState.value = updatedList
            } catch (e: Exception) {
                Log.e("SupabaseDebug", "Errore: ${e.message}")
            }
        }
    }

    fun loadChallengeById(id: String) {
        viewModelScope.launch {
            try {
                val item = postgrest.from("challenge_results")
                    .select { filter { eq("id", id) } }
                    .decodeSingle<UserChallengeItem>()

                val url = storage.from("challenge-images").publicUrl(item.storagePath)

                _selectedChallenge.value = item.copy(storagePath = url)
            } catch (e: Exception) {
                Log.e("SupabaseDebug", "Errore: ${e.message}")
            }
        }
    }
}