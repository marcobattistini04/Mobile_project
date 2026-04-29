package com.example.snaphunt.presentation.sign_in

import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaphunt.data.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state = _state.asStateFlow()

    fun onSignIn(intent: Intent) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = repo.signIn(intent)

            _state.update {
                it.copy(
                    isLoading = false,
                    user = result.data,
                    error = result.errorMessage
                )
            }
        }
    }

    fun restore() {
        val user = repo.getCurrentUser()
        _state.update { it.copy(user = user) }
    }

    suspend fun signOut() {
        repo.signOut()
        _state.update { AuthUiState() }
    }

    suspend fun getSignInIntent(): IntentSender? {
        return repo.getIntentSender()
    }
}