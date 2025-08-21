// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallScreenInfoHeaderAction
import com.azure.android.communication.ui.calling.redux.state.CallScreenInfoHeaderState

internal interface CallScreenInformationHeaderReducer : Reducer<CallScreenInfoHeaderState>

internal class CallScreenInformationHeaderReducerImpl : CallScreenInformationHeaderReducer {
    override fun reduce(state: CallScreenInfoHeaderState, action: Action): CallScreenInfoHeaderState {
        return when (action) {
            is CallScreenInfoHeaderAction.UpdateTitle -> {
                state.copy(title = action.title)
            }
            is CallScreenInfoHeaderAction.UpdateSubtitle -> {
                state.copy(subtitle = action.subtitle)
            }
            /* <CALL_START_TIME> */
            is CallScreenInfoHeaderAction.UpdateShowCallDuration -> {
                state.copy(showCallDuration = action.showCallDuration)
            }
            /* </CALL_START_TIME> */
            else -> state
        }
    }
}
