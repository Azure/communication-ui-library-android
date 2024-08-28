// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <CUSTOM_CALL_HEADER> */
package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.CallScreenInfoHeaderAction
import com.azure.android.communication.ui.calling.redux.state.ReduxState

internal class CallScreenInfoHeaderManager(private val store: Store<ReduxState>) {
    fun updateTitle(title: String) {
        store.dispatch(CallScreenInfoHeaderAction.UpdateTitle(title))
    }

    fun updateSubtitle(subtitle: String) {
        store.dispatch(CallScreenInfoHeaderAction.UpdateSubtitle(subtitle))
    }
}
/* </CUSTOM_CALL_HEADER> */
