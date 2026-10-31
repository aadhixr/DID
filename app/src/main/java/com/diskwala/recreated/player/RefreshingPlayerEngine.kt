package com.diskwala.recreated.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class EngineState {
    object Idle : EngineState()
    object Buffering : EngineState()
    object Ready : EngineState()
    object RefreshingToken : EngineState()
    data class Error(val message: String) : EngineState()
}

interface StreamResolver {
    suspend fun resolveStreamUrl(mediaId: String): Result<String>
}

class RefreshingPlayerEngine(
    private val context: Context,
    private val resolver: StreamResolver,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) {
    private var exoPlayer: ExoPlayer? = null
    private var currentMediaId: String? = null
    private var resumePositionMs: Long = 0L

    private val _engineState = MutableStateFlow<EngineState>(EngineState.Idle)
    val engineState: StateFlow<EngineState> = _engineState

    fun attachPlayer(): ExoPlayer {
        val player = ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> _engineState.value = EngineState.Buffering
                        Player.STATE_READY -> _engineState.value = EngineState.Ready
                        Player.STATE_ENDED, Player.STATE_IDLE -> _engineState.value = EngineState.Idle
                    }
                }

                @OptIn(UnstableApi::class)
                override fun onPlayerError(error: PlaybackException) {
                    val cause = error.cause
                    if (cause is HttpDataSource.InvalidResponseCodeException &&
                        (cause.responseCode == 401 || cause.responseCode == 403)
                    ) {
                        // Expired media token or forbidden stream link -> Auto-refresh URL
                        recoverStream()
                    } else {
                        _engineState.value = EngineState.Error(error.localizedMessage ?: "Playback Error")
                    }
                }
            })
        }
        exoPlayer = player
        return player
    }

    fun play(mediaId: String) {
        currentMediaId = mediaId
        resumePositionMs = 0L
        loadAndStart(mediaId, 0L)
    }

    private fun loadAndStart(mediaId: String, startMs: Long) {
        scope.launch {
            _engineState.value = EngineState.Buffering
            resolver.resolveStreamUrl(mediaId).onSuccess { streamUrl ->
                exoPlayer?.let { player ->
                    player.setMediaItem(MediaItem.fromUri(streamUrl))
                    if (startMs > 0) {
                        player.seekTo(startMs)
                    }
                    player.prepare()
                    player.play()
                }
            }.onFailure { err ->
                _engineState.value = EngineState.Error(err.message ?: "Failed to resolve stream link")
            }
        }
    }

    private fun recoverStream() {
        val mediaId = currentMediaId ?: return
        resumePositionMs = exoPlayer?.currentPosition ?: 0L
        _engineState.value = EngineState.RefreshingToken
        loadAndStart(mediaId, resumePositionMs)
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }
}
