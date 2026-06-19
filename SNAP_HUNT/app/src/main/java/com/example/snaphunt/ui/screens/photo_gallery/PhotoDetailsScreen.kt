package com.example.snaphunt.ui.screens.photo_gallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.snaphunt.data.user.UserChallengeItem
import com.example.snaphunt.photos.PhotoGalleryViewModel
import com.example.snaphunt.ui.components.AppBar
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun PhotoDetailsScreen(
    navigationController: NavHostController,
    photoGalleryViewModel: PhotoGalleryViewModel,
    challengeId: String
) {
    val challenge by photoGalleryViewModel.selectedChallenge.collectAsState()

    LaunchedEffect(challengeId) {
        photoGalleryViewModel.loadChallengeById(challengeId)
    }

    if (challenge == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val item = challenge!!

        val formatterInput = DateTimeFormatter.ISO_DATE_TIME
        val formatterOutput = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedDate = remember(item.createdAt) {
            try {
                ZonedDateTime.parse(item.createdAt, formatterInput).format(formatterOutput)

            } catch (e: Exception) {}
        }
        Scaffold(
            topBar = { AppBar(title = "Challenge details", navigationController) },
            floatingActionButton = {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    onClick = {/*TO DO */}
                ) {
                    Icon(Icons.Outlined.Share, "Share Challenge")
                }
            }
        ) { contentPadding ->
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(contentPadding).padding(16.dp).fillMaxSize()
            ) {
                if (!item.storagePath.isNullOrBlank() && item.storagePath.startsWith("http")) {
                    AsyncImage(
                        model = item.storagePath,
                        contentDescription = "Challenge Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No photo available", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Text(
                    text = item.challengeText,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Created at: $formattedDate",
                    style = MaterialTheme.typography.bodySmall
                )
                Text (
                    text = "Ai label: ${item.aiLabel ?: "Not analized"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "AI Confidence: ${item.aiConfidence?.let { (it * 100).toInt().toString() + "%" } ?: "Not Analized"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "State: ${if (item.success) "Success" else "Failed"}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (item.success) Color.Green else Color.Red
                )
                Text(
                    text = "Points Earned: ${(item.points)}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Additional Objects Found: ${(item.additionalObjects)}",
                    style = MaterialTheme.typography.bodyMedium,
                )

            }
        }
    }

}