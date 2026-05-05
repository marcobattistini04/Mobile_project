package com.example.snaphunt.presentation.sign_in

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.snaphunt.data.repositories.AuthRepository
import com.example.snaphunt.data.repositories.SettingsRepository

class AuthViewModelFactory(
    private val repository: AuthRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repository, settingsRepository) as T
    }
}