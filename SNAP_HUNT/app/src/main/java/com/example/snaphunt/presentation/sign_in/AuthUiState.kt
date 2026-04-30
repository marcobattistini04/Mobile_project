package com.example.snaphunt.presentation.sign_in

import com.example.snaphunt.data.user.UserLogInData

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val user: UserLogInData? = null,
    val error: String? = null
)
