// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk.ext

import com.azure.android.communication.calling.CallClientOptions
import com.azure.android.communication.calling.CallDiagnosticsOptions
import com.azure.android.communication.ui.calling.logger.Logger

internal fun CallClientOptions.getOrCreateDiagnostics(): CallDiagnosticsOptions {
    if (this.diagnostics == null) {
        this.diagnostics = CallDiagnosticsOptions()
    }
    return this.diagnostics
}

internal fun CallClientOptions.setTags(
    tags: Array<String>?,
    logger: Logger?,
): CallClientOptions {
    tags?.let { this.getOrCreateDiagnostics().also { it.tags = it.tags + tags } }
    logger?.let {
        this.diagnostics.tags.forEach {
            logger.debug("diagnostic tag element: $it")
        }
    }
    return this
}
