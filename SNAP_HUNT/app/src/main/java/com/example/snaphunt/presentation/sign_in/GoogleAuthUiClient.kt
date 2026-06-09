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
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

class GoogleAuthUiClient(
    private val context: Context,
    private val supabase: SupabaseClient
) {

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
                context = activity,
                request = request
            )

            val credential = result.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {

                val googleIdToken = GoogleIdTokenCredential
                    .createFrom(credential.data)
                    .idToken

                supabase.auth.signInWith(IDToken) {
                    idToken = googleIdToken
                    provider = Google
                }

                val user = supabase.auth.currentUserOrNull()
                    ?: return SignInResult(
                        data = null,
                        errorMessage = "User not available after sign-in"
                    )


                val profile = supabase
                    .from("profiles")
                    .select {
                        filter { eq("user_id", user.id) }
                    }
                    .decodeSingleOrNull<ProfileRow>()


                SignInResult(
                    data = UserLogInData(
                        userId = user.id,
                        username = profile?.username,
                        profilePictureUri = profile?.avatar_url
                    ),
                    errorMessage = null
                )

            } else {
                SignInResult(
                    data = null,
                    errorMessage = "Invalid credential type"
                )
            }

        } catch (e: Exception) {
            SignInResult(
                data = null,
                errorMessage = e.message ?: "Sign in failed"
            )
        }
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }

    suspend fun getSignedInUser(): UserLogInData? {
        val user = supabase.auth.currentUserOrNull() ?: return null

        val profile = try {
            supabase
                .from("profiles")
                .select {
                    filter { eq("user_id", user.id) }
                }
                .decodeSingleOrNull<ProfileRow>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        return UserLogInData(
            userId = user.id,
            username = profile?.username ?: user.userMetadata?.get("name")?.toString(),
            profilePictureUri = profile?.avatar_url ?: user.userMetadata?.get("avatar_url")?.toString()
        )
    }
}

@Serializable
data class ProfileRow(
    val user_id: String,
    val username: String? = null,
    val avatar_url: String? = null
)