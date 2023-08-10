// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package chatdemoapp

import java.util.concurrent.Callable

// For passing a fixed token (no refresh)
class CachedTokenFetcher(private val token: String) : Callable<String> {
    override fun call() = token
}
