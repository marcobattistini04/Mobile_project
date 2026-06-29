package com.example.snaphunt.utils

import androidx.compose.runtime.Composable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.example.snaphunt.photos.PhotoSyncViewModel
import java.io.File

@Composable
fun rememberCameraLauncher(onPhotoTaken: (Uri) -> Unit, photoSyncViewModel: PhotoSyncViewModel): Triple<Uri?, () -> Unit, () -> Unit> {
    var launcherUri by remember { mutableStateOf<Uri?>(null) }
    var pictureUri by remember { mutableStateOf<Uri?>(null) }
    val ctx = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && launcherUri != null) {
            pictureUri = launcherUri
            onPhotoTaken(launcherUri!!)
        } else {
            photoSyncViewModel.onCameraCancelled()
        }
    }

    val takePicture = {
        val file = File.createTempFile("snaphunt_tmp", ".jpg", ctx.externalCacheDir)
        val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", file)
        launcherUri = uri
        launcher.launch(uri)
    }

    val reset = { pictureUri = null }

    return Triple(pictureUri, takePicture, reset)
}
