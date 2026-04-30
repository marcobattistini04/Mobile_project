package com.example.snaphunt.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
    LaunchedEffect(key1 = state.error) {
        state.error?.let {error ->
            Toast.makeText(
                ctx,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(100.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(64.dp)
        ) {
            Text(
                text = "Your Profile",
                fontSize = 36.sp,
                fontWeight = FontWeight.SemiBold
            )

            Button(onClick = onSignInClick) {
                Text("Sign in")
            }
        }

            Text(
                text = "Oops! No account signed in!\n" +
                        "In order to $motivation and to use the other features of the app you need to sign in",
                fontSize = 20.sp
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(16.dp)
            ) {

                OutlinedButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (expanded) "Close" else "Why I need to Sign in?")
                }
                AnimatedVisibility(visible = expanded) {
                    Column {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "A demo of the app functionality is available to everyone, even without signing in! \n" +
                                "Still, in order to save your previous challenges, save your photos on google photo and receive new traits " +
                                "You need to have a Google account linked.\n The app will ONLY memorize your account name and a numeric ID" +
                                " useful to recognize you",
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }