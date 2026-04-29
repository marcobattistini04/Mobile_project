package com.example.snaphunt.presentation.sign_in

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.snaphunt.data.repositories.AuthRepository

class AuthViewModelFactory(
    private val repository: AuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repository) as T
    }
}