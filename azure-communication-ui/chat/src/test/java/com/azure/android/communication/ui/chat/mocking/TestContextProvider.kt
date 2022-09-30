package com.azure.android.communication.ui.chat.mocking

import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider
import kotlinx.coroutines.test.TestDispatcher
import kotlin.coroutines.CoroutineContext

internal class TestContextProvider(testCoroutineDispatcher: TestDispatcher) :
    CoroutineContextProvider() {
    override val Main: CoroutineContext = testCoroutineDispatcher
    override val IO: CoroutineContext = testCoroutineDispatcher
    override val Default: CoroutineContext = testCoroutineDispatcher
    override val SingleThreaded: CoroutineContext = testCoroutineDispatcher
}
