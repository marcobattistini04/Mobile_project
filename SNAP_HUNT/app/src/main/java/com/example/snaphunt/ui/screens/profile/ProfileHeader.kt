package com.example.snaphunt.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

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

            Button( onClick = {
                    authViewModel.signOut()
            }) {
                Text(textAlign = TextAlign.Center, fontSize = 16.sp, fontWeight = FontWeight.Bold, text = "Sign out")
            }
        }

        Row(
             modifier = Modifier
                 .fillMaxSize()
                 .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            AsyncImage(
                model = user.profilePictureUri,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Text(
                text = user.username!!,
                lineHeight = 64.sp,
                fontSize = 36.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Left
            )
        }

        // NEW GRAPHIC OPTIONS LIKE THE GOOGLE IMAGE PROFILE ECC
    }
}