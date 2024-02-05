// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.logger

import android.util.Log
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.mockito.Mockito.mockStatic

internal class LoggerUnitTest {
    @Test
    fun log_when_nullLoggerInfoCalled_then_writesLog() {
        // arrange
        var loggerInvocation = 0
        val mockLog = mockStatic(Log::class.java)
        mockLog.`when`<Any> { Log.d("communication.ui", "message") }.then {
            loggerInvocation++
            0
        }
        val logger: Logger = DefaultLogger()

        // act
        logger.debug("message")

        // assert
        assertEquals(1, loggerInvocation)
    }
}
