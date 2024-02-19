// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.AudioSessionAction
import com.azure.android.communication.ui.calling.redux.state.AudioFocusStatus
import com.azure.android.communication.ui.calling.redux.state.AudioSessionState

internal interface AudioSessionReducer : Reducer<AudioSessionState>

internal class AudioSessionStateReducerImpl :
    AudioSessionReducer {
    override fun reduce(
        state: AudioSessionState,
        action: Action,
    ): AudioSessionState {
        return when (action) {
            is AudioSessionAction.AudioFocusApproved -> {
                state.copy(audioFocusStatus = AudioFocusStatus.APPROVED)
            }
            is AudioSessionAction.AudioFocusRejected -> {
                state.copy(audioFocusStatus = AudioFocusStatus.REJECTED)
            }
            is AudioSessionAction.AudioFocusInterrupted -> {
                state.copy(audioFocusStatus = AudioFocusStatus.INTERRUPTED)
            }
            is AudioSessionAction.AudioFocusRequesting -> {
                state.copy(audioFocusStatus = AudioFocusStatus.REQUESTING)
            }
            else -> state
        }
    }
}
