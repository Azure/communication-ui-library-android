// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.redux.state.ErrorState
import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
internal class CallCompositeEventsHandlerTests : ACSBaseTestCoroutine() {

    @Test
    fun errorHandler_onStateChange_andAdnRemoveErrorHandler_callsNothing() = runScopedTest {

        // arrange
        val appState = ReduxState.createWithParams("", false, false)
            .copy(
                errorState = ErrorState(null, null)
            )

        val handler1 = mock<CallCompositeEventHandler<CallCompositeErrorEvent>> { }
        val handler2 = mock<CallCompositeEventHandler<CallCompositeErrorEvent>> { }

        val configuration = CallCompositeConfiguration()
        configuration.callCompositeEventsHandler.addOnErrorEventHandler(handler1)
        Assert.assertSame(
            handler1,
            configuration.callCompositeEventsHandler.getOnErrorHandlers().first()
        )
        Assert.assertEquals(1, configuration.callCompositeEventsHandler.getOnErrorHandlers().count())

        configuration.callCompositeEventsHandler.addOnErrorEventHandler(handler2)
        Assert.assertTrue(
            configuration.callCompositeEventsHandler.getOnErrorHandlers().contains(handler2)
        )

        configuration.callCompositeEventsHandler.removeOnErrorEventHandler(handler1)
        Assert.assertEquals(1, configuration.callCompositeEventsHandler.getOnErrorHandlers().count())
        configuration.callCompositeEventsHandler.removeOnErrorEventHandler(handler1)
        Assert.assertEquals(1, configuration.callCompositeEventsHandler.getOnErrorHandlers().count())
        configuration.callCompositeEventsHandler.removeOnErrorEventHandler(handler2)
        Assert.assertEquals(0, configuration.callCompositeEventsHandler.getOnErrorHandlers().count())
    }
}
