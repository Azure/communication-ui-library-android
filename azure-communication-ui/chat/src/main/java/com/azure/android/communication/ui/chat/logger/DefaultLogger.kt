// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.logger

import android.util.Log

internal class DefaultLogger : Logger {
    private val tag = "communication.ui.chat"

    override fun info(
        message: String,
        throwable: Throwable?,
    ) {
        Log.i(tag, message, throwable)
    }

    override fun debug(
        message: String,
        throwable: Throwable?,
    ) {
        Log.d(tag, message, throwable)
    }

    override fun warning(
        message: String,
        throwable: Throwable?,
    ) {
        Log.w(tag, message, throwable)
    }

    override fun error(
        message: String,
        error: Throwable?,
    ) {
        Log.e(tag, message, error)
    }
}
