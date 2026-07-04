package com.example.snaphunt.ui.screens.home

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    val mainButtonBgColor = MaterialTheme.colorScheme.inverseSurface
    val mainButtonTextColor = MaterialTheme.colorScheme.inverseOnSurface

    val (pictureUri, takePicture, reset) = rememberCameraLauncher(
        onPhotoTaken = { uri ->
            if (uiState is ScreenState.CameraActive) {
                val challenge = (uiState as ScreenState.CameraActive).challenge
                photoSyncViewModel.onPhotoCaptured(uri, challenge)
            }
        },
        photoSyncViewModel
    )

    LaunchedEffect(Unit) {
        photoSyncViewModel.uiEvent.collect { message ->
            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
        }
    }

    BackHandler(enabled = uiState is ScreenState.PhotoPreview && pictureUri != null) {
        Toast.makeText(ctx, "Cannot interrupt a challenge before it's completed!", Toast.LENGTH_SHORT).show()
    }

    when (val state = uiState) {
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
                        containerColor = mainButtonBgColor,
                        contentColor = mainButtonTextColor
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
                            containerColor = mainButtonBgColor,
                            contentColor = mainButtonTextColor
                        )
                    ) {
                        Text("Refuse..")
                    }

                    Spacer(modifier = Modifier.size(16.dp))

                    Button(
                        onClick = { photoSyncViewModel.acceptChallenge(state.challenge) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = mainButtonBgColor,
                            contentColor = mainButtonTextColor
                        )
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = {
                            reset()
                            photoSyncViewModel.resetToIdle()
                            objectDetectionViewModel.clearResults()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline
                        )
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
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            Text("Save Picture")
                        }
                    }
                }
            }
        }
    }
}