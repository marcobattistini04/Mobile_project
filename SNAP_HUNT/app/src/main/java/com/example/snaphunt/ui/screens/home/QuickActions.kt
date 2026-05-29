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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.snaphunt.photos.ImageStorageManager
import com.example.snaphunt.photos.PendingAttempt
import com.example.snaphunt.photos.PhotoSyncViewModel
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.utils.uriToBitmap
import org.koin.androidx.compose.koinViewModel
import org.koin.viewmodel.factory.KoinViewModelFactory
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
                // bitmap originale scattata
                val originalBitmap = uriToBitmap(pictureUri, ctx.contentResolver)

                // salvataggio dell'immagine originale sul telefono (per la UI locale)
                val originalFile = imageStorageManager.saveLocalImage(originalBitmap, ctx)

                // creazione della thumbnail
                val thumbnailBitmap = imageStorageManager.createThumbnail(originalBitmap, maxSize = 512)

                // salvataggio thumbnail sul telefono (servirà per l'upload)
                val thumbnailFile = imageStorageManager.saveLocalImage(thumbnailBitmap, ctx)

                // tentativo con ENTRAMBI i percorsi
                val attempt = PendingAttempt(
                    id = UUID.randomUUID().toString(),
                    challengeId = UUID.randomUUID().toString(),
                    challengeText = "Quick Snap Challenge",
                    localImagePath = originalFile.absolutePath,       // Foto pesante
                    localThumbnailPath = thumbnailFile.absolutePath,   // Foto leggera per il cloud
                    createdAt = System.currentTimeMillis(),
                    success = false
                )

                // 6. Passiamo al ViewModel
                viewModel.onPhotoTaken(attempt)

                reset()
            }) {
                Text("Save Picture")
            }
        }
    }
}
