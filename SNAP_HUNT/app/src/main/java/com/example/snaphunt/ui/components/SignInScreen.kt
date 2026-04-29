package com.example.snaphunt.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.snaphunt.presentation.sign_in.AuthUiState
import java.nio.file.WatchEvent

@Composable
fun SignInScreen(
    state: AuthUiState,
    onSignInClick: () -> Unit
) {
    val ctx = LocalContext.current
    LaunchedEffect(key1 = state.error) {
        state.error?.let {error ->
            Toast.makeText(
                ctx,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onSignInClick) {
            Text("Sign in")
        }
    }
}