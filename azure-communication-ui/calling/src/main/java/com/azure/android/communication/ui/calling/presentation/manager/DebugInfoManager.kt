// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.models.CallCompositeDebugInfo
import com.azure.android.communication.ui.calling.models.buildCallCompositeDebugInfo
import com.azure.android.communication.ui.calling.models.setCallId
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal interface DebugInfoManager {
    fun start(coroutineScope: CoroutineScope)
    val debugInfo: CallCompositeDebugInfo
}

internal class DebugInfoManagerImpl(
    private val store: Store<ReduxState>,
) : DebugInfoManager {

    override var debugInfo = buildCallCompositeDebugInfo()

    override fun start(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            store.getStateFlow().collect {
                if (!it.callState.callId.isNullOrEmpty()) {
                    val newDebugInfo = buildCallCompositeDebugInfo()
                    newDebugInfo.setCallId(it.callState.callId)
                    debugInfo = newDebugInfo
                }
            }
        }
    }
}
