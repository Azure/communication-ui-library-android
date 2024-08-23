// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

import android.content.Context

internal fun createButtonClickEvent(
    context: Context,
    buttonOptions: CallCompositeButtonOptions,
): CallCompositeButtonClickEvent {
    return CallCompositeButtonClickEvent(context, buttonOptions)
}

internal fun createCustomButtonClickEvent(
    context: Context,
    buttonOptions: CallCompositeCustomButtonOptions,
): CallCompositeCustomButtonClickEvent {
    return CallCompositeCustomButtonClickEvent(context, buttonOptions)
}
