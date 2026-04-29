package com.example.snaphunt.data.repositories

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.snaphunt.data.user.UserLogInData
import com.example.snaphunt.presentation.sign_in.GoogleAuthUiClient
import com.example.snaphunt.presentation.sign_in.SignInResult
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.flow.MutableStateFlow

class AuthRepository(
    context: Context
) {

    private val oneTapClient = Identity.getSignInClient(context)

    private val googleAuthUiClient = GoogleAuthUiClient(
        context,
        oneTapClient
    )

    suspend fun signIn(intent: Intent): SignInResult {
        return googleAuthUiClient.getSignWithIntent(intent)
    }

    suspend fun signOut() {
        googleAuthUiClient.signOut()
    }

    fun getCurrentUser(): UserLogInData? {
        return googleAuthUiClient.getSignedInUser()
    }

    suspend fun getIntentSender(): IntentSender? {
        return googleAuthUiClient.signIn()
    }
}