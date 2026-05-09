package com.example.snaphunt.presentation.sign_in

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaphunt.data.repositories.authentication.AuthRepository
import com.example.snaphunt.data.repositories.user_settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repo: AuthRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state = _state.asStateFlow()

    fun onSignIn() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = repo.signIn()

            _state.update {
                it.copy(
                    isLoading = false,
                    isSignInSuccessful = result.data != null,
                    user = result.data,
                    error = result.errorMessage
                )
            }
            result.data?.userId?.let { userId ->
                settingsRepository.syncFromCloud()
            }
        }
    }

    fun restore() {
        val user = repo.getCurrentUser()
        _state.update { it.copy(user = user) }
    }

    fun signOut() {
        viewModelScope.launch {
            settingsRepository.syncToCloud()
            repo.signOut()
            _state.update { AuthUiState() }
        }
    }
}