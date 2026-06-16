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
import androidx.compose.material3.Text
import coil.compose.AsyncImage
import com.example.snaphunt.photos.ImageStorageManager
import com.example.snaphunt.photos.PendingAttempt
import com.example.snaphunt.photos.PhotoSyncViewModel
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.ui.screens.home.image_recognition.AnalysisScreen
import com.example.snaphunt.utils.uriToBitmap
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@Composable
fun QuickActions(authViewModel: AuthViewModel, themeState: SettingsState, themeActions: SettingsActions) {
    val ctx = LocalContext.current
    val imageStorageManager =  ImageStorageManager()
    val viewModel: PhotoSyncViewModel = koinViewModel()
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
        AnalysisScreen(pictureUri)
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
                val originalBitmap = uriToBitmap(pictureUri, ctx.contentResolver)
                imageStorageManager.saveImageToGallery(originalBitmap, ctx)
                val thumbnailBitmap = imageStorageManager.createThumbnail(originalBitmap, maxSize = 512)
                val thumbnailFile = imageStorageManager.saveLocalImage(thumbnailBitmap, ctx)
                val attempt = PendingAttempt(
                    id = UUID.randomUUID().toString(),
                    challengeId = UUID.randomUUID().toString(),
                    challengeText = "Quick Snap Challenge",
                    localThumbnailPath = thumbnailFile.absolutePath,
                    createdAt = System.currentTimeMillis(),
                    success = false
                )

                viewModel.onPhotoTaken(attempt)
                reset()
            }) {
                Text("Save Picture")
            }
        }
    }
}
