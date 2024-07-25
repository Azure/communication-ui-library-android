// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

/* <RTT_POC> */
package com.azure.android.communication.ui.calling.redux.state


internal data class RttState(
    val isRttActive: Boolean = false,
    val messages: List<RttMessage> = emptyList()
)

internal data class RttMessage(
    val message: String,
    val participantID: String) {
    val isFinalized = message.endsWith("\n")
    val prettyMessage: String by lazy {
        val builder = StringBuilder()
        for (c in message.toCharArray()) {
            when (c) {
                '\n' -> {
                    // Do Nothing, we don't need to output \n
                }
                '\b' -> {
                    // Rewind
                    builder.setLength(Math.max(0, builder.length - 1))
                }
                else -> {
                    builder.append(c)
                }
            }
        }
        builder.toString()
    }
}
/* </RTT_POC> */
