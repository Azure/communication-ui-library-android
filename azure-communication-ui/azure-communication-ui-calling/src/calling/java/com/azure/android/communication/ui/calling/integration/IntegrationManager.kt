// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.integration

import com.azure.android.communication.ui.calling.implementation.CallingIntegrationBridgeImpl

internal class IntegrationManager(
    var integrationBridge: CallingIntegrationBridgeImpl? = null,
) {
    fun onCallStarted() {
        integrationBridge?.let { bridge ->
            bridge.callStartedEventHandlers.forEach { handler -> handler.handle(null) }
        }
    }
}
