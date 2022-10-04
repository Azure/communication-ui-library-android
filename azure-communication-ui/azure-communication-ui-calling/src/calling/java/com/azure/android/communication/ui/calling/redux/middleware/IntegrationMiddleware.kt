// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.middleware

import com.azure.android.communication.ui.calling.integration.IntegrationManager
import com.azure.android.communication.ui.calling.redux.Dispatch
import com.azure.android.communication.ui.calling.redux.Middleware
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState

internal interface IntegrationMiddleware

internal class IntegrationMiddlewareImpl(
    private val integrationManager: IntegrationManager,
) :
    Middleware<ReduxState>,
    IntegrationMiddleware {
    override fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
        { action: Action ->

            when (action) {

                // TODO: move it to another middleware when implementing CallState Change notification
                is CallingAction.StateUpdated -> {
                    if (action.callingState == CallingStatus.CONNECTED) {
                        integrationManager.onCallStarted()
                    }
                }
            }

            next(action)
        }
    }
}
