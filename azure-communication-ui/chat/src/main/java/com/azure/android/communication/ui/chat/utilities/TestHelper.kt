// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.utilities

import com.azure.android.communication.ui.chat.service.sdk.ChatSDK

/**
 * This singleton provides a shared global state that our in-process tests (i.e. instrumented on-device unit tests)
 * may use to inject their own implementations of dependencies into the library.
 */
internal object TestHelper {
    /**
     * Allows injecting a custom [ChatSDK] implementation.
     * E.g. a test may inject an in-memory implementation to avoid hitting real servers and simulate remote events.
     */
    var chatSDK: ChatSDK? = null

    /**
     * Allows injecting custom set of dispatchers used by the store reducer and within the library.
     * Tests will pass along their TestDispatcher in order to sequence events properly.
     */
    var coroutineContextProvider: CoroutineContextProvider? = null
}
