// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

import com.azure.android.communication.ui.calling.presentation.manager.CallTimerAPI

internal fun CallCompositeCallDurationTimer.setManager(callTimerAPI: CallTimerAPI) {
    this.callTimerAPI = callTimerAPI
}
