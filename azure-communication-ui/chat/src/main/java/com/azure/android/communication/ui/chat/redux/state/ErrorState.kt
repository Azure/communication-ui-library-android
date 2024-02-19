// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.state

import com.azure.android.communication.ui.chat.error.FatalError
import com.azure.android.communication.ui.chat.models.ChatCompositeErrorEvent

internal data class ErrorState(
    val fatalError: FatalError?,
    val chatCompositeErrorEvent: ChatCompositeErrorEvent?,
)
