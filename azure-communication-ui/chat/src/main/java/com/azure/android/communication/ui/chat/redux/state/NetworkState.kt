// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.state

import org.threeten.bp.OffsetDateTime

internal enum class NetworkStatus {
    CONNECTED,
    DISCONNECTED,
}

internal data class NetworkState(
    val networkStatus: NetworkStatus,
    // last native chat sdk notification received offset date time
    val disconnectOffsetDateTime: OffsetDateTime?,
)
