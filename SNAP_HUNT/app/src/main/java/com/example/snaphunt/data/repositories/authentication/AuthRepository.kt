package com.example.snaphunt.data.repositories.authentication

import android.app.Activity
import android.content.Context
import com.example.snaphunt.data.user.UserLogInData
import com.example.snaphunt.presentation.sign_in.GoogleAuthUiClient
import com.example.snaphunt.presentation.sign_in.SignInResult

class AuthRepository(
    context: Context
) {
    private val googleAuthUiClient = GoogleAuthUiClient(context)

    suspend fun signIn(activity: Activity): SignInResult {
        return googleAuthUiClient.signIn(activity)
    }

    suspend fun signOut() {
        googleAuthUiClient.signOut()
    }

    fun getCurrentUser(): UserLogInData? {
        return googleAuthUiClient.getSignedInUser()
    }
}