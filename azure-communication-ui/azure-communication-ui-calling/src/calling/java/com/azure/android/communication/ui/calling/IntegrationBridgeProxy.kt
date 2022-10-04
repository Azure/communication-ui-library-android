// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import com.azure.android.communication.ui.calling.implementation.CallingIntegrationBridgeImpl

internal class IntegrationBridgeProxy {
    companion object {

        fun setBridge(callCompositeBuilder: CallCompositeBuilder, integrationBridge: CallingIntegrationBridgeImpl) {
            callCompositeBuilder.setIntegrationBridge(integrationBridge)
        }
    }
}
