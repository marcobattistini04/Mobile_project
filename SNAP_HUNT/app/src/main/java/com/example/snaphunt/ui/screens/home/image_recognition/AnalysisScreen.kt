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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import com.example.snaphunt.image_recognition.ObjectDetectionViewModel
import com.example.snaphunt.utils.uriToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnalysisScreen(
    pictureUri: Uri,
    context: Context = LocalContext.current
) {
    val viewModel: ObjectDetectionViewModel = koinViewModel <ObjectDetectionViewModel>()
    val results by viewModel.detectionResults.collectAsState()

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
                results?.let {
                    BoxOverlay(
                        results = it,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        bitmap?.let {bitmap ->
            Button(onClick = {
                viewModel.processImage(bitmap)
            }) {
                Text("Analize Image")
            }
        }


        results?.let {
            Text("Objects found: ${it.detections().size}")
            it.detections().forEach { detection ->
                Text("Found: ${detection.categories().first().categoryName()}")
            }
        }
    }
}