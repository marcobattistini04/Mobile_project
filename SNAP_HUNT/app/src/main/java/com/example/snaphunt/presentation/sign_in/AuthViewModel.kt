package com.example.snaphunt.presentation.sign_in

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaphunt.data.repositories.authentication.AuthRepository
import com.example.snaphunt.data.repositories.user_settings.SettingsRepository
import com.example.snaphunt.network.NetworkMonitor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _state = MutableStateFlow(AuthUiState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<SignInEvent>()
    val events = _events.asSharedFlow()

    val isLoggedIn: Boolean
        get() = state.value.user != null

    val userId: String?
        get() = state.value.user?.userId

    init {
        viewModelScope.launch {
            restore()
        }
    }

    init {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                if (isOnline) {
                    refreshProfile()
                }
            }
        }
    }

    fun onSignIn(activity: Activity) {
        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, error = null) }

            val result = repo.signIn(activity)

            _state.update {
                it.copy(
                    isLoading = false,
                    isSignInSuccessful = result.data != null,
                    user = result.data,
                    error = result.errorMessage
                )
            }

            result.data?.userId?.let {
                onSignInSuccess()
                settingsRepository.syncFromCloud()
            }
        }
    }

    fun onSignInSuccess() {
        viewModelScope.launch {
            _events.emit(SignInEvent.SignInSuccess)
        }
    }

    suspend fun restore() {
        val user = repo.getCurrentUser()
        _state.update { it.copy(user = user) }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                settingsRepository.syncToCloud()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                repo.signOut()
                _state.value = AuthUiState()
            }
        }
    }

    private var isFetching = false
    fun refreshProfile() {
        if (isFetching) return
        isFetching = true
        viewModelScope.launch {
            try {
                val updatedUser = repo.getCurrentUser()
                _state.update { it.copy(user = updatedUser) }
            } finally {
                isFetching = false
            }
        }
    }
}