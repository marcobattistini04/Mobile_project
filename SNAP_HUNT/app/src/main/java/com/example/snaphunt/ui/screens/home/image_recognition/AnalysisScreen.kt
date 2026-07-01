package com.example.snaphunt.ui.screens.home.image_recognition

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.snaphunt.R
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
    val isAnalyzeEnabled by photoSyncViewModel.isAnalysisPerformed.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    val backgroundGreen = Color(0xFFE2F3E7)
    val backgroundRed = Color(0xFFFFEBEF)

    val mainButtonBgColor = MaterialTheme.colorScheme.inverseSurface
    val mainButtonTextColor = MaterialTheme.colorScheme.inverseOnSurface
    val outlineTextColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(12.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(12.dp))
        ) {
            AsyncImage(
                model = pictureUri,
                contentDescription = "Captured image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            rawResults?.let {
                BoxOverlay(results = it, modifier = Modifier.matchParentSize())
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
            enabled = isAnalyzeEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = mainButtonBgColor,
                contentColor = mainButtonTextColor
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_analize),
                contentDescription = "Analize Icon",
                modifier = Modifier.size(20.dp),
                tint = mainButtonTextColor
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text("Analize Image", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        results?.let { summary ->
            photoSyncViewModel.onAnalysisTerminated()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (summary.success) backgroundGreen else backgroundRed
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (summary.success) "Results - Scan Complete!" else "Results - Scan Error!",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (summary.success) Color(0xFF2E7D32) else Color.Red,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ResultStatItem(
                            iconId = R.drawable.ic_object_found,
                            label = "Object found",
                            value = "${if (summary.success) 1 else 0}",
                            modifier = Modifier.weight(1f),
                            textOffset = 7.dp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        ResultStatItem(
                            iconId = R.drawable.ic_points,
                            label = "Total Points",
                            value = "${summary.points}",
                            modifier = Modifier.weight(1f),
                            textOffset = 7.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ResultStatItem(
                            iconId = R.drawable.ic_additional_objects,
                            label = "Additional Object",
                            value = "${summary.additionalObjects}",
                            modifier = Modifier.weight(1f),
                            textOffset = 0.dp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        ResultStatItem(
                            iconId = R.drawable.ic_confidence,
                            label = "Model confidence",
                            value = "${(summary.aiConfidence * 100).toInt()}%",
                            modifier = Modifier.weight(1f),
                            textOffset = 0.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (userState.user != null) {
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            val bitmap = uriToBitmap(pictureUri, context.contentResolver)
                            photoSyncViewModel.processAndSave(bitmap, summary, challenge)
                        }
                    },
                    enabled = isSaveEnabled && !loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = mainButtonBgColor,
                        contentColor = mainButtonTextColor
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = mainButtonTextColor
                        )
                    } else {
                        Text("Save and Complete Challenge", fontWeight = FontWeight.Bold)
                    }
                }



            } else {
                Text("Login to save your progress", color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}