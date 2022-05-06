// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.CallingEventHandler
import com.azure.android.communication.ui.configuration.events.CommunicationUIErrorEvent
import com.azure.android.communication.ui.redux.state.AppReduxState
import com.azure.android.communication.ui.redux.state.ErrorState
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
internal class CallCompositeEventsHandlerTests : ACSBaseTestCoroutine() {

    @Test
    fun errorHandler_onStateChange_andAdnRemoveErrorHandler_callsNothing() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("")
            appState.errorState = ErrorState(null, null)

            val handler1 = mock<CallingEventHandler<CommunicationUIErrorEvent>> { }
            val handler2 = mock<CallingEventHandler<CommunicationUIErrorEvent>> { }

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.setOnErrorHandler(handler1)
            Assert.assertSame(
                handler1,
                configuration.callCompositeEventsHandler.getOnErrorHandler()
            )

            configuration.callCompositeEventsHandler.setOnErrorHandler(handler2)
            Assert.assertSame(
                handler2,
                configuration.callCompositeEventsHandler.getOnErrorHandler()
            )

            configuration.callCompositeEventsHandler.setOnErrorHandler(null)
            Assert.assertNull(configuration.callCompositeEventsHandler.getOnErrorHandler())
        }
}
