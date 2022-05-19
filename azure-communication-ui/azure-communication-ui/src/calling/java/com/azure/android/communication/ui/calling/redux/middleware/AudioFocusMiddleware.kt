// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.middleware

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.azure.android.communication.ui.calling.redux.Dispatch
import com.azure.android.communication.ui.calling.redux.Middleware
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.ReduxState

internal interface AudioFocusMiddleware

// Track Manage AudioFocus with Middleware
internal class AudioFocusMiddlewareImpl(
    private val context : Context,
    private val dispatch: (Action) -> Unit
) :
    Middleware<ReduxState>,
    AudioFocusMiddleware, AudioManager.OnAudioFocusChangeListener {

    private var currentFocus = AudioManager.AUDIOFOCUS_NONE
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    @RequiresApi(Build.VERSION_CODES.O)
    private val audioFocusRequest26 = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setOnAudioFocusChangeListener(this).build()

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAudioFocus26() : Boolean {
        return audioManager.requestAudioFocus(audioFocusRequest26) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    override fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
        { action: Action ->
            if (action is CallingAction.CallStartRequested || action is CallingAction.ResumeRequested) {
                var result = false

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    result = getAudioFocus26()
                }

                if (result) {
                    // Could fetch AudioFocus
                    next(action)
                } else {
                    // Error fetching AudioFocus
                    Toast.makeText(context, action.toString(), Toast.LENGTH_SHORT).show()
                }


            } else {
                next(action)
            }
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        this.currentFocus = focusChange
        // Todo: AudioFocus can be resumed as well (e.g. transient is temporary, we will get back.
        // I.e. like how spotify can continue playing after a call is done.
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS
            || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
            || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
          dispatch(CallingAction.HoldRequested())
        }
    }
}
