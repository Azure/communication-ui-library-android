// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

import com.azure.android.communication.ui.calling.models.CallCompositeOverlayBuilder

internal sealed class ExternalOverlayAction : Action {
    class SetOverlay(val viewBuilder: CallCompositeOverlayBuilder) : ExternalOverlayAction()
    class RemoveOverlay : ExternalOverlayAction()
}
