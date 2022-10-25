// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.sdk

import com.azure.android.communication.ui.chat.models.ParticipantTimestampInfoModel
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.ParticipantAction
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class DispatchTypingIndicatorReceived(
    private val dispatch: Dispatch,
    private val coroutineContextProvider: CoroutineContextProvider
) {
    private lateinit var timeoutScope: CoroutineScope

    fun dispatch(participantTimestampInfoModel: ParticipantTimestampInfoModel) {
        dispatch(ParticipantAction.TypingIndicatorReceived(participantTimestampInfoModel))
        if (::timeoutScope.isInitialized) {
            timeoutScope.cancel()
        }
        timeoutScope = CoroutineScope((coroutineContextProvider.Default))
        timeoutScope.launch {
            delay(TYPING_INDICATOR_TIMEOUT)
            dispatch(ParticipantAction.TypingIndicatorClear(participantTimestampInfoModel))
        }
    }

    fun cancel() = timeoutScope.cancel()

    companion object {
        private const val TYPING_INDICATOR_TIMEOUT = 10000L
    }
}
