// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.middleware

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.azure.android.communication.ui.calling.redux.Dispatch
import com.azure.android.communication.ui.calling.redux.Middleware
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.ReduxState


/// Audio Focus Middleware
/// Handles AudioFocus by requesting/releasing before the appropriate actions
/// Rejects actions such as Start/Resume when Focus can't be retrieved
internal interface AudioFocusMiddleware

// Handle Audio Focus for different platforms
// Provides ability to listen to the changes
internal abstract class AudioFocusHandler : AudioManager.OnAudioFocusChangeListener {
    companion object {
        fun getForPlatform(context: Context) : AudioFocusHandler {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AudioFocusHandler26(context)
            } else {
                AudioFocusHandlerLegacy(context)
            }
        }
    }

    var onFocusChange: ((Int) -> Unit)? = null
        set(handler) {
            field = handler
        }

    override fun onAudioFocusChange(focusChange: Int) {
        onFocusChange?.let { it(focusChange) }
    }

    abstract fun getAudioFocus() : Boolean
    abstract fun releaseAudioFocus() : Boolean
}

// Newer API Version of AudioFocusHandler
@RequiresApi(Build.VERSION_CODES.O)
internal class AudioFocusHandler26(val context: Context) : AudioFocusHandler() {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val audioFocusRequest26 = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
        .setOnAudioFocusChangeListener(this).build()

    override fun getAudioFocus() =
        audioManager.requestAudioFocus(audioFocusRequest26) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED

    override fun releaseAudioFocus() =
        audioManager.abandonAudioFocusRequest(audioFocusRequest26) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
}


// Legacy AudioFocus API
@Suppress("DEPRECATION")
internal class AudioFocusHandlerLegacy(val context: Context) : AudioFocusHandler() {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun getAudioFocus() = audioManager.requestAudioFocus(this,
        AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)  == AudioManager.AUDIOFOCUS_REQUEST_GRANTED

    override fun releaseAudioFocus(): Boolean
        = audioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
}


// Track Manage AudioFocus with Middleware
internal class AudioFocusMiddlewareImpl(
    private val audioFocusHandler : AudioFocusHandler,
    private val onFocusFailed: () -> Unit,
    private val onFocusLost: () -> Unit) :
    Middleware<ReduxState>,
    AudioFocusMiddleware {

    private var currentFocus = AudioManager.AUDIOFOCUS_NONE

    init {
        audioFocusHandler.onFocusChange = {
            this.currentFocus = it
            // Todo: AudioFocus can be resumed as well (e.g. transient is temporary, we will get back.
            // I.e. like how spotify can continue playing after a call is done.
            if (it == AudioManager.AUDIOFOCUS_LOSS
                || it == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                || it == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                onFocusLost()
            }
        }
    }

    override fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
        { action: Action ->
            if (action is CallingAction.CallStartRequested || action is CallingAction.ResumeRequested) {
                var result = audioFocusHandler.getAudioFocus()

                if (result) {
                    // Could fetch AudioFocus
                    next(action)
                } else {
                    // Error fetching AudioFocus
                    onFocusFailed()
                }
            } else {
                next(action)
            }
        }
    }

}
