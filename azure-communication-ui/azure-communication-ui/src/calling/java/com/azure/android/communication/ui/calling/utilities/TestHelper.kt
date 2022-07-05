// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import com.azure.android.communication.ui.calling.presentation.VideoViewManager
import com.azure.android.communication.ui.calling.service.sdk.CallingSDK

internal object TestHelper {
    var customCallingSDK: CallingSDK? = null
    var videoViewManager: VideoViewManager? = null
}
