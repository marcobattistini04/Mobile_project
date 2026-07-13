package com.example.snaphunt.utils

import androidx.compose.runtime.Composable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.example.snaphunt.photos.PhotoSyncViewModel
import java.io.File
import androidx.core.net.toUri

@Composable
fun rememberCameraLauncher(
    onPhotoTaken: (Uri) -> Unit,
    photoSyncViewModel: PhotoSyncViewModel
): Triple<Uri?, () -> Unit, () -> Unit> {
    val ctx = LocalContext.current

    var launcherUriStr by rememberSaveable { mutableStateOf<String?>(null) }
    var pictureUriStr by rememberSaveable { mutableStateOf<String?>(null) }

    val pictureUri = remember(pictureUriStr) { pictureUriStr?.toUri() }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val currentUri = launcherUriStr?.toUri()

        if (success && currentUri != null) {
            pictureUriStr = currentUri.toString()
            onPhotoTaken(currentUri)
        } else {
            currentUri?.let { uri ->
                try {
                    ctx.contentResolver.delete(uri, null, null)
                } catch (e: Exception) {
                    try {
                        val file = File(ctx.externalCacheDir, uri.lastPathSegment ?: "")
                        if (file.exists()) file.delete()
                    } catch (i: Exception) {}
                }
            }
            photoSyncViewModel.onCameraCancelled()
        }
    }

    val takePicture = {
        try {
            val file = File.createTempFile("snaphunt_tmp", ".jpg", ctx.externalCacheDir)
            val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", file)

            launcherUriStr = uri.toString()
            launcher.launch(uri)
        } catch (e: Exception) { }
    }

    val reset = { pictureUriStr = null }

    return Triple(pictureUri, takePicture, reset)
}
