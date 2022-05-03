// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.helper

import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlin.coroutines.CoroutineContext

internal class TestContextProvider : CoroutineContextProvider() {
    val testCoroutineDispatcher = TestCoroutineDispatcher()
    override val Main: CoroutineContext = testCoroutineDispatcher
    override val IO: CoroutineContext = testCoroutineDispatcher
    override val Default: CoroutineContext = testCoroutineDispatcher
}
