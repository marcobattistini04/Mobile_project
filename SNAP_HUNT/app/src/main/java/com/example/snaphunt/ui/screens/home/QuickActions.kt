package com.example.snaphunt.ui.screens.home

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.snaphunt.image_recognition.ObjectDetectionViewModel
import com.example.snaphunt.photos.ImageStorageManager
import com.example.snaphunt.photos.PendingAttempt
import com.example.snaphunt.photos.PhotoSyncViewModel
import com.example.snaphunt.photos.ScreenState
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.ui.screens.home.image_recognition.AnalysisScreen
import com.example.snaphunt.utils.uriToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color

@Composable
fun QuickActions(
    objectDetectionViewModel: ObjectDetectionViewModel,
    photoSyncViewModel: PhotoSyncViewModel,
    authViewModel: AuthViewModel,
    themeState: SettingsState,
    themeActions: SettingsActions
) {
    val ctx = LocalContext.current
    val imageStorageManager =  ImageStorageManager()
    val uiState by photoSyncViewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()

    val (pictureUri, takePicture, reset) = rememberCameraLauncher()

    var cameraInterrupted by remember { mutableStateOf(false) }

    BackHandler(enabled = uiState is ScreenState.CameraActive && pictureUri != null) {
        Toast.makeText(ctx, "Cannot interrupt a challenge before it's completed!", Toast.LENGTH_SHORT).show()
    }

    when(val state = uiState) {
        is ScreenState.Idle -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { photoSyncViewModel.startNewChallenge() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF000000),
                        contentColor = Color.White
                    )
                ) {
                    Text("New SnapHunt!")
                }
            }
        }

        is ScreenState.ChallengeProposed -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("New Mission: Find a... ${state.challenge.keyword} !!")
                Spacer(modifier = Modifier.height(14.dp))
                Row {
                    Button(
                        onClick = { photoSyncViewModel.rejectChallenge(state.challenge) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF000000),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Refuse..")
                    }
                    Button(
                        onClick = { photoSyncViewModel.acceptChallenge(state.challenge) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF000000),
                        contentColor = Color.White
                        )
                    ) {
                        Text("Accept!")
                    }
                }
            }
        }

        is ScreenState.CameraActive -> {
            LaunchedEffect(pictureUri) {
                delay(800)
                if (pictureUri == null && !cameraInterrupted) {
                    cameraInterrupted = true
                }
            }
            if (cameraInterrupted) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("PhotoCamera Closed. Click here to continue the SnapHunt!")
                    Button(onClick = {
                        cameraInterrupted = false
                        takePicture()
                    }) {
                        Text("New SnapHunt")
                    }
                }
            }
            else if(pictureUri != null) {
                AnalysisScreen(
                    viewModel = objectDetectionViewModel,
                    photoSyncViewModel =  photoSyncViewModel,
                    authViewModel = authViewModel,
                    pictureUri,
                    state.challenge,
                    onAnalysisFinished = { results ->
                        scope.launch(Dispatchers.IO) {
                            val originalBitmap = uriToBitmap(pictureUri, ctx.contentResolver)
                            val thumbnailBitmap = imageStorageManager.createThumbnail(originalBitmap, maxSize = 512)
                            val thumbnailFile = imageStorageManager.saveLocalImage(thumbnailBitmap, ctx)
                            val attempt = PendingAttempt(
                                id = UUID.randomUUID().toString(),
                                challengeId = UUID.randomUUID().toString(),
                                challengeText = "Find a... ${state.challenge.keyword}",
                                success = results.success,
                                skipped = false,
                                aiLabel = results.aiLabel,
                                aiConfidence = results.aiConfidence,
                                localThumbnailPath = thumbnailFile.absolutePath,
                                createdAt = System.currentTimeMillis(),
                                points = results.points,
                                additionalObjects = results.additionalObjects
                            )
                            photoSyncViewModel.onPhotoTaken(attempt)
                            withContext(Dispatchers.Main) {
                                val message = if (results.success) "Challenge Completed! Points:: ${results.points}" else "Object not found."
                                Toast.makeText(ctx, message, Toast.LENGTH_LONG).show()
                            }
                        }
                    })
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        reset()
                        photoSyncViewModel.resetToIdle()
                    }) {
                        Text("Do not Save Picture")
                    }
                    Button(onClick = {
                        scope.launch(Dispatchers.IO) {
                            val originalBitmap = uriToBitmap(pictureUri, ctx.contentResolver)
                            val success = imageStorageManager.saveImageToGallery(originalBitmap, ctx)
                            withContext(Dispatchers.Main) {
                                if (success) {
                                    Toast.makeText(ctx, "Immagine salvata in Galleria!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(ctx, "Errore durante il salvataggio", Toast.LENGTH_SHORT).show()
                                }
                                reset()
                                photoSyncViewModel.resetToIdle()
                            }
                        }

                    }) {
                        Text("Save Picture")
                    }
                }
            } else {
                LaunchedEffect(Unit) {
                    if (!cameraInterrupted) {
                        takePicture()
                    }
                }
            }

        }
    }

}
