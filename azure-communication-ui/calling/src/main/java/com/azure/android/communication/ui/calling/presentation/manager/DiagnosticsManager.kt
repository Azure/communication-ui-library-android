// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service

import androidx.lifecycle.LifecycleCoroutineScope
import com.azure.android.communication.ui.calling.models.CallCompositeDiagnosticsInfo
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal interface DiagnosticsManager {
    fun start(lifecycleScope: LifecycleCoroutineScope)
    val diagnosticsInfo: CallCompositeDiagnosticsInfo
}

internal class DiagnosticsManagerImpl(
    private val store: Store<ReduxState>,
) : DiagnosticsManager {

    override val diagnosticsInfo = CallCompositeDiagnosticsInfo()

    override fun start(lifecycleScope: LifecycleCoroutineScope) {
        lifecycleScope.launch {
            store.getStateFlow().collect {
                diagnosticsInfo.lastKnownCallId = it.callState.callId
            }
        }
    }
}
