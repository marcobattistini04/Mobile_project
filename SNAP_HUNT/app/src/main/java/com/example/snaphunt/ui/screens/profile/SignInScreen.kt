package com.example.snaphunt.ui.screens.profile

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snaphunt.presentation.sign_in.AuthUiState

@Composable
fun SignInScreen(
    state: AuthUiState,
    motivation: String,
    onSignInClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val ctx = LocalContext.current
    val mainButtonBgColor = MaterialTheme.colorScheme.inverseSurface
    val mainButtonTextColor = MaterialTheme.colorScheme.inverseOnSurface
    val outlinedButtonBorderColor = MaterialTheme.colorScheme.outline
    val outlinedButtonTextColor = MaterialTheme.colorScheme.onSurface

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            Toast.makeText(ctx, error, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        // HEADER
        Text(
            text = "Your Profile",
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "No account signed in",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "To $motivation and use all features, please sign in.",
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // SIGN IN BUTTON
        Button(
            onClick = onSignInClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = mainButtonBgColor,
                contentColor = mainButtonTextColor
            )
        ) {
            Text("Sign in")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // INFO CARD STYLE SECTION
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {

            OutlinedButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (expanded) "Hide details" else "Why sign in?")
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {

                    Text(
                        text = "Why this is required:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Save your progress\n" +
                                "• Sync settings and progression data across devices\n" +
                                "• Enable Google Photos backup\n" +
                                "• Identify your account securely",
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Only minimal data of the Google account is registered (name and numeric ID).",
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}