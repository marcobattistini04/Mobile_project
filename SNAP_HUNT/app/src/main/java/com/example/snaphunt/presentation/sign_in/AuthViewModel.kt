package com.example.snaphunt.presentation.sign_in

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaphunt.data.repositories.authentication.AuthRepository
import com.example.snaphunt.data.repositories.user_settings.SettingsRepository
import com.example.snaphunt.network.NetworkMonitor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface SignInEvent {
    data object SignInSuccess : SignInEvent
}


class AuthViewModel(
    private val repo: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())

    val state = combine(repo.currentUser, _uiState) { user, uiState ->
        uiState.copy(user = user, isInitializing = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthUiState(isInitializing = true))

    private val _events = MutableSharedFlow<SignInEvent>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            val session = repo.getCurrentUser()
            if (session != null) {
                repo.refreshUser()
            }

            networkMonitor.isOnline.collect { isOnline ->
                if (isOnline) repo.refreshUser()
            }
        }
    }

    fun onSignIn(activity: Activity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = repo.signIn(activity)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSignInSuccessful = result.data != null,
                    error = result.errorMessage
                )
            }

            if (result.data != null) {
                _events.emit(SignInEvent.SignInSuccess)
                settingsRepository.syncFromCloud()
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                settingsRepository.syncToCloud()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                repo.signOut()
            }
        }
    }
}