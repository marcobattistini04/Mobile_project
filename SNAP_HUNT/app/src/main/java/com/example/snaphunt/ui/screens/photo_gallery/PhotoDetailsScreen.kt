package com.example.snaphunt.ui.screens.photo_gallery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.snaphunt.R
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
            try { ZonedDateTime.parse(item.createdAt, formatterInput).format(formatterOutput) } catch (e: Exception) { "" }
        }

        Scaffold(
            topBar = { AppBar(isNavigationEnabled = true, title = "Challenges Details", navigationController) }
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (!item.storagePath.isNullOrBlank() && item.storagePath.startsWith("http")) {
                    AsyncImage(
                        model = item.storagePath,
                        contentDescription = "Challenge Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = item.challengeText,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Created at $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoCard(label = "Points earned", value = item.points.toString(), modifier = Modifier.weight(1f))
                    InfoCard(label = "Additional Object", value = item.additionalObjects.toString(), modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(id = R.drawable.icon_label), contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Ai label", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.weight(1f))
                        Text(item.aiLabel ?: "Not analyzed", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(id = R.drawable.icon_conf), contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Ai Confidence", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.weight(1f))
                        Text("${(item.aiConfidence?.times(100)?.toInt() ?: 0)}%", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(1.dp))

                    LinearProgressIndicator(
                        progress = { item.aiConfidence?.toFloat() ?: 0f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF009649)
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "State : ${if (item.success) "Success" else "Failed"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (item.success) Color(0xFF009649) else Color.Red
                )
            }
        }
    }
}