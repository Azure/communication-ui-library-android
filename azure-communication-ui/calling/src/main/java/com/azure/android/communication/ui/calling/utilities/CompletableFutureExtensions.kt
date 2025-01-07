// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import java.util.concurrent.CompletableFuture

internal fun <T> java9.util.concurrent.CompletableFuture<T>.toJavaUtil(): CompletableFuture<T> {
    val future = CompletableFuture<T>()
    whenComplete { aVoid, throwable ->
        if (throwable != null) {
            future.completeExceptionally(throwable)
        } else {
            future.complete(aVoid)
        }
    }

    return future
}