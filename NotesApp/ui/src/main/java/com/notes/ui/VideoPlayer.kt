package com.notes.ui

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoLink: String?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var player by remember { mutableStateOf<Player?>(null) }
    LifecycleStartEffect(videoLink) {
        if (videoLink != null) {
            player = ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(videoLink))
                repeatMode = Player.REPEAT_MODE_ALL
                prepare()
                play() // Play immediately
            }
        }
        onStopOrDispose {
            player?.release()
            player = null
        }
    }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .then(modifier),
    ) {
        // Render the video
        PlayerSurface(player, surfaceType = SURFACE_TYPE_TEXTURE_VIEW)
    }
}