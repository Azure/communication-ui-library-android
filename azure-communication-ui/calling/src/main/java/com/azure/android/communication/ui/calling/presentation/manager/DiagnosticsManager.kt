// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.models.CallCompositeDiagnosticsInfo
import com.azure.android.communication.ui.calling.models.buildCallCompositeDiagnosticsInfo
import com.azure.android.communication.ui.calling.models.setCallId
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal interface DiagnosticsManager {
    fun start(coroutineScope: CoroutineScope)
    val diagnosticsInfo: CallCompositeDiagnosticsInfo
}

internal class DiagnosticsManagerImpl(
    private val store: Store<ReduxState>,
) : DiagnosticsManager {

    override var diagnosticsInfo = buildCallCompositeDiagnosticsInfo()

    override fun start(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            store.getStateFlow().collect {
                if (!it.callState.callId.isNullOrEmpty()) {
                    val newDiagnosticsInfo = buildCallCompositeDiagnosticsInfo()
                    newDiagnosticsInfo.setCallId(it.callState.callId)
                    diagnosticsInfo = newDiagnosticsInfo
                }
            }
        }
    }
}
