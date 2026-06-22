package com.example.snaphunt.data.repositories.authentication

import android.app.Activity
import android.content.Context
import com.example.snaphunt.data.user.UserLogInData
import com.example.snaphunt.presentation.sign_in.GoogleAuthUiClient
import com.example.snaphunt.presentation.sign_in.SignInResult
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(
    private val context: Context,
    private val supabase: SupabaseClient
) {
    private val googleAuthUiClient = GoogleAuthUiClient(context, supabase)

    private val _currentUser = MutableStateFlow<UserLogInData?>(null)
    val currentUser = _currentUser.asStateFlow()

    suspend fun signIn(activity: Activity): SignInResult {
        val result = googleAuthUiClient.signIn(activity)
        if (result.data != null) {
            _currentUser.value = result.data
        }
        return result
    }

    suspend fun signOut() {
        googleAuthUiClient.signOut()
        _currentUser.value = null
    }

    suspend fun refreshUser() {
        val user = googleAuthUiClient.getSignedInUser()
        _currentUser.value = user
    }

    suspend fun getCurrentUser(): UserLogInData? {
        val user = googleAuthUiClient.getSignedInUser()
        _currentUser.value = user
        return user
    }
}