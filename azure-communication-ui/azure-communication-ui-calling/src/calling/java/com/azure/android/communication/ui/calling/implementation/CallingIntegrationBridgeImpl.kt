// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.implementation

import com.azure.android.communication.ui.calling.CallCompositeBuilder
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.IntegrationBridgeProxy
import com.azure.android.communication.ui.calling.di.DependencyInjectionContainer
import com.azure.android.communication.ui.calling.models.CallCompositeOverlayBuilder
import com.azure.android.communication.ui.calling.models.CallCompositeOverlayOptions
import com.azure.android.communication.ui.calling.redux.action.ExternalOverlayAction

class CallingIntegrationBridgeImpl(
    callCompositeBuilder: CallCompositeBuilder,
) {

    internal lateinit var diContainer: DependencyInjectionContainer
    internal val callStartedEventHandlers = mutableSetOf<CallCompositeEventHandler<*>>()

    init {
        IntegrationBridgeProxy.setBridge(callCompositeBuilder, this)
    }

    // TODO: replace with callComposite.addOnCallStateChangeEventHandler(handler: CallCompositeEventHandler<CallCompositeState>)
    fun addOnCallStaredEventHandler(handler: CallCompositeEventHandler<*>) {
        callStartedEventHandlers.add(handler)
    }
    fun removeOnCallStaredEventHandler(handler: CallCompositeEventHandler<*>) {
        callStartedEventHandlers.remove(handler)
    }

    // TODO: remove setOverlay and removeOverlay as those are defined in CallComposite
    fun setOverlay(viewBuilder: CallCompositeOverlayBuilder, overlayOptions: CallCompositeOverlayOptions) {
        diContainer.appStore.dispatch(ExternalOverlayAction.SetOverlay(viewBuilder))
    }

    fun removeOverlay() {
        diContainer.appStore.dispatch(ExternalOverlayAction.RemoveOverlay())
    }
}
