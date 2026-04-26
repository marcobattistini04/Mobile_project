package com.example.snaphunt.ui.components

import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView


@Composable
@androidx.media3.common.util.UnstableApi // this is still considered unstable: this.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
fun VideoPlayer(uri: Uri) {
    val context = LocalContext.current

    val exoPlayer = remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
            volume = 0f
            prepare()
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                this.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        }
    )
}