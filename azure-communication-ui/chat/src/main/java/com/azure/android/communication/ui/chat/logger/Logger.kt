// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.logger

internal interface Logger {
    fun info(
        message: String,
        throwable: Throwable? = null,
    )

    fun debug(
        message: String,
        throwable: Throwable? = null,
    )

    fun warning(
        message: String,
        throwable: Throwable? = null,
    )

    fun error(
        message: String,
        error: Throwable?,
    )
}
