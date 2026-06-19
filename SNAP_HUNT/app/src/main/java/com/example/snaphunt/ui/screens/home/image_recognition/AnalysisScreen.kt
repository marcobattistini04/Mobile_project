package com.example.snaphunt.ui.screens.home.image_recognition

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import com.example.snaphunt.image_recognition.DailyObjects
import com.example.snaphunt.image_recognition.DetectionResults
import com.example.snaphunt.image_recognition.ObjectDetectionViewModel
import com.example.snaphunt.photos.PhotoSyncViewModel
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.utils.uriToBitmap
import io.github.jan.supabase.auth.Auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnalysisScreen(
    pictureUri: Uri,
    challenge: DailyObjects,
    onAnalysisFinished: (DetectionResults) -> Unit,
    context: Context = LocalContext.current
) {
    val viewModel: ObjectDetectionViewModel = koinViewModel <ObjectDetectionViewModel>()
    val photoSyncViewModel: PhotoSyncViewModel = koinViewModel<PhotoSyncViewModel>()
    val authViewModel: AuthViewModel = koinViewModel<AuthViewModel>()
    val userState = authViewModel.userId
    val results by viewModel.detectionResults.collectAsState()
    val rawResults by viewModel.rawDetectionResult.collectAsState()
    val loading = photoSyncViewModel.isProcessing.collectAsState()
    val savingButtonEnabled = photoSyncViewModel.savingButtonEnabled.collectAsState()

    var bitmap by remember(pictureUri) { mutableStateOf<Bitmap?>(null) }



    LaunchedEffect(pictureUri) {
        viewModel.clearResults()
        withContext(Dispatchers.IO) {
            bitmap = uriToBitmap(pictureUri, context.contentResolver)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
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

            bitmap?.let {bitmap ->
                rawResults?.let {
                    BoxOverlay(
                        results = it,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        bitmap?.let {bitmap ->
            Button(onClick = {
                viewModel.processImage(bitmap, challenge)
            }) {
                Text("Analize Image")
            }
        }


        rawResults?.let {
            Text("Objects found: ${it.detections().size}")
            it.detections().forEach { detection ->
                Text("Found: ${detection.categories().first().categoryName()}")
            }
        }

        results?.let { summary ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (summary.success) "Mission Completed!" else "Object  not found",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (summary.success) Color.Green else Color.Red
                    )
                    Text("Total points: ${summary.points}")
                    Text("Additional objects found: ${summary.additionalObjects}")
                    Text("Model Confidence: ${(summary.aiConfidence * 100).toInt()}%")

                    //EVENTUALLY IF THE CHALLENGE IS LOST THE UPLOAD COULD BE AUTOMATIC AND NOT LINKED TO A BUTTON
                    // IN ORDER TO AVOID SMART USERS :)

                    if(userState != null) {
                        Button(
                            onClick = {
                                onAnalysisFinished(summary)
                            },
                            enabled = savingButtonEnabled.value,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            if (loading.value) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Save and Complete Challenge")
                            }
                        }
                    }
                }
            }
        }
    }
}