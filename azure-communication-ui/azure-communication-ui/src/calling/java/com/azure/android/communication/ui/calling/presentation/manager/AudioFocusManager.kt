// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.flow.collect

internal abstract class AudioFocusHandler : AudioManager.OnAudioFocusChangeListener {
    var onFocusChange: ((Int) -> Unit)? = null

    override fun onAudioFocusChange(focusChange: Int) {
        onFocusChange?.let { it(focusChange) }
    }

    abstract fun getAudioFocus(): Boolean
    abstract fun releaseAudioFocus(): Boolean
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

    override fun getAudioFocus() = audioManager.requestAudioFocus(
        this,
        AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
    ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED

    override fun releaseAudioFocus(): Boolean =
        audioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
}

internal class AudioFocusManager(
    private val store: Store<ReduxState>,
    private val applicationContext: Context,
) {
    private var audioFocusHandler: AudioFocusHandler? = null

    init {
        audioFocusHandler = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusHandler26(applicationContext)
        } else {
            AudioFocusHandlerLegacy(applicationContext)
        }

        audioFocusHandler?.onFocusChange = {
            // Todo: AudioFocus can be resumed as well (e.g. transient is temporary, we will get back.
            // I.e. like how spotify can continue playing after a call is done.
            if (it == AudioManager.AUDIOFOCUS_LOSS ||
                it == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                it == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK
            ) {
                store.dispatch(CallingAction.HoldRequested())
            }
        }
    }

    suspend fun start() {
        store.getStateFlow().collect {
            if ( it.callState.callingStatus == CallingStatus.CONNECTED) {
                val result = audioFocusHandler?.getAudioFocus()
                if (result == false) {
                    Toast.makeText(applicationContext, "Failure to get focus", Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (it.callState.callingStatus == CallingStatus.DISCONNECTED) {
                audioFocusHandler?.releaseAudioFocus()
            }
        }
    }
}
