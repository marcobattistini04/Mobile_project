package com.example.snaphunt.presentation.sign_in

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.snaphunt.R
import com.example.snaphunt.data.user.UserLogInData
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(
    private val context: Context
) {
    private val auth = Firebase.auth
    private val credentialManager = CredentialManager.create(context)

    suspend fun signIn(activity: Activity): SignInResult {
        return try {

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(
                    context.getString(R.string.web_client_id)
                )
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = activity, // 👈 MUST be Activity here
                request = request
            )

            val credential = result.credential

            if (
                credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {

                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)

                val googleIdToken = googleIdTokenCredential.idToken

                val firebaseCredential =
                    GoogleAuthProvider.getCredential(googleIdToken, null)

                val user = auth.signInWithCredential(firebaseCredential)
                    .await()
                    .user

                SignInResult(
                    data = user?.run {
                        UserLogInData(
                            userId = uid,
                            username = displayName,
                            profilePictureUri = photoUrl?.toString()
                        )
                    },
                    errorMessage = null
                )

            } else {
                SignInResult(
                    data = null,
                    errorMessage = "Invalid credential type"
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
            SignInResult(
                data = null,
                errorMessage = e.message ?: "Sign in failed"
            )
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }

    fun getSignedInUser(): UserLogInData? {
        return auth.currentUser?.run {
            UserLogInData(
                userId = uid,
                username = displayName,
                profilePictureUri = photoUrl?.toString()
            )
        }
    }
}