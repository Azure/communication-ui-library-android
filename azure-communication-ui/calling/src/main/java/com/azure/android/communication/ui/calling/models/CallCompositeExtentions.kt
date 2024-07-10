// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

import android.content.Context

internal class CallCompositeExtentions

internal fun CallCompositeCallScreenControlBarOptions.getCustomButtons(): List<CallCompositeButtonOptions>? {
    return this.customButtons
}

internal fun createButtonClickEvent(
    context: Context,
    buttonOptions: CallCompositeButtonOptions,
): CallCompositeButtonClickEvent {
    return CallCompositeButtonClickEvent(context, buttonOptions)
}
