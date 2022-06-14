// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.MODE_NORMAL
import android.os.Build
import androidx.annotation.RequiresApi
import com.azure.android.communication.ui.calling.redux.action.AudioSessionAction
import com.azure.android.communication.ui.calling.redux.state.AudioFocusStatus
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.flow.collect
import org.reduxkotlin.Store

internal abstract class AudioFocusHandler : AudioManager.OnAudioFocusChangeListener {
    var onFocusChange: ((Int) -> Unit)? = null

    override fun onAudioFocusChange(focusChange: Int) {
        onFocusChange?.let { it(focusChange) }
    }

    abstract fun getAudioFocus(): Boolean
    abstract fun releaseAudioFocus(): Boolean
    abstract fun getMode(): Int
}

// Newer API Version of AudioFocusHandler
@RequiresApi(Build.VERSION_CODES.O)
internal class AudioFocusHandler26(val context: Context) : AudioFocusHandler() {
    private fun audioManager() = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val audioFocusRequest26 = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
        .setOnAudioFocusChangeListener(this).build()

    override fun getAudioFocus() =
        audioManager().requestAudioFocus(audioFocusRequest26) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED

    override fun getMode() = audioManager().mode

    override fun releaseAudioFocus() =
        audioManager().abandonAudioFocusRequest(audioFocusRequest26) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
}

// Legacy AudioFocus API
@Suppress("DEPRECATION")
internal class AudioFocusHandlerLegacy(val context: Context) : AudioFocusHandler() {
    private fun audioManager() = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun getAudioFocus() = audioManager().requestAudioFocus(
        this,
        AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
    ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED

    override fun getMode() = audioManager().mode

    override fun releaseAudioFocus(): Boolean =
        audioManager().abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
}

internal class AudioFocusManager(
    private val store: Store<ReduxState>,
    applicationContext: Context,
) {
    private var audioFocusHandler: AudioFocusHandler? = null
    private var isAudioFocused = false
    private var previousCallState: CallingStatus? = null
    private var previousAudioFocusStatus: AudioFocusStatus? = null

    init {
        audioFocusHandler = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusHandler26(applicationContext)
        } else {
            AudioFocusHandlerLegacy(applicationContext)
        }
    }

    suspend fun start() {
        audioFocusHandler?.onFocusChange = {
            // Todo: AudioFocus can be resumed as well (e.g. transient is temporary, we will get back.
            // I.e. like how spotify can continue playing after a call is done.
            if (it == AudioManager.AUDIOFOCUS_LOSS ||
                it == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                it == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK
            ) {
                store.dispatch(AudioSessionAction.AudioFocusInterrupted())
            }
        }
        store.subscribe {
            onStateChanged()
        }
        onStateChanged()
    }

    private fun onStateChanged() {
        if (previousAudioFocusStatus != store.state.audioSessionState.audioFocusStatus) {
            previousAudioFocusStatus = store.state.audioSessionState.audioFocusStatus
            if (store.state.audioSessionState.audioFocusStatus == AudioFocusStatus.REQUESTING) {
                val mode = audioFocusHandler?.getMode()
                if (mode != MODE_NORMAL && store.state.audioSessionState.audioFocusStatus == AudioFocusStatus.REQUESTING) {
                    store.dispatch(AudioSessionAction.AudioFocusRejected())
                } else {
                    isAudioFocused = audioFocusHandler?.getAudioFocus() == true
                    if (!isAudioFocused) {
                        store.dispatch(AudioSessionAction.AudioFocusRejected())
                    } else {
                        store.dispatch(AudioSessionAction.AudioFocusApproved())
                    }
                }
            }
        } else if (previousCallState != store.state.callState.callingStatus) {
            previousCallState = store.state.callState.callingStatus
            if (store.state.callState.callingStatus == CallingStatus.CONNECTED) {
                isAudioFocused = audioFocusHandler?.getAudioFocus() == true
                if (!isAudioFocused) {
                    store.dispatch(AudioSessionAction.AudioFocusRejected())
                } else {
                    store.dispatch(AudioSessionAction.AudioFocusApproved())
                }
            } else if (store.state.callState.callingStatus == CallingStatus.DISCONNECTING) {
                if (isAudioFocused) {
                    isAudioFocused = audioFocusHandler?.releaseAudioFocus() == false
                }
            }
        }
    }

    fun stop() {
        audioFocusHandler?.onFocusChange = null
    }
}
