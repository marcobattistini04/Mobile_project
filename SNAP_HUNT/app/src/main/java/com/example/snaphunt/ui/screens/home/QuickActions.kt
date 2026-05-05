package com.example.snaphunt.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState
import com.example.snaphunt.utils.rememberCameraLauncher
import com.example.snaphunt.utils.saveImageToStorage
import androidx.compose.material3.Text
import coil.compose.AsyncImage
import com.example.snaphunt.presentation.sign_in.AuthViewModel

@Composable
fun QuickActions(authViewModel: AuthViewModel, themeState: SettingsState, themeActions: SettingsActions) {
    val ctx = LocalContext.current
    val (pictureUri, takePicture, reset) = rememberCameraLauncher()
    if (pictureUri == null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = takePicture) {
                Text("New SnapHunt!")
            }
        }
    } else {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = pictureUri,
                contentDescription = "Captured image",
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Placeholder, more text and image analysis in the future",
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {reset()}) {
                Text("Do not Save Picture")
            }

            Button(onClick = {
                saveImageToStorage(pictureUri, ctx.contentResolver)
                reset()
            }) {
                Text("Save Picture")
            }
        }
    }
}
