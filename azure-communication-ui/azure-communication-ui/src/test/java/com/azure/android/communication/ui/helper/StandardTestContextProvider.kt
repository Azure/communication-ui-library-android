// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.helper

import com.azure.android.communication.ui.utilities.CoroutineContextProvider
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlin.coroutines.CoroutineContext

internal class StandardTestContextProvider : CoroutineContextProvider() {
    private val testCoroutineDispatcher = StandardTestDispatcher()
    override val Main: CoroutineContext = testCoroutineDispatcher
    override val IO: CoroutineContext = testCoroutineDispatcher
    override val Default: CoroutineContext = testCoroutineDispatcher
}
