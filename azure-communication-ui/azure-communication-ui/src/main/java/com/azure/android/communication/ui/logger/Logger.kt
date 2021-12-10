// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.logger

internal interface Logger {
    fun info(message: String)
    fun debug(message: String)
    fun warning(message: String)
    fun error(message: String, error: Throwable?)
}
