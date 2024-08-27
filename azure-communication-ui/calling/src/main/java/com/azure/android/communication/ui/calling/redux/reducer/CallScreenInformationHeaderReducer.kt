// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <CUSTOM_CALL_HEADER> */
package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallScreenInformationHeaderAction
import com.azure.android.communication.ui.calling.redux.state.CallScreenInformationHeaderState

internal interface CallScreenInformationHeaderReducer : Reducer<CallScreenInformationHeaderState>

internal class CallScreenInformationHeaderReducerImpl : CallScreenInformationHeaderReducer {
    override fun reduce(state: CallScreenInformationHeaderState, action: Action): CallScreenInformationHeaderState {
        return when (action) {
            is CallScreenInformationHeaderAction.UpdateTitle -> {
                state.copy(title = action.title)
            }
            is CallScreenInformationHeaderAction.UpdateSubtitle -> {
                state.copy(subtitle = action.subtitle)
            }
            else -> state
        }
    }
}

/* </CUSTOM_CALL_HEADER> */
