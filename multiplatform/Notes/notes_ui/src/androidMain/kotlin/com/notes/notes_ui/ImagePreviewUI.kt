package com.notes.notes_ui

import android.net.Uri
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    uri: Uri,
    title: String,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = title)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
            )
        },
    ) { padding ->
        ZoomableImage(
            uri = uri,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}

@Composable
private fun ZoomableImage(
    uri: Uri,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val minScale = 1f
    val maxScale = 5f

    val configuration = LocalWindowInfo.current
    val density = LocalDensity.current

    val screenWidthPx = with(density) {
        configuration.containerSize.width.dp.toPx()
    }

    val screenHeightPx = with(density) {
        configuration.containerSize.height.dp.toPx()
    }

    Box(
        modifier = modifier
            .safeDrawingPadding()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->

                    val newScale = (scale * zoom).coerceIn(minScale, maxScale)

                    val extraWidth = (newScale - 1) * screenWidthPx
                    val extraHeight = (newScale - 1) * screenHeightPx

                    val maxX = extraWidth / 2
                    val maxY = extraHeight / 2

                    scale = newScale

                    offset = Offset(
                        x = (offset.x + pan.x).coerceIn(-maxX, maxX),
                        y = (offset.y + pan.y).coerceIn(-maxY, maxY)
                    )

                    // Reset position when zoomed out
                    if (scale <= 1f) {
                        offset = Offset.Zero
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {

        AsyncImage(
            model = uri,
            contentDescription = "",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale

                    translationX = offset.x
                    translationY = offset.y
                },
            contentScale = ContentScale.Fit
        )

    }
}