// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

import android.widget.ImageButton

// This class is to access CustomButtonConfiguration.setOnFieldUpdatedListener
internal class CustomButtonConfigurationProxy(
    val customButtonState: CallCompositeCustomButtonViewData,
    val imageButton: ImageButton,
) {
    fun setOnFieldUpdatedListener(listener: (CallCompositeCustomButtonViewData, ImageButton) -> Unit) {
        customButtonState.setOnFieldUpdatedListener {
            listener(customButtonState, imageButton)
        }
    }
}
