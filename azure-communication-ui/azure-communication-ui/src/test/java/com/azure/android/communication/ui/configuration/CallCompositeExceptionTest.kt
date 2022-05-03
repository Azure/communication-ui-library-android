// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

import com.azure.android.communication.ui.CallCompositeException
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class CallCompositeExceptionTest {

    @Test
    fun callCompositeConfiguration_errorHandling() {
        val classObj = CallCompositeException::class.java
        CallCompositeConfiguration.putConfig(0, CallCompositeConfiguration())

        val ex = assertThrows(classObj, ::getCallCompositeConfig)
        assertThat(
            "invalid type: ${ex.javaClass.simpleName}",
            ex.javaClass.simpleName == classObj.simpleName
        )
        assertThat(
            "invalid message, expecting: ${ex.message}",
            ex.message?.startsWith("This ID is not valid, and no entry exists in the map") ?: false
        )
    }

    private fun getCallCompositeConfig() = CallCompositeConfiguration.getConfig(1)
}