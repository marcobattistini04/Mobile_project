package com.example.snaphunt.ui.screens.home.image_recognition

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.snaphunt.image_recognition.DailyObjects
import com.example.snaphunt.image_recognition.ObjectDetectionViewModel
import com.example.snaphunt.photos.PhotoSyncViewModel
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.utils.uriToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AnalysisScreen(
    viewModel: ObjectDetectionViewModel,
    photoSyncViewModel: PhotoSyncViewModel,
    authViewModel: AuthViewModel,
    pictureUri: Uri,
    challenge: DailyObjects
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val userState by authViewModel.state.collectAsStateWithLifecycle()
    val results by viewModel.detectionResults.collectAsStateWithLifecycle()
    val rawResults by viewModel.rawDetectionResult.collectAsStateWithLifecycle()
    val loading by photoSyncViewModel.isProcessing.collectAsStateWithLifecycle()
    val isSaveEnabled by photoSyncViewModel.savingButtonEnabled.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
        ) {
            AsyncImage(
                model = pictureUri,
                contentDescription = "Captured image",
                modifier = Modifier.fillMaxWidth()
            )

            rawResults?.let {
                BoxOverlay(results = it, modifier = Modifier.fillMaxSize())
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    val bitmap = uriToBitmap(pictureUri, context.contentResolver)
                    viewModel.processImage(bitmap, challenge)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Analyze Image")
        }

        rawResults?.let {
            Text("Objects found: ${it.detections().size}")
            it.detections().forEach { detection ->
                Text("Found: ${detection.categories().first().categoryName()}")
            }
        }

        results?.let { summary ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (summary.success) "Mission Completed!" else "Object not found",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (summary.success) Color.Green else Color.Red
                    )
                    Text("Total points: ${summary.points}")
                    Text("Additional objects found: ${summary.additionalObjects}")
                    Text("Model Confidence: ${(summary.aiConfidence * 100).toInt()}%")

                    if (userState.user != null) {
                        Button(
                            onClick = {
                                scope.launch(Dispatchers.IO) {
                                    val bitmap =
                                        uriToBitmap(pictureUri, context.contentResolver)
                                    photoSyncViewModel.processAndSave(
                                        bitmap,
                                        summary,
                                        challenge
                                    )
                                }
                            },
                            enabled = isSaveEnabled && !loading,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            if (loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White
                                )
                            } else {
                                Text("Save and Complete Challenge")
                            }
                        }
                    } else {
                        Text("Login to save your progress", color = Color.Gray)
                    }
                }
            }
        }
    }
}