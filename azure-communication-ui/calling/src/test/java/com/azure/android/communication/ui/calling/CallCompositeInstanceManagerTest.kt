// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import org.hamcrest.MatcherAssert
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class CallCompositeInstanceManagerTest {
    @Test
    fun callCompositeInstanceManager_errorHandling() {
        val classObj = CallCompositeException::class.java
        CallCompositeInstanceManager.putCallComposite(0, CallCompositeBuilder().build())

        val ex = Assert.assertThrows(classObj, ::getCallCompositeConfig)
        MatcherAssert.assertThat(
            "invalid type: ${ex.javaClass.simpleName}",
            ex.javaClass.simpleName == classObj.simpleName
        )
        MatcherAssert.assertThat(
            "invalid message, expecting: ${ex.message}",
            ex.message?.startsWith("This ID is not valid, and no entry exists in the map")
                ?: false
        )

        MatcherAssert.assertThat(
            "Invalid cause, expecting: ${ex.cause}",
            ex.cause is IllegalStateException
        )
    }

    private fun getCallCompositeConfig() = CallCompositeInstanceManager.getCallComposite(1)
}
