package com.example.snaphunt.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.snaphunt.data.user.UserLogInData
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState

@Composable
fun ProfileHeader(
    authViewModel: AuthViewModel,
    user: UserLogInData,
    themeState: SettingsState,
    themeActions: SettingsActions
) {
    val parts = user.username?.trim()?.split(" ") ?: emptyList()

    val firstName = parts.firstOrNull() ?: ""
    val lastName = parts.drop(1).joinToString(" ")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Your Profile",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold
            )

            Button(onClick = { authViewModel.signOut() }) {
                Text("Sign out")
            }
        }

        // PROFILE INFO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = user.profilePictureUri,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = firstName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = lastName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // FUTURE SECTION PLACEHOLDER
        Spacer(modifier = Modifier.height(12.dp))
    }
}