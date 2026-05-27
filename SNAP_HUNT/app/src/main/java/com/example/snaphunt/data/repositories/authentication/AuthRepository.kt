package com.example.snaphunt.data.repositories.authentication

import android.app.Activity
import android.content.Context
import com.example.snaphunt.data.user.UserLogInData
import com.example.snaphunt.presentation.sign_in.GoogleAuthUiClient
import com.example.snaphunt.presentation.sign_in.SignInResult
import io.github.jan.supabase.SupabaseClient

class AuthRepository(
    context: Context,
    supabase: SupabaseClient
) {
    private val googleAuthUiClient = GoogleAuthUiClient(context, supabase)

    suspend fun signIn(activity: Activity): SignInResult {
        return googleAuthUiClient.signIn(activity)
    }

    suspend fun signOut() {
        googleAuthUiClient.signOut()
    }

    suspend fun getCurrentUser(): UserLogInData? {
        return googleAuthUiClient.getSignedInUser()
    }
}