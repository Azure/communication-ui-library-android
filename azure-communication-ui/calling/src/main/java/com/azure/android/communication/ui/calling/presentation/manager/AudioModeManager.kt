// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.MODE_IN_COMMUNICATION
import android.media.AudioManager.MODE_NORMAL
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.flow.collect

internal class AudioModeManager(
    private val store: Store<ReduxState>,
    context: Context,
) {
    private val audioManager by lazy { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    suspend fun start() {
        store.getStateFlow().collect {
            if (audioManager.mode != MODE_IN_COMMUNICATION && it.callState.callingStatus == CallingStatus.CONNECTED) {
                // to fix samsung device audio issue
                // MODE_IN_COMMUNICATION is used to let the system know that the app is in a VOIP call
                audioManager?.mode = MODE_IN_COMMUNICATION
            }
            if (it.callState.callingStatus == CallingStatus.LOCAL_HOLD) {
                // To fix audio focus retrieval after returning from other call, we need to
                // assign ourselves as mode_normal when we go to hold
                audioManager?.mode = MODE_NORMAL
            }
        }
    }

    fun onDestroy() {
        audioManager.mode = MODE_NORMAL
    }
}
