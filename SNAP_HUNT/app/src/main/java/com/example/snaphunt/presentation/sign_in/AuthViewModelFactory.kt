package com.example.snaphunt.presentation.sign_in

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.snaphunt.data.repositories.authentication.AuthRepository
import com.example.snaphunt.data.repositories.user_settings.SettingsRepository
import com.example.snaphunt.network.NetworkMonitor

class AuthViewModelFactory(
    private val repository: AuthRepository,
    private val networkMonitor: NetworkMonitor,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repository, settingsRepository, networkMonitor) as T
    }
}