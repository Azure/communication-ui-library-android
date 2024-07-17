// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

/* <RTT_POC> */
package com.azure.android.communication.ui.calling.redux.state

internal data class RttState(
    val isRttActive: Boolean = false,
    val localSequenceNumber: Int = 0,
    val messages: List<String> = emptyList())
/* </RTT_POC> */