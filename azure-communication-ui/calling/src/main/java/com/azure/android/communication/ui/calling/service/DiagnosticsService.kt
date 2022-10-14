// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service

import androidx.lifecycle.LifecycleCoroutineScope
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.models.CallCompositeDiagnosticsInfo
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.setDiagnosticsInfo
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal interface DiagnosticsService {
    fun start(lifecycleScope: LifecycleCoroutineScope)
}

internal class DiagnosticsServiceImpl(
    private val store: Store<ReduxState>,
    callComposite: CallComposite,
) : DiagnosticsService {

    private val diagnosticsInfo = CallCompositeDiagnosticsInfo()

    init {
        callComposite.setDiagnosticsInfo(diagnosticsInfo)
    }

    override fun start(lifecycleScope: LifecycleCoroutineScope) {
        lifecycleScope.launch {
            store.getStateFlow().collect {
                diagnosticsInfo.lastKnownCallId = it.callState.callId
            }
        }
    }
}
