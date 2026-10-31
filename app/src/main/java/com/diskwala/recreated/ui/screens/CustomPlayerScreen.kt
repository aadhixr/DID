package com.diskwala.recreated.ui.screens

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.diskwala.recreated.player.EngineState
import com.diskwala.recreated.player.RefreshingPlayerEngine
import com.diskwala.recreated.player.StreamResolver

@OptIn(UnstableApi::class)
@Composable
fun CustomPlayerScreen(
    mediaId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // In a production setup, inject this via Hilt / ViewModel
    val mockResolver = remember {
        object : StreamResolver {
            override suspend fun resolveStreamUrl(id: String): Result<String> {
                // Return playback stream URL or dynamically resolve from API
                return Result.success("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
            }
        }
    }

    val engine = remember { RefreshingPlayerEngine(context, mockResolver) }
    val exoPlayer = remember { engine.attachPlayer() }
    val engineState by engine.engineState.collectAsState()

    DisposableEffect(mediaId) {
        engine.play(mediaId)
        onDispose {
            engine.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    useController = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay status during buffering or token recovery
        when (engineState) {
            is EngineState.Buffering -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }
            is EngineState.RefreshingToken -> {
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = "Refreshing stream link...",
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            is EngineState.Error -> {
                Surface(
                    color = Color.Black.copy(alpha = 0.8f),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = (engineState as EngineState.Error).message,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> Unit
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text(text = "← Back", color = Color.White)
        }
    }
}
