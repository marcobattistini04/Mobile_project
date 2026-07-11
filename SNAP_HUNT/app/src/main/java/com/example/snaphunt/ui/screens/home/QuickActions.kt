package com.example.snaphunt.ui.screens.home

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.snaphunt.image_recognition.ObjectDetectionViewModel
import com.example.snaphunt.photos.PhotoSyncViewModel
import com.example.snaphunt.photos.ScreenState
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.ui.screens.home.image_recognition.AnalysisScreen
import com.example.snaphunt.user_settings.SettingsActions
import com.example.snaphunt.user_settings.SettingsState
import com.example.snaphunt.utils.rememberCameraLauncher
import com.example.snaphunt.utils.uriToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun QuickActions(
    objectDetectionViewModel: ObjectDetectionViewModel,
    photoSyncViewModel: PhotoSyncViewModel,
    authViewModel: AuthViewModel,
    themeState: SettingsState,
    themeActions: SettingsActions
) {
    val ctx = LocalContext.current
    val uiState by photoSyncViewModel.uiState.collectAsStateWithLifecycle()
    val loading by photoSyncViewModel.isProcessing.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val (pictureUri, takePicture, reset) = rememberCameraLauncher(
        onPhotoTaken = { uri ->
            if (uiState is ScreenState.CameraActive) {
                val challenge = (uiState as ScreenState.CameraActive).challenge
                photoSyncViewModel.onPhotoCaptured(uri, challenge)
            }
        },
        photoSyncViewModel
    )

    // Logica per gestire gli eventi di errore/messaggi
    LaunchedEffect(Unit) {
        photoSyncViewModel.uiEvent.collect { message ->
            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
        }
    }

    BackHandler(enabled = uiState is ScreenState.PhotoPreview && pictureUri != null) {
        Toast.makeText(ctx, "Cannot interrupt a challenge before it's completed!", Toast.LENGTH_SHORT).show()
    }

    // Qui gestiamo i vari stati dell'interfaccia
    when (val state = uiState) {
        is ScreenState.Idle -> {
            // Nota: Rimosso .fillMaxSize() per evitare conflitti di layout
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { photoSyncViewModel.startNewChallenge() },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color.White else Color.Black,
                        contentColor = if (isDark) Color.Black else Color.White
                    )
                ) {
                    Text("New Snaphunt!")
                }
            }
        }

        is ScreenState.ChallengeProposed -> {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("New Mission: Find a... ${state.challenge.keyword} !!")
                Spacer(modifier = Modifier.height(14.dp))
                Row {
                    Button(
                        onClick = { photoSyncViewModel.rejectChallenge(state.challenge) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inverseSurface)
                    ) {
                        Text("Refuse..")
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(
                        onClick = { photoSyncViewModel.acceptChallenge(state.challenge) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inverseSurface)
                    ) {
                        Text("Accept!")
                    }
                }
            }
        }

        is ScreenState.CameraActive -> {
            LaunchedEffect(Unit) { takePicture() }
        }

        is ScreenState.Analyzing -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ScreenState.PhotoPreview -> {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    AnalysisScreen(
                        objectDetectionViewModel = objectDetectionViewModel,
                        photoSyncViewModel = photoSyncViewModel,
                        authViewModel = authViewModel,
                        pictureUri = state.uri,
                        challenge = state.challenge,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = {
                            reset()
                            photoSyncViewModel.resetToIdle()
                            objectDetectionViewModel.clearResults()
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text("Don't Save Picture")
                    }

                    OutlinedButton(
                        onClick = {
                            scope.launch(Dispatchers.Default) {
                                val originalBitmap = uriToBitmap(state.uri, ctx.contentResolver)
                                photoSyncViewModel.saveToGallery(originalBitmap)
                                objectDetectionViewModel.clearResults()
                            }
                        },
                        enabled = !loading,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        if (loading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        } else {
                            Text("Save Picture")
                        }
                    }
                }
            }
        }
    }
}