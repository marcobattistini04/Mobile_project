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
import java.io.File

@Composable
fun rememberCameraLauncher(): Triple<Uri?, () -> Unit, () -> Unit> {
    var launcherUri by remember { mutableStateOf<Uri?>(null) }
    var pictureUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture())  { pictureTaken ->
        if(pictureTaken) launcherUri?.let{
            pictureUri = it
        }
    }

    val ctx = LocalContext.current
    val takePicture = {
        val file = File.createTempFile("tmp_img", ".jpg", ctx.externalCacheDir)
        launcherUri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", file)
        launcher.launch(launcherUri!!)
    }

    val reset = { pictureUri = null }

    return Triple(pictureUri, takePicture, reset)
}