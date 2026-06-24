package com.example.snaphunt.ui.screens.photo_gallery

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.snaphunt.R
import com.example.snaphunt.presentation.sign_in.AuthViewModel
import com.example.snaphunt.ui.components.AppBar
import com.example.snaphunt.SnapHuntRoute
import com.example.snaphunt.data.user.UserChallengeItem
import com.example.snaphunt.photos.PhotoGalleryViewModel
import com.example.snaphunt.ui.components.FilterBar
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@Composable
fun PhotoGalleryScreen(authViewModel: AuthViewModel, galleryViewModel: PhotoGalleryViewModel, navigationController: NavHostController, ) {
    val state by authViewModel.state.collectAsState()
    val user = state.user
    val photos by galleryViewModel.filteredPhotos.collectAsState()
    val currentFilter by galleryViewModel.currentFilter.collectAsState()
    val isSortByPoints by galleryViewModel.isSortByPoints.collectAsState()
    val isOnline by galleryViewModel.isOnline.collectAsState()
    val ctx = LocalContext.current

    LaunchedEffect(user?.userId, isOnline) {
        if(isOnline) {
            user?.userId?.let { galleryViewModel.loadUserChallenges(it)}
        }
        if(photos.isEmpty() && isOnline) {
            Toast.makeText(
                ctx,
                "no Photos to show. Take new Snaphunts to view them here!",
                Toast.LENGTH_LONG
            ).show()
        }
        if (!isOnline) {
            Toast.makeText(
                ctx,
                "Unable to load Photo Gallery",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        topBar = { AppBar(isNavigationEnabled = true, title = "Challenges Collection", navigationController) }
    ) {
            contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    FilterBar(
                        currentFilter = currentFilter,
                        onFilterSelected = { galleryViewModel.updateFilter(it)}
                    )
                }

                IconButton(onClick = {galleryViewModel.toggleSort()}) {
                    Icon(
                        imageVector = when {
                            isSortByPoints -> Icons.Default.Star
                            else -> Icons.Default.DateRange
                        },
                        contentDescription = "Change sort logic by descending points or date",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp),
                modifier = Modifier.weight(1f)

            ) {
                items(photos) {photo -> PhotoGalleryItem(
                    photo,
                    navigationController,
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = 300),
                        fadeOutSpec = tween(durationMillis = 300),
                        placementSpec = spring(stiffness = Spring.StiffnessLow) // Fluid animation Effect
                    )
                        .size(150.dp)
                )}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGalleryItem(item: UserChallengeItem, navigationController: NavHostController, modifier: Modifier) {
    val formatterInput = DateTimeFormatter.ISO_DATE_TIME
    val formatterOutput = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val formattedDate = remember(item.createdAt) {
        try {
            ZonedDateTime.parse(item.createdAt, formatterInput).format(formatterOutput)

        } catch (e: Exception) {}
    }

    Card(
        onClick = { navigationController.navigate(SnapHuntRoute.PhotoDetails(item.id)) },
        modifier = modifier
            .size(150.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =  MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Log.d("ImageDebug", "URL: ${item.storagePath}")
            AsyncImage(
                model = item.storagePath,
                contentDescription = "Challenge Photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_placeholder),
                error = painterResource(id = R.drawable.ic_error)
            )


            Text(
                text = formattedDate.toString(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(4.dp),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
